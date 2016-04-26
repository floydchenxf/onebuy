package com.floyd.onebuy.biz.manager;

import android.os.SystemClock;

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
            return System.currentTimeMillis();
        }
    }

}
