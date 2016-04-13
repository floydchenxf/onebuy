package com.floyd.onebuy.ui.multiimage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.ui.multiimage.base.PicViewObject;
import com.floyd.onebuy.ui.multiimage.gif.GifFrame;
import com.floyd.onebuy.utils.WXUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

/**
 * Created by floyd on 15-12-6.
 */
public class ImageDetailFragment extends Fragment implements View.OnClickListener,
        ILoadBigImageView, TouchImageView.OnImageTouchListener {
    private static long CURRENT_IMAGE_ID;
    private ImageView mDefaultImageView;
    private TouchImageView mImageView;
    private MutliImageGifView mGifImageView;
    private ImageView mLoadFailedImageView;
    private TextView mLoadFailedTextView;
    private long mImageId = 0;

    private Bitmap mBmp;
    private byte[] mData;

    private Handler handler;
    protected ProgressWheel mDownloadProgressBar;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;
    private PicViewObject picView;

    private View picVoteLayout;
    private TextView picVoteTextView;


    private OnImageFragmentListener mCallback;

    private static HighDefinitionImageLoader mHDImageLoader;


    public static ImageDetailFragment newInstance(PicViewObject picViewObject,
                                                  HighDefinitionImageLoader HDImageLoader) {
        if (mHDImageLoader == null) {
            mHDImageLoader = HDImageLoader;
        }

        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putSerializable(MultiImageActivity.IMAGE_DATA_EXTRA, picViewObject);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE
                || newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnImageFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public static void setCurrentImageId(long currentImageId) {
        CURRENT_IMAGE_ID = currentImageId;
    }

    public void pageChanged(long ImageId) {
		if (mImageView != null) {
			if (mImageView.getDrawable() != null) {
				if (BitmapDrawable.class.isInstance(mImageView.getDrawable())) {
					BitmapDrawable bdmp = (BitmapDrawable) mImageView
							.getDrawable();
					Bitmap bmp = bdmp.getBitmap();
					if (bmp != null) {
						if (!bmp.isRecycled() && mImageView.isZOOMMode()) {
							mImageView.ScaleImage();
						}
					}
				}
			}
		}
		if (mImageId != ImageId && mGifImageView != null) {
			mGifImageView.stopPlay();
		} else if (mGifImageView != null) {
			mGifImageView.startPlay();
		}
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        picView = (PicViewObject) (getArguments() != null ? getArguments()
                .getSerializable(MultiImageActivity.IMAGE_DATA_EXTRA) : null);
        if (picView != null) {
            mImageId = picView.getPicId();
        }
        handler = new Handler(getActivity().getMainLooper());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.image_detail_fragment,
                container, false);

        picVoteLayout = v.findViewById(R.id.vote_pic_layout);
        picVoteTextView = (TextView)v.findViewById(R.id.vote_pic_view);

        if (TextUtils.isEmpty(picView.getExtData())) {
            picVoteLayout.setVisibility(View.GONE);
        }

        picVoteLayout.setVisibility(View.GONE);
        v.findViewById(R.id.image_detail_layout).setOnClickListener(this);
        mImageView = (TouchImageView) v.findViewById(R.id.image_detail_view);
        mImageView.setOnImageTouchListener(this);
        mGifImageView = (MutliImageGifView) v
                .findViewById(R.id.gif_image_detail_view);
        mGifImageView.setOnClickListener(this);

        mDefaultImageView = (ImageView) v
                .findViewById(R.id.image_detail_default_view);

        mDownloadProgressBar = (ProgressWheel) v
                .findViewById(R.id.image_detail_progress);

        if (picView == null) {
            mImageView.setVisibility(View.GONE);
            mGifImageView.setVisibility(View.GONE);
            mDefaultImageView.setVisibility(View.VISIBLE);
            mDownloadProgressBar.setVisibility(View.GONE);
            return v;
        }

        if (picView.getPicType() == PicViewObject.GIF) {
            mImageView.setVisibility(View.GONE);
            mGifImageView.setVisibility(View.VISIBLE);
        } else if (picView.getPicType() == PicViewObject.IMAGE) {
            mImageView.setVisibility(View.VISIBLE);
            mGifImageView.setVisibility(View.GONE);
        }

        mLoadFailedImageView = (ImageView) v
                .findViewById(R.id.image_detail_download_fail_view);
        mLoadFailedTextView = (TextView) v
                .findViewById(R.id.image_detail_download_fail_textview);
        mLoadFailedImageView.setVisibility(View.GONE);
        mLoadFailedTextView.setVisibility(View.GONE);

        if (TextUtils.isEmpty(picView.getPicUrl())
                && TextUtils.isEmpty(picView.getPicPreViewUrl())) {
            mImageView.setVisibility(View.GONE);
            mGifImageView.setVisibility(View.GONE);
            mDefaultImageView.setVisibility(View.VISIBLE);
            mDownloadProgressBar.setVisibility(View.GONE);
        }

        setImage(picView, false);
        return v;
    }

    private void setImage(final PicViewObject picViewObj, boolean isPreview) {
        boolean isBindFail = false;
        if (mHDImageLoader != null) {
            if (MultiImageActivity.mFailImageMap == null) {
                MultiImageActivity.mFailImageMap = new HashMap<Long, Boolean>();
            }
            if (MultiImageActivity.mFailImageMap.containsKey(mImageId)) {
                if (MultiImageActivity.mFailImageMap.get(mImageId)) {// 彻底失败
                    showDownloadFailView();
                    isBindFail = true;
                } else {// 缩略图可用
                    MultiImageActivity.mFailImageMap.put(mImageId, false);
                    isBindFail = !mHDImageLoader.bind(this,
                            picViewObj.getPicId(),
                            picViewObj.getPicPreViewUrl(), picViewObj.getPicType());
                }
            } else {
                if (isPreview) {
                    MultiImageActivity.mFailImageMap.put(mImageId, false);
                    isBindFail = !mHDImageLoader.bind(this,
                            picViewObj.getPicId(),
                            picViewObj.getPicPreViewUrl(), picViewObj.getPicType());
                } else {
                    isBindFail = !mHDImageLoader.bind(this,
                            picViewObj.getPicId(), picViewObj.getPicUrl(), picViewObj.getPicType());
                }
            }
        }
        if (isBindFail) {
            mImageView.setVisibility(View.GONE);
            mGifImageView.setVisibility(View.GONE);
            mDefaultImageView.setVisibility(View.GONE);
            mDownloadProgressBar.setVisibility(View.GONE);
        }
    }

    private void showDownloadFailView() {
        MultiImageActivity.mFailImageMap.put(mImageId, true);
        mImageView.setVisibility(View.GONE);
        mGifImageView.setVisibility(View.GONE);
        mDefaultImageView.setVisibility(View.GONE);
        mDownloadProgressBar.setVisibility(View.GONE);
        mLoadFailedImageView.setVisibility(View.VISIBLE);
        mLoadFailedTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoadImage(final Bitmap bitmap, final String url, int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    if (!TextUtils.isEmpty(url)
                            && url.equals(picView.getPicPreViewUrl())) {
                        // 下载失败逻辑
                        showDownloadFailView();
                    } else {
                        setImage(picView, true);
                    }
                } else {
                    mBmp = bitmap;
                    mLoadFailedImageView.setVisibility(View.GONE);
                    mLoadFailedTextView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.VISIBLE);
                    mGifImageView.setVisibility(View.GONE);
                    mDefaultImageView.setVisibility(View.GONE);
                    mDownloadProgressBar.setVisibility(View.GONE);
                    mImageView.setImageBitmap(bitmap);
                }
            }
        });
    }

    @Override
    public void onLoadGif(final List<GifFrame> gifs, final byte[] data, final String url, int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (gifs == null || gifs.isEmpty() || data == null) {
                    if (!TextUtils.isEmpty(url)
                            && url.equals(picView.getPicPreViewUrl())) {
                        // 下载失败逻辑
                        showDownloadFailView();
                    } else {
                        setImage(picView, true);
                    }
                } else {
                    mData = data;
                    mLoadFailedImageView.setVisibility(View.GONE);
                    mLoadFailedTextView.setVisibility(View.GONE);
                    mImageView.setVisibility(View.GONE);
                    mGifImageView.setVisibility(View.VISIBLE);
                    mDefaultImageView.setVisibility(View.GONE);
                    mDownloadProgressBar.setVisibility(View.GONE);
                    mGifImageView.setFrames(gifs);
                    // if (CurrentImageId == mImageId) {
                    mGifImageView.startPlay();
                    // }
                }
            }
        });
    }

    @Override
    public void notfiyProgress(final int progress, final String url) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("image", "yiqiu.wsh progress == " + progress
                        + " url == " + url);
                int wheelprogress = (int) (progress * 3.6);
                mLoadFailedImageView.setVisibility(View.GONE);
                mLoadFailedTextView.setVisibility(View.GONE);
                mImageView.setVisibility(View.GONE);
                mGifImageView.setVisibility(View.GONE);
                mDefaultImageView.setVisibility(View.VISIBLE);
                mDownloadProgressBar.setVisibility(View.VISIBLE);
                mDownloadProgressBar.setProgress(wheelprogress);
            }
        });
    }

    @Override
    public void notifyError(final String url, int type) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(url)
                        && url.equals(picView.getPicPreViewUrl())) {
                    // 下载失败逻辑
                    showDownloadFailView();
                } else {
                    setImage(picView, true);
                }
            }
        });
    }


    private String getFileName() {
        String name = "";
        PicViewObject msg = picView;
        final String path = msg.getPicPreViewUrl();
        if (path != null
                && path.startsWith(EnvConstants.imageRootPath)) {
            name = WXUtil.getFileName(path);
        } else {
            name = WXUtil.getMD5FileName(path);
        }
        return name;
    }

    private Bitmap getBmp() {
        return mBmp;
    }

    private byte[] getData() {
        return mData;
    }

    public void saveImage() {
        String fileName = getFileName();
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(getActivity(), R.string.null_image, Toast.LENGTH_SHORT).show();
            return;
        }
        File file = new File(EnvConstants.imageRootPath, fileName);
        boolean isFileExist = false;
        Bitmap bitmap = getBmp();
        byte[] data = getData();
        isFileExist = file.exists() || file.mkdirs();
        if (!isFileExist) {
            Toast.makeText(getActivity(), R.string.insert_sdcard, Toast.LENGTH_SHORT).show();
        } else {
            if (bitmap != null || data != null) {
                long dateTaken = System.currentTimeMillis();
                Uri uri = null;
                if (bitmap != null) {
                    String name = fileName + ".jpg";
                    if (addImageAsApplication(
                            EnvConstants.thumbRootPath,
                            name, bitmap)) {
                        try {
                            uri = saveToDb(
                                    dateTaken,
                                    EnvConstants.thumbRootPath,
                                    name);
                        } catch (UnsupportedOperationException e) {
                            // TODO EXCEPTION_TODO: handle exception
                        }

                    }

                } else if (data != null) {
                    String name = fileName + ".gif";
                    if (addImageAsApplication(
                            EnvConstants.thumbRootPath,
                            name, data)) {
                        try {
                            uri = saveToDb(
                                    dateTaken,
                                    EnvConstants.thumbRootPath,
                                    name);
                        } catch (UnsupportedOperationException e) {
                        }
                    }
                }
                if (uri != null) {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.image_saved)
                                    + EnvConstants.thumbRootPath,
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(),
                            getResources().getString(R.string.image_saved),
                            Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getActivity(), R.string.null_image, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean addImageAsApplication(String directory, String filename,
                                          byte[] source) {

        boolean flag = false;
        FileOutputStream outputStream = null;

        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                outputStream.write(source);
                outputStream.flush();
                flag = true;
            } else {
                flag = true;
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        return flag;

    }

    private boolean addImageAsApplication(String directory, String filename,
                                          Bitmap source) {

        boolean success = false;
        OutputStream outputStream = null;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                if (source != null) {
                    source.compress(Bitmap.CompressFormat.JPEG, 75, outputStream);
                    success = true;
                }
            } else {
                success = true;
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        return success;

    }

    private Uri saveToDb(long dateTaken, String directory, String filename) {
        String filePath = directory + "/" + filename;
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN, dateTaken);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATA, filePath);
        try {
            return getActivity().getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gif_image_detail_view:
            case R.id.image_detail_layout:
                if (mCallback != null) {
                    mCallback.onSingleTouch();
                }
                break;
            default:
                break;

        }
    }

    @Override
    public void onSingleTouch() {
        if (mCallback != null) {
            mCallback.onSingleTouch();
        }
    }

    @Override
    public void onLongTouch() {
        if (mCallback != null) {
            mCallback.onSingleTouch();
        }

    }

    @Override
    public void onScaleBegin() {

    }

    @Override
    public void onDoubleTap() {

    }

    public interface OnImageFragmentListener {
        public void onSingleTouch();

        public void onDialogClick();
    }
}
