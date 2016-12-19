package com.floyd.onebuy.biz.manager;

import android.util.Log;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.aync.HttpJobFactory;
import com.floyd.onebuy.biz.func.StringFunc;
import com.floyd.onebuy.biz.parser.AbstractJsonParser;
import com.floyd.onebuy.channel.request.FileItem;
import com.floyd.onebuy.channel.request.HttpMethod;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Created by floyd on 15-12-5.
 */
public class JsonHttpJobFactory {

    private static final String TAG = "JsonHttpJobFactory";
    public static <T> AsyncJob<T> getJsonAsyncJob(String url, Map<String, String> params, HttpMethod httpMethod, final Type type) {
        return HttpJobFactory.createHttpJob(url, params, httpMethod).map(new StringFunc()).flatMap(new Func<String, AsyncJob<T>>() {
            @Override
            public AsyncJob<T> call(final String s) {
                return new AsyncJob<T>() {
                    @Override
                    public void start(ApiCallback<T> callback) {
                        new AbstractJsonParser<T>() {
                            @Override
                            protected T convert2Obj(String data) {

                                if (data == null) {
                                    return null;
                                }
                                T result = null;
                                try {
                                    result = GsonHelper.getGson().fromJson(data, type);
                                } catch (Exception e) {
                                    Log.e(TAG, e.getMessage());
                                    result = null;
                                }
                                return result;
                            }
                        }.doParse(callback, s);
                    }
                };
            }
        });
    }

    public static <T> AsyncJob<T> getJsonAsyncJob(String url, Map<String, String> params, Map<String, FileItem> files, HttpMethod httpMethod, final Type type) {
        return HttpJobFactory.createFileJob(url, params, files, HttpMethod.POST).map(new StringFunc()).flatMap(new Func<String, AsyncJob<T>>() {
            @Override
            public AsyncJob<T> call(final String s) {
                return new AsyncJob<T>() {
                    @Override
                    public void start(ApiCallback<T> callback) {
                        new AbstractJsonParser<T>() {
                            @Override
                            protected T convert2Obj(String data) {
                                return GsonHelper.getGson().fromJson(data, type);
                            }
                        }.doParse(callback, s);
                    }
                };
            }
        });
    }
}
