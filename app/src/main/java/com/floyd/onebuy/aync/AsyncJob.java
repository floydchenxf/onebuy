package com.floyd.onebuy.aync;

import android.os.Handler;
import android.os.Looper;

import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.channel.threadpool.WxDefaultExecutor;


/**
 * Created by floyd on 15-11-19.
 */
public abstract class AsyncJob<T> {

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public abstract void start(ApiCallback<T> callback);

    public void startUI(final ApiCallback<T> callback) {
        final AsyncJob<T> source = this;
        source.start(new ApiCallback<T>() {
            @Override
            public void onError(final int code, final String errorInfo) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(code, errorInfo);
                    }
                });
            }

            @Override
            public void onSuccess(final T t) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (t == null) {
                            callback.onError(APIError.API_CONTENT_EMPTY, "数据为空");
                            return;
                        }
                        callback.onSuccess(t);
                    }
                });

            }

            @Override
            public void onProgress(final int progress) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onProgress(progress);
                    }
                });
            }
        });
    }

    public AsyncJob<T> threadOn() {
        final AsyncJob<T> source = this;

        return new AsyncJob<T>() {

            @Override
            public void start(final ApiCallback<T> callback) {
                WxDefaultExecutor.getInstance().executeHttp(new Runnable() {

                    @Override
                    public void run() {
                        source.start(callback);
                    }
                });
            }
        };
    }

    public AsyncJob<T> process(final Processor<T> processor) {
        final AsyncJob<T> source = this;

        return new AsyncJob<T>() {
            @Override
            public void start(final ApiCallback<T> callback) {
                source.start(new ApiCallback<T>() {
                    @Override
                    public void onSuccess(T result) {
                        processor.doProcess(result);
                        callback.onSuccess(result);
                    }

                    @Override
                    public void onError(int code, String e) {
                        callback.onError(code, e);
                    }

                    public void onProgress(int progress) {
                        callback.onProgress(progress);
                    }
                });
            }
        };
    }

    public <R> AsyncJob<R> map(final Func<T, R> func) {
        final AsyncJob<T> source = this;
        return new AsyncJob<R>() {
            @Override
            public void start(final ApiCallback<R> callback) {
                source.start(new ApiCallback<T>() {
                    @Override
                    public void onSuccess(T result) {
                        R mapped = func.call(result);
                        callback.onSuccess(mapped);
                    }

                    @Override
                    public void onError(int code, String e) {
                        callback.onError(code, e);
                    }

                    public void onProgress(int progress) {
                        callback.onProgress(progress);
                    }
                });
            }
        };
    }

    public <R> AsyncJob<R> flatMap(final Func<T, AsyncJob<R>> func) {
        final AsyncJob<T> source = this;
        return new AsyncJob<R>() {
            @Override
            public void start(final ApiCallback<R> callback) {
                source.start(new ApiCallback<T>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        callback.onError(code, errorInfo);
                    }

                    @Override
                    public void onSuccess(T result) {
                        AsyncJob<R> mapped = func.call(result);
                        mapped.start(new ApiCallback<R>() {
                            @Override
                            public void onSuccess(R result) {
                                callback.onSuccess(result);
                            }

                            @Override
                            public void onProgress(int progress) {
                                callback.onProgress(progress);
                            }

                            @Override
                            public void onError(int code, String e) {
                                callback.onError(code, e);
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress) {
                        callback.onProgress(progress);
                    }

                });
            }
        };
    }
}
