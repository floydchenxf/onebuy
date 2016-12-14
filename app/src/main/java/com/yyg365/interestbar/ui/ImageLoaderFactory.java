package com.yyg365.interestbar.ui;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.yyg365.interestbar.IMChannel;
import com.yyg365.interestbar.IMImageCache;
import com.yyg365.interestbar.biz.constants.EnvConstants;

/**
 * Created by floyd on 15-12-11.
 */
public class ImageLoaderFactory {

    private static RequestQueue mQueue;

    public static ImageLoader createImageLoader() {
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(IMChannel.getApplication());
        }
        IMImageCache wxImageCache = IMImageCache.findOrCreateCache(
                IMChannel.getApplication(), EnvConstants.imageRootPath);
        ImageLoader mImageLoader = new ImageLoader(mQueue, wxImageCache);
        mImageLoader.setBatchedResponseDelay(0);
        return mImageLoader;
    }
}
