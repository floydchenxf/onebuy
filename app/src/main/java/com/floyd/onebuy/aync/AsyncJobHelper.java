package com.floyd.onebuy.aync;

import android.util.Log;

import com.floyd.onebuy.biz.constants.APIError;

import java.util.List;

/**
 * Created by floyd on 15-11-21.
 */
public class AsyncJobHelper {

    public static final String TAG = "AsyncJobFactory";

    public static AsyncJobHook hook = new AsyncJobHook();

    public static <T> AsyncJob<T> just(final T t) {
        return new AsyncJob<T>() {
            @Override
            public void start(ApiCallback<T> callback) {
                callback.onSuccess(t);
            }
        };
    }

    public static <T> AsyncJob<T> just(final List<T> list) {
        return new AsyncJob<T>() {
            @Override
            public void start(ApiCallback<T> callback) {
                for (T t:list) {
                    callback.onSuccess(t);
                }
            }
        };
    }

    public static <T> AsyncJob<T> concat(final AsyncJob<T> job1, final AsyncJob<T> job2) {
        return new AsyncJob<T>() {
            @Override
            public void start(final ApiCallback<T> callback) {
                job1.start(new ApiCallback<T>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        callback.onError(code, errorInfo);
                    }

                    @Override
                    public void onSuccess(T t) {
                        callback.onSuccess(t);
                        job2.start(callback);
                    }

                    @Override
                    public void onProgress(int progress) {
                        callback.onProgress(progress);
                    }
                });
            }
        };
    }

    public static <T1, T2, R> AsyncJob<R> zip(final AsyncJob<T1> job1, final AsyncJob<T2> job2, final Func2<T1, T2, R> func2) {
        AsyncJob<R> resultJob = job1.flatMap(new Func<T1, AsyncJob<R>>() {
            @Override
            public AsyncJob<R> call(final T1 t1) {
                return job2.flatMap(new Func<T2, AsyncJob<R>>() {
                    @Override
                    public AsyncJob<R> call(final T2 t2) {
                        return new AsyncJob<R>() {
                            @Override
                            public void start(ApiCallback<R> callback) {
                                R r = null;
                                try {
                                    r = func2.call(t1, t2);
                                } catch (Exception e) {
                                    Log.e(TAG, "convert func2 cause error", e);
                                    callback.onError(APIError.API_BIZ_FUNC2_ERROR, e.getMessage());
                                    return;
                                }

                                if (r == null) {
                                    callback.onError(APIError.API_CONTENT_EMPTY, "func2 is convert content is empty!");
                                } else {
                                    callback.onSuccess(r);
                                }
                            }
                        };
                    }
                });
            }
        });
        return resultJob;
    }
}
