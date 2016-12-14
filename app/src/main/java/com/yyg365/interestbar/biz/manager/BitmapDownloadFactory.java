package com.yyg365.interestbar.biz.manager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.aync.HttpJobFactory;
import com.yyg365.interestbar.channel.request.HttpMethod;

import java.util.Map;

/**
 * Created by floyd on 15-11-29.
 */
public class BitmapDownloadFactory {

    public static AsyncJob<Bitmap> getImage(String url, Map<String, String> params, HttpMethod httpMethod) {
        return HttpJobFactory.createHttpJob(url, params, httpMethod).map(new Func<byte[], Bitmap>() {
            @Override
            public Bitmap call(byte[] bytes) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        });
    }
}
