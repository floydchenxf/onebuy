package com.floyd.onebuy.ui.activity;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.biz.manager.ImagerInfoManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.tools.DataBaseUtils;
import com.floyd.onebuy.biz.tools.FileTools;
import com.floyd.onebuy.biz.tools.ThumbnailUtils;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.graphic.CropImageActivity;
import com.floyd.onebuy.view.LeftDownPopupWindow;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by chenxiaofeng on 16/9/2.
 */
public class PrizeShareSubmitActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "PrizeShareActivity";
    public static final String LSSUE_ID = "lssue_id";
    public static final String PRO_ID = "pro_id";

    private static final int CODE_GALLERY_REQUEST = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int CROP_PICTURE_REQUEST = 3;
    private static final int CODE_UPLOAD_VIDEO = 4;

    private String tempImage = "image_temp";
    private String tempVideo = "video_temp.mp4";
    private String tempImageCompress = "image_tmp";
    private int avatorSize = 720;
    private String avatorTmp = "avator_tmp.jpg";

    private TextView submitButton;
    private EditText showTitleView;
    private EditText showContentView;
    private RadioButton typePicView;
    private RadioButton typeVideoView;
    private View typePicLayout, typeVideoLayout;
    private LinearLayout picLayout, videoLayout;
    private TextView uploadButton;
    private LeftDownPopupWindow popupWindow;
    private TextView choosePhoneView;
    private TextView takePhoneView;
    private TextView cancelView;
    private int typeId; //1 图片  2 视频

    private List<String> urlList = new ArrayList<String>();
    private File tempFile;

    private Dialog loadingDialog;
    private ImageLoader mImageLoader;
    private long userId;
    private long proId;
    private long lssueId;
    private float onedp;
    private String videoUrl;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prize_share_submit);
        onedp = this.getResources().getDimension(R.dimen.one_dp);
        typeId = 1;
        loadingDialog = DialogCreator.createDataLoadingDialog(this);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        userId = LoginManager.getLoginInfo(this).ID;
        proId = getIntent().getLongExtra(PRO_ID, 0l);
        lssueId = getIntent().getLongExtra(LSSUE_ID, 0l);
        findViewById(R.id.title_back).setOnClickListener(this);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("晒单");
        titleNameView.setVisibility(View.VISIBLE);

        submitButton = (TextView) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(this);
        showTitleView = (EditText) findViewById(R.id.show_title_view);
        showContentView = (EditText) findViewById(R.id.show_content_view);
        typePicLayout = findViewById(R.id.type_pic_layout);
        typeVideoLayout = findViewById(R.id.type_video_layout);
        typePicView = (RadioButton) findViewById(R.id.type_pic_view);
        typeVideoView = (RadioButton) findViewById(R.id.type_video_view);
        uploadButton = (TextView) findViewById(R.id.upload_button);
        picLayout = (LinearLayout) findViewById(R.id.pic_add_layout);
        videoLayout = (LinearLayout) findViewById(R.id.video_add_layout);

        picLayout.setVisibility(View.VISIBLE);
        videoLayout.setVisibility(View.GONE);

        uploadButton.setOnClickListener(this);

        typePicView.setOnClickListener(this);
        typePicLayout.setOnClickListener(this);

        typeVideoView.setOnClickListener(this);
        typeVideoLayout.setOnClickListener(this);
        popupWindow = new LeftDownPopupWindow(this);
        popupWindow.initView(R.layout.edit_head_pop, new LeftDownPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                choosePhoneView = (TextView) v.findViewById(R.id.choose_phone);
                takePhoneView = (TextView) v.findViewById(R.id.take_phone);
                cancelView = (TextView) v.findViewById(R.id.cancel_button);

                choosePhoneView.setOnClickListener(PrizeShareSubmitActivity.this);
                takePhoneView.setOnClickListener(PrizeShareSubmitActivity.this);
                cancelView.setOnClickListener(PrizeShareSubmitActivity.this);
            }
        });

    }

    private void hiddenPopup() {
        if (!PrizeShareSubmitActivity.this.isFinishing()) {
            if (popupWindow.isShow()) {
                popupWindow.hidePopUpWindow();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.type_pic_layout:
            case R.id.type_pic_view:
                typeId = 1;
                typePicView.setChecked(true);
                typeVideoView.setChecked(false);
                picLayout.setVisibility(View.VISIBLE);
                videoLayout.setVisibility(View.GONE);
                break;
            case R.id.type_video_layout:
            case R.id.type_video_view:
                typeId = 2;
                typePicView.setChecked(false);
                typeVideoView.setChecked(true);
                picLayout.setVisibility(View.GONE);
                videoLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.upload_button:
                //上传图片
                if (this.isFinishing()) {
                    return;
                }
                if (typeId == 1) {
                    //图片
                    popupWindow.showPopUpWindow();
                } else if (typeId == 2) {
                    //视频
                    Intent intentFromGallery = new Intent();
                    intentFromGallery.setType("video/*");
                    intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intentFromGallery, CODE_UPLOAD_VIDEO);
                }
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
            case R.id.delete_button:
                if (typeId == 1) {
                    final String deleteUrl = (String) view.getTag();
                    urlList.remove(deleteUrl);
                    drawPicLayout(urlList);
                } else if (typeId == 2) {
                    videoUrl = null;
                    drawVideoLayout();
                }
                break;
            case R.id.submit_button:
                String showTitle = showTitleView.getText().toString();
                if (TextUtils.isEmpty(showTitle)) {
                    Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
                    return;
                }

                String showContent = showContentView.getText().toString();
                if (TextUtils.isEmpty(showContent)) {
                    Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder sb = new StringBuilder();
                if (typeId == 1 && urlList != null && !urlList.isEmpty()) {
                    for (String s : urlList) {
                        sb.append(s).append("|");
                    }

                } else if (typeId == 2 && videoUrl != null) {
                    sb.append(videoUrl).append("|");
                }
                int l = sb.toString().length();
                String fillUrl = l > 0 ? sb.toString().substring(0, l - 1) : sb.toString();
                loadingDialog.show();
                ImagerInfoManager.shareImage(userId, lssueId, proId, showTitle, showContent, typeId, fillUrl).startUI(new ApiCallback<Long>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        loadingDialog.dismiss();
                        Toast.makeText(PrizeShareSubmitActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Long l) {
                        loadingDialog.dismiss();
                        PrizeShareSubmitActivity.this.finish();
                        Toast.makeText(PrizeShareSubmitActivity.this, "晒单成功", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;

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
            if (urlList.size() >= 3) {
                Toast.makeText(PrizeShareSubmitActivity.this, "最多上传3个图片", Toast.LENGTH_SHORT).show();
                return;
            }
            loadingDialog.show();
            final File newFile = new File(EnvConstants.imageRootPath, avatorTmp);
            ImagerInfoManager.uploadImage(userId, lssueId, newFile).startUI(new ApiCallback<String>() {
                @Override
                public void onError(int code, String errorInfo) {
                    loadingDialog.dismiss();
                    Toast.makeText(PrizeShareSubmitActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String s) {
                    loadingDialog.dismiss();
                    urlList.add(s);
                    drawPicLayout(urlList);
                }

                @Override
                public void onProgress(int progress) {

                }
            });

            //处理图片

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
                    intent.putExtra("needRotate", false);
                    this.startActivityForResult(intent, CROP_PICTURE_REQUEST);
                }
            }
        } else if (requestCode == CODE_UPLOAD_VIDEO) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }

            Uri uri = data.getData();
            if (uri == null) {
                return;
            }

            if (videoUrl != null) {
                Toast.makeText(PrizeShareSubmitActivity.this, "视频只能上传一个!", Toast.LENGTH_SHORT).show();
                return;
            }

            InputStream in = null;
            byte[] tmpData = null;

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

            FileTools.writeFile(EnvConstants.imageRootPath, tempVideo, tmpData);
            File newFile = new File(EnvConstants.imageRootPath, tempVideo);
            loadingDialog.show();
            ImagerInfoManager.uploadVideo(userId, lssueId, newFile).startUI(new ApiCallback<String>() {
                @Override
                public void onError(int code, String errorInfo) {
                    loadingDialog.dismiss();
                    Toast.makeText(PrizeShareSubmitActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(String s) {
                    loadingDialog.dismiss();
                    PrizeShareSubmitActivity.this.videoUrl = s;
                    drawVideoLayout();
                }

                @Override
                public void onProgress(int progress) {

                }
            });


        }
    }

    private void drawVideoLayout() {
        picLayout.removeAllViews();
        if (videoUrl != null) {
            final int eachWidth = (int) (60 * onedp);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(eachWidth, eachWidth);
            lp.setMargins(0, 0, (int) (10 * onedp), 0);
            final View picItemView = View.inflate(this, R.layout.process_upload_pic_item, null);
            picItemView.setLayoutParams(lp);
            NetworkImageView networkImage = (NetworkImageView) picItemView.findViewById(R.id.task_pic_item);
            networkImage.setDefaultImageResId(R.drawable.tupian);
            final String picUrl = APIConstants.HOST + videoUrl;
            networkImage.setImageUrl(picUrl, mImageLoader);
            networkImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            View markLayout = picItemView.findViewById(R.id.mark_layout);
            ImageView deleteButton = (ImageView) picItemView.findViewById(R.id.delete_button);
            markLayout.setVisibility(View.VISIBLE);
            deleteButton.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(this);
            videoLayout.addView(picItemView);
        }
    }

    private void drawPicLayout(List<String> pics) {
        picLayout.removeAllViews();
        final int eachWidth = (int) (60 * onedp);
        if (pics != null) {
            for (final String pic : pics) {
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(eachWidth, eachWidth);
                lp.setMargins(0, 0, (int) (10 * onedp), 0);
                final View picItemView = View.inflate(this, R.layout.process_upload_pic_item, null);
                picItemView.setLayoutParams(lp);
                NetworkImageView networkImage = (NetworkImageView) picItemView.findViewById(R.id.task_pic_item);
                networkImage.setDefaultImageResId(R.drawable.tupian);
                final String picUrl = APIConstants.HOST + pic;
                networkImage.setImageUrl(picUrl, mImageLoader);
                networkImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                View markLayout = picItemView.findViewById(R.id.mark_layout);
                ImageView deleteButton = (ImageView) picItemView.findViewById(R.id.delete_button);
                markLayout.setVisibility(View.VISIBLE);
                deleteButton.setVisibility(View.VISIBLE);
                deleteButton.setTag(pic);
                deleteButton.setOnClickListener(this);
                picLayout.addView(picItemView);
            }
        }
    }
}
