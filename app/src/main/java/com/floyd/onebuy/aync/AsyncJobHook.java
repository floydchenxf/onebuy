package com.floyd.onebuy.aync;

/**
 * Created by floyd on 16-2-13.
 */
public class AsyncJobHook {

    public <T> AsyncJob<T> onCreate(AsyncJob<T> f) {
        return f;
    }
}
