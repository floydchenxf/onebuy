package com.floyd.onebuy.biz.func;

import android.util.Log;

import com.floyd.onebuy.aync.Func;

/**
 * Created by floyd on 15-11-28.
 */
public class StringFunc implements Func<byte[], String> {
    private static final String TAG = "StringFunc";
    @Override
    public String call(byte[] t) {
        String result = new String(t);
        Log.i(TAG, result);
        return result;
    }
}
