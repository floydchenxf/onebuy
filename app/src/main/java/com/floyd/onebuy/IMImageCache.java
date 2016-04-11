/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.floyd.onebuy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.biz.tools.FileTools;
import com.floyd.onebuy.channel.threadpool.WxDefaultExecutor;
import com.floyd.onebuy.utils.WXUtil;

import java.io.File;


/**
 * This class holds our bitmap caches (memory and disk).
 */
public class IMImageCache implements ImageLoader.ImageCache {

    // Default memory cache size
    private static final int DEFAULT_MEM_CACHE_SIZE = (int) (Runtime
            .getRuntime().maxMemory() / 12); //

    // Constants to easily toggle various caches
    private static final boolean DEFAULT_MEM_CACHE_ENABLED = true;


//	private static final int delayMillis = 50; // 延时时间

    //	private WxImageDiskCache mDiskCache;
    private LruCache<String, Bitmap> mMemoryCache;

    private static IMImageCache imageCache;

//	private Handler mHandler;

    /**
     * Creating a new ImageCache object using the specified parameters.
     *
     * @param context     The context to use
     * @param cacheParams The cache parameters to use to initialize the cache
     */
    public IMImageCache(Context context, ImageCacheParams cacheParams) {
        init(context, cacheParams);
    }

    /**
     * Creating a new ImageCache object using the default parameters.
     *
     * @param context    The context to use
     * @param uniqueName A unique name that will be appended to the cache directory
     */
    public IMImageCache(Context context, String uniqueName) {
        init(context, new ImageCacheParams(uniqueName));
    }

    /**
     * , if not found a new one is created with defaults and saved to a
     * <p/>
     * The calling {@link FragmentActivity}
     *
     * @param uniqueName A unique name to append to the cache directory
     * @return An existing retained ImageCache object or a new one if one did
     * not exist.
     */
    public static IMImageCache findOrCreateCache(final Context context,
                                                 final String uniqueName) {
        return findOrCreateCache(context, new ImageCacheParams(uniqueName));
    }

    /**
     * <p/>
     * The calling {@link FragmentActivity}
     *
     * @param cacheParams The cache parameters to use if creating the ImageCache
     * @return An existing retained ImageCache object or a new one if one did
     * not exist
     */
    private static synchronized IMImageCache findOrCreateCache(final Context context,
                                                               ImageCacheParams cacheParams) {
        if (imageCache == null) {
            imageCache = new IMImageCache(context, cacheParams);
        }
        return imageCache;
    }

    /**
     * Initialize the cache, providing all parameters.
     *
     * @param context     The context to use
     * @param cacheParams The cache parameters to initialize the cache
     */
    private void init(Context context, ImageCacheParams cacheParams) {
        // final File diskCacheDir = DiskLruCache.getDiskCacheDir(context,
        // cacheParams.uniqueName);

        // Set up disk cache
//		if (cacheParams.diskCacheEnabled) {
//			mDiskCache = new WxImageDiskCache(cacheParams.uniqueName);
//		}

        // Set up memory cache
        if (cacheParams.memoryCacheEnabled) {
            mMemoryCache = new LruCache<String, Bitmap>(
                    cacheParams.memCacheSize) {
                /**
                 * Measure item size in bytes rather than units which is more
                 * practical for a bitmap cache
                 */
                @SuppressLint("NewApi")
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // int size = bitmap.getRowBytes() * bitmap.getHeight();
                    // Log.d("setting", "Bitmap size:" + size);
                    int size = 0;
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
                        size = bitmap.getRowBytes() * bitmap.getHeight();
                    } else {
                        size = bitmap.getByteCount();
                    }
                    return size;
                }
            };
        }

    }

//	public void addBitmapToCache(String data, Bitmap bitmap) {
//		if (data == null || bitmap == null) {
//			return;
//		}
//
//		// Add to memory cache
//		if (mMemoryCache != null && mMemoryCache.get(data) == null) {
//			mMemoryCache.put(data, bitmap);
//		}
//
//		// Add to disk cache
//		if (mDiskCache != null) {
//			mDiskCache.put(data, bitmap);
//		}
//	}

    /**
     * Get from memory cache.
     *
     * @param data Unique identifier for which item to get
     * @return The bitmap if found in cache, null otherwise
     */
    public Bitmap getBitmapFromMemCache(String data) {
        if (mMemoryCache != null) {
            final Bitmap memBitmap = mMemoryCache.get(data);
            if (memBitmap != null) {
                return memBitmap;
            }
        }
        return null;
    }

    /**
     * Get from disk cache.
     *
     * @return The bitmap if found in cache, null otherwise
     */
//	public Bitmap getBitmapFromDiskCache(String data) {
//		if (mDiskCache != null) {
//			return mDiskCache.get(data);
//		}
//		return null;
//	}
    public void clearCaches() {
        mMemoryCache.evictAll();
    }

    /**
     * A holder class that contains cache parameters.
     */
    private static class ImageCacheParams {
        public int memCacheSize = DEFAULT_MEM_CACHE_SIZE;
        public boolean memoryCacheEnabled = DEFAULT_MEM_CACHE_ENABLED;

        private ImageCacheParams(String uniqueName) {
        }
    }

    @Override
    public Bitmap getBitmap(final String url) {
        final String md5Name = WXUtil.getMD5Value(url);
        Bitmap memBitmap = getBitmapFromMemCache(md5Name);
        if (memBitmap != null) {
//			FileTools.writeBitmap(Environment
//					.getExternalStorageDirectory().getAbsolutePath()+"/wangxin/", "test.jpg", memBitmap);
            return memBitmap;
        } else {
            Log.d("test1", "bitmap memory cache not hit load from sdcard");
            memBitmap = FileTools.readBitmap(EnvConstants.imageRootPath + File.separator + md5Name);
            if (memBitmap == null) {
                Log.d("test1", "bitmap memory cache not hit");
            }
            return memBitmap;

        }
    }

    @Override
    public void putBitmap(String url, final Bitmap bitmap) {
        final String md5Name = WXUtil.getMD5Value(url);

        if (md5Name == null || bitmap == null) {
            return;
        }

        // Add to memory cache
        if (mMemoryCache != null && mMemoryCache.get(md5Name) == null) {
            mMemoryCache.put(md5Name, bitmap);
        }


        WxDefaultExecutor.getInstance().submitHighPriority(new Runnable() {
            @Override
            public void run() {
                FileTools.writeBitmap(EnvConstants.imageRootPath + File.separator + md5Name, bitmap, 100);
            }
        });

    }

    public void removeBitmap(String key) {
        if (TextUtils.isEmpty(key)) {
            return;
        }
        String md5Name = WXUtil.getMD5Value(key);

        if (mMemoryCache != null) {
            mMemoryCache.remove(md5Name);
        }
    }


}
