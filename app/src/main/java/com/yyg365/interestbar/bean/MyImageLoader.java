package com.yyg365.interestbar.bean;

import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.IMChannel;
import com.yyg365.interestbar.IMImageCache;
import com.yyg365.interestbar.biz.constants.EnvConstants;

/**
 * Created by Administrator on 2015/11/24.
 */
public class MyImageLoader extends ImageLoader {
    /**
     * Constructs a new ImageLoader.
     *
     * @param queue The RequestQueue to use for making image requests.
     */
    public MyImageLoader(RequestQueue queue) {
        super(queue, IMImageCache.findOrCreateCache(
                IMChannel.getApplication(), EnvConstants.imageRootPath));
    }

    public void bindView(String url, View view) {
        this.get(url, new MyImageListener(view, view.getContext().getApplicationContext()));
    }
}
