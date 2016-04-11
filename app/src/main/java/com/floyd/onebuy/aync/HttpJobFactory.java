package com.floyd.onebuy.aync;

import com.floyd.onebuy.IMChannel;
import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.channel.request.BaseRequest;
import com.floyd.onebuy.channel.request.FileItem;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.floyd.onebuy.channel.request.RequestCallback;
import com.floyd.onebuy.utils.NetworkUtil;

import java.util.Map;

/**
 * Created by floyd on 15-11-22.
 */
public class HttpJobFactory {

    public static AsyncJob<byte[]> createHttpJob(final String url, final Map<String, String> params, final HttpMethod httpMethod) {
        return new AsyncJob<byte[]>() {
            @Override
            public void start(final ApiCallback<byte[]> callback) {

                boolean isNetworkAvailable = NetworkUtil.isNetworkAvailable(IMChannel.getApplication());
                if (!isNetworkAvailable) {
                    if (callback != null) {
                        callback.onError(APIError.API_NETWORK_ERROR, "无网络，请检查网络设置．");
                    }
                    return;
                }

                new BaseRequest(url, params, httpMethod, new RequestCallback() {
                    @Override
                    public void onProgress(int progress) {
                        if (callback != null) {
                            callback.onProgress(progress);
                        }
                    }

                    @Override
                    public <T> void onSuccess(T... result) {
                        if (result == null || result.length <= 0) {
                            if (callback != null) {
                                callback.onError(APIError.API_CONTENT_EMPTY, "empty!");
                            }
                            return;
                        }

                        byte[] content = (byte[]) result[0];
                        if (callback != null) {
                            callback.onSuccess(content);
                        }
                    }

                    @Override
                    public void onError(int code, String info) {
                        if (callback != null) {
                            callback.onError(code, info);
                        }
                    }
                }).execute();

            }
        }.threadOn();
    }

    public static AsyncJob<byte[]> createFileJob(final String url, final Map<String, String> params, final Map<String, FileItem> files, final HttpMethod httpMethod) {
        return new AsyncJob<byte[]>() {
            @Override
            public void start(final ApiCallback<byte[]> callback) {
                new BaseRequest(url, params, files, httpMethod, new RequestCallback() {
                    @Override
                    public void onProgress(int progress) {
                        if (callback != null) {
                            callback.onProgress(progress);
                        }
                    }

                    @Override
                    public <T> void onSuccess(T... result) {
                        if (result == null || result.length <= 0) {
                            if (callback != null) {
                                callback.onError(APIError.API_CONTENT_EMPTY, "empty!");
                            }
                            return;
                        }

                        byte[] content = (byte[]) result[0];
                        if (callback != null) {
                            callback.onSuccess(content);
                        }
                    }

                    @Override
                    public void onError(int code, String info) {
                        if (callback != null) {
                            callback.onError(code, info);
                        }
                    }
                }).execute();

            }
        }.threadOn();
    }

}
