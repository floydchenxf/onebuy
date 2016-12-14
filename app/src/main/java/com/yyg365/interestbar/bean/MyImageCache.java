package com.yyg365.interestbar.bean;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by Administrator on 2015/11/24.
 */
public class MyImageCache implements ImageLoader.ImageCache {
    private static MyImageCache myImageCache = new MyImageCache();

    public MyImageCache() {

    }

    int cacheSize = (int) (Runtime.getRuntime().totalMemory() / 8);
    LruCache<String, Bitmap> lruCache = new LruCache<String, Bitmap>(cacheSize) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };

    @Override
    public Bitmap getBitmap(String url) {
        return lruCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        lruCache.put(url, bitmap);
    }


    public static MyImageCache getMyImageCache() {
        return myImageCache;
    }
}
