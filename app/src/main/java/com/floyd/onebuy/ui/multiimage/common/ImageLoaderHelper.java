package com.floyd.onebuy.ui.multiimage.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.widget.ImageView;

import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.biz.tools.ThumbnailUtils;

import java.lang.ref.WeakReference;

/**
 * Created by floyd on 15-9-9.
 */
public class ImageLoaderHelper {

    private static ImageLoaderHelper helper = null;

    private Context context;

    private ImageLoaderHelper(Context context) {
        this.context = context;
    }

    public static synchronized ImageLoaderHelper getHelper(Context context) {
        if (helper == null) {
            helper = new ImageLoaderHelper(context);
        }
        return helper;
    }

    private static final int DEFAULT_SIZE = 180;

    private class AsyncDrawable extends BitmapDrawable {
        private final WeakReference<BitmapWorkerTask> viewResizeTaskReference;

        public AsyncDrawable(Resources res, Bitmap bitmap, BitmapWorkerTask viewResizeTask) {
            super(res, bitmap);
            viewResizeTaskReference = new WeakReference<BitmapWorkerTask>(viewResizeTask);
        }

        public BitmapWorkerTask getBitmapWorkerTask() {
            return viewResizeTaskReference.get();
        }
    }

    /**
     * 确保ImageView执行的是它对应的Task，否则取消任务
     *
     * @param url
     * @param imageView
     * @return
     */
    private boolean cancelPotentialWork(String url, ImageView imageView) {
        // 获得ImageView对应的Task
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final String imgUrl = bitmapWorkerTask.url;
            if (imgUrl == null || !imgUrl.equals(url)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    /**
     * 获得已经被分配到ImageView的指定的Task
     *
     * @param imageView
     * @return
     */
    private BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    /**
     * 异步加载图片
     *
     * @param url
     * @param imageView
     *
     */
    public void loadBitmap(String url, ImageView imageView, LruCache<String, Bitmap> cache, int orientation) {
        if (cancelPotentialWork(url, imageView)) {
            final BitmapWorkerTask task = new BitmapWorkerTask(imageView, cache, orientation);
            Bitmap defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_image);
            final AsyncDrawable asyncDrawable = new AsyncDrawable(context.getResources(), defaultBitmap, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(url);
        }
    }

    /*
     * 异步加载图片Task类
     */
    class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
        private WeakReference<ImageView> imageViewReference;
        public String url;
        private LruCache<String, Bitmap> mBitmapCache;
        private int orientation = 0;

        private BitmapWorkerTask(ImageView imageView, LruCache<String, Bitmap> mBitmapCache, int orientation) {
            this.imageViewReference = new WeakReference<ImageView>(imageView);
            this.mBitmapCache = mBitmapCache;
            this.orientation = orientation;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            String k = params[0];
            this.url = k;

            if (imageViewReference != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (bitmapWorkerTask == null || this != bitmapWorkerTask) {
                    Log.d("test", "url:" + url + " execute cancel1---------------");
                    return null;
                }

                final String imgUrl = bitmapWorkerTask.url;
                if (imgUrl == null || !imgUrl.equals(url)) {
                    Log.d("test", "url:" + url + "execute cancel2---------------");
                    return null;
                }
            }
            return decodeBitmap(k);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()) {
                bitmap = null;
                return;
            }

            if (imageViewReference != null && bitmap != null) {
                final ImageView imageView = imageViewReference.get();
                final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);
                if (this == bitmapWorkerTask && imageView != null) {
                    mBitmapCache.put(url, bitmap);
                    imageView.setImageBitmap(bitmap);
                }
            }
        }

        private Bitmap decodeBitmap(String filePath) {
            long start = System.currentTimeMillis();
            Bitmap cacheBitmap = mBitmapCache.get(filePath);
            if (cacheBitmap != null) {
                return cacheBitmap;
            }

            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            Bitmap bitmap = null;
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, decodeOptions);

            Log.d("test", "decodeBitmap time 1:" + (System.currentTimeMillis() - start));

            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int[] desiredDim = getResizedDimension(actualWidth, actualHeight);

            int desiredWidth = desiredDim[0];
            int desiredHeight = desiredDim[1];

            // Decode to the nearest power of two scaling factor.
            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;

            decodeOptions.inSampleSize = findBestSampleSize(actualWidth,
                    actualHeight, desiredWidth, desiredHeight);

            Bitmap tempBitmap = BitmapFactory.decodeFile(filePath, decodeOptions);
            Log.d("test", "decodeBitmap time 2:"
                    + (System.currentTimeMillis() - start));

            if (tempBitmap == null || tempBitmap.isRecycled()) {
                return null;
            }

            tempBitmap = ThumbnailUtils.getCropAndScaledBitmap(tempBitmap,
                    desiredWidth, desiredHeight, desiredDim[2], desiredDim[3],
                    true);

            Log.d("test", "decodeBitmap time 3:"
                    + (System.currentTimeMillis() - start));

            if (orientation != 0) {
                Log.d("test", "orientation:" + orientation);
                tempBitmap = ThumbnailUtils.rotateBitmap(tempBitmap, orientation);
            }
            Log.d("test", "decodeBitmap time:"
                    + (System.currentTimeMillis() - start));

            return tempBitmap;
        }


    }

    static int findBestSampleSize(int actualWidth, int actualHeight,
                                  int desiredWidth, int desiredHeight) {
        double wr = (double) actualWidth / desiredWidth;
        double hr = (double) actualHeight / desiredHeight;
        double ratio = Math.min(wr, hr);
        float n = 1.0f;
        while ((n * 2) <= ratio) {
            n *= 2;
        }
        return (int) n;
    }

    public static int[] getResizedDimension(int actualWidth, int actualHeight) {
        int[] resizedResults = new int[4];
        int DEFAULT_WIDTH = DEFAULT_SIZE;
        int DEFAULT_HEIGHT = DEFAULT_SIZE;
        int mMaxHeight = DEFAULT_SIZE;
        int mMinWidth = DEFAULT_SIZE;

        if (actualWidth <= 0 || actualHeight <= 0) {
            resizedResults[0] = DEFAULT_WIDTH;
            resizedResults[1] = DEFAULT_HEIGHT;
            resizedResults[2] = DEFAULT_WIDTH;
            resizedResults[3] = DEFAULT_HEIGHT;
            return resizedResults;
        }
        if (actualWidth <= actualHeight) {
            double ratio = (double) actualHeight / (double) actualWidth;
            int tmpHeight = (int) (DEFAULT_SIZE * ratio);

            resizedResults[0] = DEFAULT_SIZE; // 最终尺寸的宽度
            resizedResults[1] = tmpHeight;// 最终尺寸的高度
            resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
            resizedResults[3] = actualHeight; // 裁剪尺寸的高度
        } else {
            double ratio = (double) actualWidth / (double) actualHeight;
            int tmpWidth = (int) (DEFAULT_SIZE * ratio);

            resizedResults[0] = tmpWidth; // 最终尺寸的宽度
            resizedResults[1] = DEFAULT_SIZE;// 最终尺寸的高度
            resizedResults[2] = actualWidth; // 剪裁尺寸的宽度
            resizedResults[3] = actualHeight; // 裁剪尺寸的高度
        }

        return resizedResults;
    }
}
