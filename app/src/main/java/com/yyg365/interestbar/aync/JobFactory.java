package com.yyg365.interestbar.aync;

/**
 * Created by floyd on 15-11-21.
 */
public class JobFactory {

    public static <T> AsyncJob<T> createJob(final T t) {
        return new AsyncJob<T>() {
            @Override
            public void start(ApiCallback<T> callback) {
                callback.onSuccess(t);
            }
        };
    }

    public static <T> AsyncJob<T> createAsyncJob(AsyncJob<T> job) {
        return job.threadOn();
    }
}
