package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.tools.DataBaseUtils;
import com.yyg365.interestbar.biz.tools.FileTools;
import com.yyg365.interestbar.biz.tools.ImageUtils;
import com.yyg365.interestbar.biz.tools.ThumbnailUtils;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.ui.DialogCreator;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.graphic.CropImageActivity;
import com.yyg365.interestbar.view.LeftDownPopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.UUID;

public class MyInfoActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "MyInfoActivity";

    private static final int CODE_GALLERY_REQUEST = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int CROP_PICTURE_REQUEST = 3;

    private String tempImage = "image_temp";
    private String tempImageCompress = "image_tmp";
    private int avatorSize = 720;
    private String avatorTmp = "avator_tmp.jpg";

    private NetworkImageView headImageView;

    private TextView phoneView;
    private TextView nickNameView;
    private View addressManagerView;

    private ImageLoader mImageLoader;

    private LeftDownPopupWindow popupWindow;

    private TextView choosePhoneView;
    private TextView takePhoneView;
    private TextView cancelView;

    private View phoneLayout;

    private Dialog dataLoadingDialog;

    private File tempFile;
    private float oneDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        TextView titleView = (TextView)findViewById(R.id.title_name);
        titleView.setText("个人资料");
        titleView.setVisibility(View.VISIBLE);
        findViewById(R.id.nick_layout).setOnClickListener(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this);
        headImageView = (NetworkImageView) findViewById(R.id.head_image);
        View headLayout = findViewById(R.id.head_layout);
        headLayout.setOnClickListener(this);

        phoneLayout = findViewById(R.id.phone_layout);
        phoneLayout.setOnClickListener(this);
        phoneView = (TextView) findViewById(R.id.phone_num);
        nickNameView = (TextView) findViewById(R.id.nick);
        addressManagerView = findViewById(R.id.address_layout);
        oneDp = this.getResources().getDimension(R.dimen.one_dp);

        headImageView.setDefaultImageResId(R.drawable.default_head);
        addressManagerView.setOnClickListener(this);
        popupWindow = new LeftDownPopupWindow(this);
        popupWindow.initView(R.layout.edit_head_pop, new LeftDownPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                choosePhoneView = (TextView) v.findViewById(R.id.choose_phone);
                takePhoneView = (TextView) v.findViewById(R.id.take_phone);
                cancelView = (TextView) v.findViewById(R.id.cancel_button);

                choosePhoneView.setOnClickListener(MyInfoActivity.this);
                takePhoneView.setOnClickListener(MyInfoActivity.this);
                cancelView.setOnClickListener(MyInfoActivity.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        UserVO userVO = LoginManager.getLoginInfo(this);
        headImageView.setImageUrl(userVO.getFullPic(), mImageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, MyInfoActivity.this.getResources().getDimension(R.dimen.small_cycle_head_image_size));
            }
        });
        headImageView.setOnClickListener(this);
        nickNameView.setText(userVO.getUserName());
        phoneView.setText(userVO.Mobile);
    }

    @Override
    public void onBackPressed() {
        if (!this.isFinishing() && popupWindow.isShow()) {
            popupWindow.hidePopUpWindow();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.address_layout:
                Intent addressIntent = new Intent(this, AddressManagerActivity.class);
                startActivity(addressIntent);
                break;
            case R.id.nick_layout:
                Intent it = new Intent(this, ChangeNickActivity.class);
                startActivity(it);
                break;
            case R.id.choose_phone:
                Intent intentFromGallery = new Intent();
                intentFromGallery.setType("image/*");
                intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
                break;
            case R.id.take_phone:
                String status = Environment.getExternalStorageState();
                File saveFile = new File(EnvConstants.imageRootPath);

                if (!status.equals(Environment.MEDIA_MOUNTED)) {
                    Toast.makeText(this, R.string.insert_sdcard, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!saveFile.exists()) {
                    saveFile.mkdir();
                }

                tempFile = new File(saveFile.getAbsolutePath() + File.separator + UUID.randomUUID().toString());

                if (saveFile.exists()) {// 判断是否有SD卡
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri uri = Uri.fromFile(tempFile);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, TAKE_PICTURE);
                } else if (saveFile == null || !saveFile.exists()) {
                    Toast.makeText(this, R.string.insert_sdcard, Toast.LENGTH_SHORT).show();
                }
                //拍照
                break;
            case R.id.cancel_button:
                hiddenPopup();
                break;
            case R.id.head_image:
            case R.id.head_layout:
                if (!this.isFinishing()) {
                    popupWindow.showPopUpWindow();
                }
                break;
            case R.id.title_back:
                this.finish();
                break;

            case R.id.phone_layout:
                Intent mobileBindIntent = new Intent(this, MobileBindActivity.class);
                startActivity(mobileBindIntent);
                break;
        }
    }

    private void hiddenPopup() {
        if (!MyInfoActivity.this.isFinishing()) {
            if (popupWindow.isShow()) {
                popupWindow.hidePopUpWindow();
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        hiddenPopup();
        if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent(this, CropImageActivity.class);
                intent.setDataAndType(Uri.fromFile(tempFile), "image/*");// 设置要裁剪的图片
                intent.putExtra("crop", "true");// crop=true
                // 有这句才能出来最后的裁剪页面.
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("outputX", avatorSize);
                intent.putExtra("outputY", avatorSize);
                intent.putExtra("noFaceDetection", true);
                intent.putExtra("return-data", true);
                intent.putExtra("path", avatorTmp);
                intent.putExtra("outputFormat", "JPEG");// 返回格式
                intent.putExtra("needRotate", true);
                this.startActivityForResult(intent, CROP_PICTURE_REQUEST);
            }
        } else if (requestCode == CROP_PICTURE_REQUEST && resultCode == Activity.RESULT_OK) {
            final File newFile = new File(EnvConstants.imageRootPath, avatorTmp);
            dataLoadingDialog.show();

            final UserVO userVO = LoginManager.getLoginInfo(this);
            LoginManager.modifyHead(userVO.ID, newFile).startUI(new ApiCallback<Map<String, String>>() {
                @Override
                public void onError(int code, String errorInfo) {
                    Toast.makeText(MyInfoActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    hiddenPopup();
                    dataLoadingDialog.dismiss();
                }

                @Override
                public void onSuccess(Map<String, String> stringStringMap) {
                    dataLoadingDialog.dismiss();
                    String path = stringStringMap.get("url");
                    if (path != null && path.startsWith(File.separator)) {
                        path = path.substring(1);
                    }
                    String url = APIConstants.HOST+ path;
                    headImageView.setImageUrl(url, mImageLoader, new BitmapProcessor() {
                        @Override
                        public Bitmap processBitmap(Bitmap bitmap) {
                            return ImageUtils.getCircleBitmap(bitmap, 40 * oneDp);
                        }
                    });
                    UserVO userVO = LoginManager.getLoginInfo(MyInfoActivity.this);
                    userVO.Pic = path;
                    LoginManager.saveLoginInfo(MyInfoActivity.this, userVO);
                    hiddenPopup();
                    newFile.delete();
                }

                @Override
                public void onProgress(int progress) {

                }
            });

        } else if (requestCode == CODE_GALLERY_REQUEST) {
            if (!this.isFinishing()) {
                hiddenPopup();
            }

            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    InputStream in = null;
                    String type = "";
                    byte[] tmpData = null;
                    try {
                        in = this.getContentResolver().openInputStream(uri);
                        tmpData = new byte[10];
                        in.read(tmpData);
                        type = ThumbnailUtils.getType(tmpData);
                    } catch (FileNotFoundException e) {
                        Log.w(TAG, e);
                        Log.w(TAG, e);
                    } catch (IOException e) {
                        Log.w(TAG, e);
                        Log.w(TAG, e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                Log.w(TAG, e);
                            }
                        }
                    }

                    if ("GIF".equals(type)) {
                        try {
                            in = this.getContentResolver().openInputStream(
                                    uri);
                            tmpData = new byte[in.available()];
                            in.read(tmpData);
                        } catch (FileNotFoundException e) {
                            Log.w(TAG, e);
                            Log.w(TAG, e);
                        } catch (IOException e) {
                            Log.w(TAG, e);
                            Log.w(TAG, e);
                        } finally {
                            if (in != null) {
                                try {
                                    in.close();
                                } catch (IOException e) {
                                    Log.w(TAG, e);
                                }
                            }
                        }
                        Bitmap bitmap = null;
                        try {
                            bitmap = BitmapFactory.decodeByteArray(tmpData, 0,
                                    tmpData.length);
                        } catch (OutOfMemoryError e) {
                            Log.e(TAG, e.getMessage(), e);
                            bitmap = BitmapFactory.decodeByteArray(tmpData, 0,
                                    tmpData.length);
                        }
                        FileTools.writeBitmap(EnvConstants.imageRootPath,
                                tempImage, bitmap);
                        File newFile = new File(EnvConstants.imageRootPath, tempImage);
                        bitmap = ThumbnailUtils.getImageThumbnail(newFile,
                                avatorSize, avatorSize, tempImageCompress,
                                false);
                        if (bitmap != null) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                    } else {
                        // tempImageCompress 是tmpFile的文件名
                        int orientation = 0;
                        Cursor cursor = null;
                        try {
                            cursor = DataBaseUtils.doContentResolverQueryWrapper(this, uri,
                                    new String[]{MediaStore.Images.ImageColumns.ORIENTATION},
                                    null, null, null);
                            if (cursor != null) {
                                if (cursor.moveToFirst()) {
                                    orientation = cursor.getInt(0);
                                }
                            }
                        } catch (RuntimeException e) {
                            Log.w(TAG, e);
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                                cursor = null;
                            }
                        }

                        Bitmap bitmap = ThumbnailUtils.getImageThumbnailFromAlbum(this, uri, avatorSize, avatorSize, tempImageCompress, orientation);

                        if (bitmap != null) {
                            bitmap.recycle();
                            bitmap = null;
                        }
                    }
                    tmpData = null;

                    File tmpFile = new File(EnvConstants.imageRootPath + File.separator + tempImageCompress);
                    Intent intent = new Intent(this, CropImageActivity.class);
                    intent.setDataAndType(Uri.fromFile(tmpFile), "image/*");// 设置要裁剪的图片
                    intent.putExtra("crop", "true");// crop=true
                    // 有这句才能出来最后的裁剪页面.
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", avatorSize);
                    intent.putExtra("outputY", avatorSize);
                    intent.putExtra("noFaceDetection", true);
                    intent.putExtra("return-data", true);
                    intent.putExtra("path", avatorTmp);
                    intent.putExtra("outputFormat", "JPEG");// 返回格式
                    intent.putExtra("needRotate", true);
                    this.startActivityForResult(intent, CROP_PICTURE_REQUEST);
                }
            }
        }
    }
}
