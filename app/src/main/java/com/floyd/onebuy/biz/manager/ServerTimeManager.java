package com.floyd.onebuy.biz.manager;

import android.os.SystemClock;

import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.channel.request.BaseRequest;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.floyd.onebuy.channel.request.Response;
import com.floyd.onebuy.channel.threadpool.WxDefaultExecutor;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-4-26.
 */
public class ServerTimeManager {
    private static long mServerTime = 0;
    private static long mClientLocalTime = 0;

    public static long getServerTime() {
        if (SystemClock.elapsedRealtime() > mClientLocalTime
                && mServerTime != 0 && mClientLocalTime != 0) {
            return mServerTime * 1000 + SystemClock.elapsedRealtime()
                    - mClientLocalTime;
        } else {
            WxDefaultExecutor.getInstance().executeHttp(new Runnable() {
                @Override
                public void run() {
                    String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("pageType", "GetServiceData");
                    Response response = new BaseRequest(url, params, HttpMethod.GET).execute();
                    if (response.isSuccess()) {
                        mClientLocalTime = SystemClock.elapsedRealtime();
                        mServerTime = Long.parseLong(new String(response.getContent()));
                    }
                }
            });

            return System.currentTimeMillis();
        }
    }
}
