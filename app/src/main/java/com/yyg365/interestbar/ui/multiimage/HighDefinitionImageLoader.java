package com.yyg365.interestbar.ui.multiimage;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.URLUtil;

import com.yyg365.interestbar.IMChannel;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.biz.tools.FileTools;
import com.yyg365.interestbar.biz.tools.ImageUtils;
import com.yyg365.interestbar.biz.tools.ThumbnailUtils;
import com.yyg365.interestbar.channel.request.BaseRequest;
import com.yyg365.interestbar.channel.request.HttpMethod;
import com.yyg365.interestbar.channel.request.RequestCallback;
import com.yyg365.interestbar.channel.request.Response;
import com.yyg365.interestbar.ui.multiimage.gif.GifDecoder;
import com.yyg365.interestbar.ui.multiimage.gif.GifFrame;
import com.yyg365.interestbar.utils.WXUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @author yiqiu.wsh
 */

public class HighDefinitionImageLoader {

    // private EgoAccount mContext = null;
    public static final int NO_IMAGE = -1;
    public static final int BIG_IMAGE = 0;
    public static final int ORIGINAL_IMAGE = 1;
    public static final int THUMBNAIL_IMAGE = 2;
    private String TAG = HighDefinitionImageLoader.class.getSimpleName();
    private int mActiveTaskCount = 0;

    private final int mMaxTaskCount = 3;

    private List<AsyncTask<ImageRequest, ImageRequest, ImageRequest>> mImageTaskList = new ArrayList<AsyncTask<ImageRequest, ImageRequest, ImageRequest>>();

    private static HighDefinitionImageLoader mHighDefinitionImageLoader;

    public static final HighDefinitionImageLoader getInstance() {

        if (mHighDefinitionImageLoader == null) {
            mHighDefinitionImageLoader = new HighDefinitionImageLoader();
        }
        return mHighDefinitionImageLoader;
    }

    private int screenWidth;
    private int screenHeight;

    private HighDefinitionImageLoader() {
        DisplayMetrics dm = new DisplayMetrics();
        dm = IMChannel.getApplication().getResources().getDisplayMetrics();
        screenWidth = dm.widthPixels;
        screenHeight = dm.heightPixels;

    }

    private final ArrayList<ImageRequest> mRequests = new ArrayList<ImageRequest>();

    private class ImageTask extends
            AsyncTask<ImageRequest, ImageRequest, ImageRequest> {

        public final AsyncTask<ImageRequest, ImageRequest, ImageRequest> executeOnThreadPool(
                ImageRequest... params) {
            if (Build.VERSION.SDK_INT < 4) {
                // Thread pool size is 1
                return execute(params);
            } else if (Build.VERSION.SDK_INT < 11) {
                // The execute() method uses a thread pool
                return execute(params);
            } else {
                // The execute() method uses a single thread,
                // so call executeOnExecutor() instead.
                try {
                    Method method = AsyncTask.class
                            .getMethod("executeOnExecutor", Executor.class,
                                    Object[].class);
                    Field field = AsyncTask.class
                            .getField("THREAD_POOL_EXECUTOR");
                    Object executor = field.get(null);
                    method.invoke(this, executor, params);
                } catch (NoSuchMethodException e) {
                    throw new RuntimeException(
                            "Unexpected NoSuchMethodException", e);
                } catch (NoSuchFieldException e) {
                    throw new RuntimeException(
                            "Unexpected NoSuchFieldException", e);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(
                            "Unexpected IllegalAccessException", e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(
                            "Unexpected InvocationTargetException", e);
                }
                return this;
            }
        }

        @Override
        protected ImageRequest doInBackground(ImageRequest... requests) {

            if (requests != null && requests.length == 1) {
                final ImageRequest request = requests[0];
                if (request != null) {
                    request.execute(new ApiCallback() {

                        @Override
                        public void onSuccess(Object result) {

                        }

                        @Override
                        public void onProgress(int progress) {
                            request.progress = progress;
                            publishProgress(request);
                        }

                        @Override
                        public void onError(int code, String info) {

                        }
                    });
                    return request;
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            mActiveTaskCount++;
        }

        @Override
        protected void onPostExecute(ImageRequest result) {
            if (result != null) {
                result.publishResult();
                mImageTaskList.remove(this);
                mActiveTaskCount--;
                flushRequests();
            }
        }

        @Override
        protected void onProgressUpdate(ImageRequest... values) {
            if (values != null && values.length == 1) {
                ImageRequest request = values[0];
                request.notfiyProgress(request.progress, request.url);
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

    private class ImageRequest {

        int progress = 0;
        long id = 0;
        Bitmap bitmap = null;
        WeakReference<ILoadBigImageView> view;
        String url = null;
        String md5Name; // 保存到本地文件名
        String cacheName;
        List<GifFrame> gifFrames;
        byte[] data;
        int type = HighDefinitionImageLoader.BIG_IMAGE;
//		private String selfCreateMd5Name;


        boolean execute(final ApiCallback listener) {
//			if(IMChannel.DEBUG)Log.d(TAG+"@OriginalPic","url = "+url);
            String name = WXUtil.getFileName(url);
            md5Name = WXUtil.getMD5FileName(url);
            Log.d(TAG + "@originalPic", "md5Name = " + md5Name);
            // 从sdcard加载图片
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())) {
                // 加载文件名未转换为md5的图片
                String localName = name;
                File file = new File(EnvConstants.imageRootPath, name);
                if (!file.exists()) {
                    localName = md5Name;
                }
                file = new File(EnvConstants.imageRootPath, localName);
                if (file.exists()) {
                    //md5名称
                    boolean obj = false;
                    try {
                        obj = decode(url, localName);
                    } catch (OutOfMemoryError e) {
                        System.gc();
                        gifFrames = null;
                        bitmap = null;
                        data = null;
                        return obj;
                    }

                    if (obj) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    file = new File(url);
                    if (file.exists()) {
                        boolean flag = decodeImage(file.getAbsolutePath());
                        if (flag) {
                            int degree = ImageUtils.getOrientation(file.getAbsolutePath(), null, null);
                            //旋转图片
                            Bitmap b2 = ThumbnailUtils.rotateBitmap(bitmap, degree);
                            if (b2 != null) {
                                if (bitmap != b2) {
                                    Log.d(TAG + "@originalPic", "旋转图片并创造了新的图片");
                                    try {
                                        bitmap.recycle();
                                    } catch (Exception e) {
                                    } catch (Error e) {
                                    }
                                    bitmap = b2;
                                }
                            }
                        }
                        return flag;
                    }
                }

            }
            if (!URLUtil.isValidUrl(url)) {
                return false;
            }
            // sdcard上没有图片从网络上下载图片
            if (Environment.MEDIA_MOUNTED.equals(Environment
                    .getExternalStorageState())) {
                boolean ret = false;
                String destfile = new StringBuilder(EnvConstants.imageRootPath).append(File.separator).append(md5Name).toString();
                try {
                    //url有效，从网络读取图片
                    Response r = new BaseRequest(url, new HashMap<String, String>(), HttpMethod.GET, new RequestCallback() {
                        @Override
                        public void onProgress(int progress) {
                            listener.onProgress(progress);
                        }

                        @Override
                        public <T> void onSuccess(T... result) {
                            listener.onSuccess(result);
                        }

                        @Override
                        public void onError(int code, String info) {
                            listener.onError(code, info);
                        }
                    }).execute();
                    if (r.isSuccess()) {
                        byte[] data = r.getContent();
                        FileTools.writeFile(EnvConstants.imageRootPath, md5Name, data);
                        ret = true;
                    }
                } catch (OutOfMemoryError e) {
                    System.gc();
                    gifFrames = null;
                    bitmap = null;
                    data = null;
                }
                try {
                    if (ret) {
                        Log.d(TAG + "@OriginalPic", "网络下载成功，从本地图片解析Bitmap,URL:  " + url);
                        boolean decode = decode(url, md5Name);
                        return decode;
                    }
                } catch (OutOfMemoryError e) {
                    System.gc();
                    gifFrames = null;
                    bitmap = null;
                    data = null;
                }
            }
            return true;
        }

        public void notfiyProgress(int progress, String url) {
            ILoadBigImageView imageView = view.get();
            if (imageView != null) {
                imageView.notfiyProgress(progress, url);
            }
        }

        /**
         * fileIO 读取图片字节流
         *
         * @param pathName
         * @return
         */
        private byte[] readFileByProgress(String pathName) {
            File file = new File(pathName);
            long totalSize = file.length();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                byte[] buffer = new byte[256];
                int len = 0;
                while ((len = fis.read(buffer)) != -1) {
                    result.write(buffer, 0, len);
                }
                return result.toByteArray();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null) {
                        fis.close();
                    }
                    if (result != null) {
                        result.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        protected boolean decode(String cacheName, String fileName) {
            this.cacheName = cacheName;
            String pathName = new StringBuilder(EnvConstants.imageRootPath).append(File.separator).append(fileName).toString();

            boolean flag = decodeImage(pathName);
            if (flag) {
                int degree = ImageUtils.getOrientation(pathName, null, null);
                //旋转图片
                Bitmap b2 = ThumbnailUtils.rotateBitmap(bitmap, degree);
                if (b2 != null) {
                    if (bitmap != b2) {
                        Log.d(TAG + "@originalPic", "旋转图片并创造了新的图片");
                        try {
                            bitmap.recycle();
                        } catch (Exception e) {
                        } catch (Error e) {
                        }
                        bitmap = b2;
                    }
                }
            }
            return flag;
        }

        /**
         * 云旺OpenIM本地路径读取图片 shuheng 2015.6
         *
         * @param pathName
         * @return
         */
        protected boolean decode(String pathName) {
            boolean flag = decodeImage(pathName);
            if (flag) {
                try {


                    int degree = ImageUtils.getOrientation(pathName, null, null);
                    //旋转图片
                    Bitmap b2 = ThumbnailUtils.rotateBitmap(bitmap, degree);
                    if (b2 != null) {
                        if (bitmap != b2) {
                            Log.d(TAG + "@originalPic", "旋转图片并创造了新的图片");
                            try {
                                bitmap.recycle();
                            } catch (Exception e) {
                            } catch (Error e) {
                            }
                            bitmap = b2;
                        }
                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                return flag;
            }
            return false;
        }

        /**
         * objIO 读取和压缩［文件流］为图片或GIF用于预览
         *
         * @param filePath
         * @return
         */
        private boolean decodeImage(String filePath) {

            File file = new File(filePath);

            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;
            if (decodeOptions != null)

                if (decodeOptions != null && !TextUtils.isEmpty(decodeOptions.outMimeType) && decodeOptions.outMimeType != null && (decodeOptions.outMimeType.contains("gif") || decodeOptions.outMimeType.contains("GIF"))) {
                    byte[] data = null;
                    try {
                        data = readFileByProgress(filePath);
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                    if (data == null) return false;
                    ByteArrayInputStream bis = null;
                    bis = new ByteArrayInputStream(data);
                    GifDecoder decoder = new GifDecoder();
                    try {
                        decoder.read(bis);
                        gifFrames = decoder.getFrames();
                        if (gifFrames != null && gifFrames.size() > 0) {
                            this.data = data;

                            boolean isAllZero = true;
                            for (GifFrame frame : gifFrames) {
                                if (frame.getDelay() != 0) {
                                    isAllZero = false;
                                    break;
                                }
                            }
                            if (isAllZero) {
                                for (GifFrame frame : gifFrames) {
                                    frame.setDelay(50);
                                }
                            }
                            return true;
                        }
                    } catch (OutOfMemoryError e) {
                        gifFrames = decoder.getFrames();
                        if (gifFrames != null) {
                            for (GifFrame frame : gifFrames) {
                                if (frame != null && frame.getImage() != null) {
                                    frame.getImage().recycle();
                                }
                            }
                            gifFrames.clear();
                        }
                        gifFrames = null;
                    }
                }

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inPreferredConfig = Config.ARGB_8888;
            //objIO 读取和压缩［字节流］为图片用于预览,包括原图和预览图
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                    actualHeight, screenWidth, screenHeight);
            bitmap = FileTools.decodeBitmap(filePath, decodeOptions);
            if (bitmap != null) {
                return true;
            }
            return false;

        }


        /**
         * objIO 读取和压缩［字节流］为图片或GIF用于预览
         *
         * @param data
         * @param type
         * @return
         */
        private boolean decodeImage(byte[] data, String type) {

            if ("GIF".equals(type)) {

                ByteArrayInputStream bis = null;
                bis = new ByteArrayInputStream(data);
                GifDecoder decoder = new GifDecoder();
                try {
                    decoder.read(bis);
                    gifFrames = decoder.getFrames();
                    if (gifFrames != null && gifFrames.size() > 0) {
                        this.data = data;

                        boolean isAllZero = true;
                        for (GifFrame frame : gifFrames) {
                            if (frame.getDelay() != 0) {
                                isAllZero = false;
                                break;
                            }
                        }
                        if (isAllZero) {
                            for (GifFrame frame : gifFrames) {
                                frame.setDelay(50);
                            }
                        }
                        return true;
                    }
                } catch (OutOfMemoryError e) {
                    gifFrames = decoder.getFrames();
                    if (gifFrames != null) {
                        for (GifFrame frame : gifFrames) {
                            if (frame != null && frame.getImage() != null) {
                                frame.getImage().recycle();
                            }
                        }
                        gifFrames.clear();
                    }
                    gifFrames = null;
                }
            }

            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(data, 0, data.length, decodeOptions);
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inPreferredConfig = Config.ARGB_8888;
            //objIO 读取和压缩［字节流］为图片用于预览,包括原图和预览图
            decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                    actualHeight, screenWidth, screenHeight);
            try {
                bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                        decodeOptions);
            } catch (OutOfMemoryError e) {
                try {
                    decodeOptions.inSampleSize *= 2;
                    bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                            decodeOptions);
                } catch (OutOfMemoryError e2) {
                    try {
                        decodeOptions.inSampleSize *= 2;
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                                decodeOptions);
                    } catch (OutOfMemoryError e3) {
                        decodeOptions.inSampleSize *= 2;
                        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,
                                decodeOptions);
                    }

                }

            }

            if (bitmap != null) {
                return true;
            }
            return false;

        }

        private int findBestSampleSize(int width, int height,
                                       int reqWidth, int reqHeight) {
            int inSampleSize = 1;
            if (height > reqHeight || width > reqWidth) {

                final int halfHeight = height / 2;
                final int halfWidth = width / 2;

                // Calculate the largest inSampleSize value that is a power of 2 and keeps both
                // height and width larger than the requested height and width.
                while ((halfHeight / inSampleSize) > reqHeight
                        && (halfWidth / inSampleSize) > reqWidth) {
                    inSampleSize *= 2;
                }
            }

            return (int) inSampleSize;
        }

        protected boolean decode(String cacheName, byte[] data) {
            this.cacheName = cacheName;
            String type = ThumbnailUtils.getType(data);
            boolean flag = decodeImage(data, type);
            if (flag) {
                FileTools.writeFile(
                        EnvConstants.imageRootPath,
                        md5Name, data);
                int degree = ImageUtils.getOrientation(new StringBuilder(EnvConstants.imageRootPath).append(File.separator).append(md5Name).toString(), null, null);
                //旋转图片
                Bitmap b2 = ThumbnailUtils.rotateBitmap(bitmap, degree);
                if (b2 != null) {
                    if (bitmap != b2) {
                        try {
                            bitmap.recycle();
                        } catch (Exception e) {
                        } catch (Error e) {
                        }

                        bitmap = b2;
                    }
                }
            }
            return flag;
        }

        void publishResult() {
            if (view != null) {
                ILoadBigImageView imageView = view.get();
                if (imageView != null) {
                    if ((gifFrames != null && data != null) || bitmap != null) {
                        if (gifFrames != null && data != null) {
                            imageView.onLoadGif(gifFrames, data, cacheName, type);
                        } else {

                            imageView.onLoadImage(bitmap, cacheName, type);


                        }
                        bitmap = null;
                        data = null;
                    } else {
                        imageView.notifyError(url, type);
                        bitmap = null;
                        data = null;
                    }
                }
            }
        }

        public ImageRequest(ILoadBigImageView imageView, long id, String url, int type) {
            this.id = id;
            this.view = new WeakReference<ILoadBigImageView>(imageView);
            this.url = url;
            this.type = type;
        }
    }

    private void flushRequests() {
        while (mActiveTaskCount < mMaxTaskCount && !mRequests.isEmpty()) {
            try {
                mImageTaskList.add(new ImageTask()
                        .executeOnThreadPool(mRequests.remove(0)));
            } catch (Exception e) {
                mImageTaskList
                        .add(new ImageTask().execute(mRequests.remove(0)));
            }
        }
    }

    public boolean bind(ILoadBigImageView view, long id, String url, int type) {
        if (view == null || id == 0 || TextUtils.isEmpty(url)) {
            return false;
        }
        ImageRequest request = new ImageRequest(view, id, url, type);
        insertRequestAtFrontOfQueue(request);
        return true;
    }

    private void insertRequestAtFrontOfQueue(ImageRequest request) {

        for (int i = (mRequests.isEmpty() ? -1 : mRequests.size() - 1); i >= 0; i--) {
            ImageRequest req = mRequests.get(i);
            if (req.id == request.id) {
                mRequests.remove(req);
                break;
            }
        }
        mRequests.add(0, request);
        flushRequests();
    }

    public void recycle() {
        if (!mImageTaskList.isEmpty()) {
            for (AsyncTask<ImageRequest, ImageRequest, ImageRequest> asyncTask : mImageTaskList) {
                if (asyncTask.isCancelled()) {
                    asyncTask.cancel(true);
                }
            }
        }
        mImageTaskList.clear();
    }
}
