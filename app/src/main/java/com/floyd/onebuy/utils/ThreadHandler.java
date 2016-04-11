package com.floyd.onebuy.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;


/**
 * 监于目前常驻内存的工作线程handler太多，占用了不必要的内存空间
 * 故有此对象的诞生，此handler与进程的生命周期等同，不需要另外的quit操作，切记切记！！
 *
 * @author zhaoxu
 */
public class ThreadHandler {
    private static String TAG = "WxThreadHandler";

    private static class LazyHolder {
        private static final ThreadHandler INSTANCE = new ThreadHandler();
    }

    public static ThreadHandler getInstance() {
        return LazyHolder.INSTANCE;
    }

    private Handler mHandler;

    private ThreadHandler() {
        init();
    }

    public Handler getHandler() {
        return mHandler;
    }

    public Looper getLooper() {
        return mHandler.getLooper();
    }

    private void init() {
        Log.d(TAG, "WxThreadHandler init");

        HandlerThread thread = new HandlerThread("WxThreadHandler");
        thread.start();
        mHandler = new Handler(thread.getLooper());
    }
}