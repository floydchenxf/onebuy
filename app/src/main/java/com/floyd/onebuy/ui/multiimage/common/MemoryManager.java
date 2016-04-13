package com.floyd.onebuy.ui.multiimage.common;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.os.Process;
import android.util.Log;

import com.floyd.onebuy.IMChannel;
import com.floyd.onebuy.utils.ThreadHandler;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 负责内存管理和监控
 *
 * @author yuanyi.rss
 */
public class MemoryManager {

    private static final String TAG = MemoryManager.class.getSimpleName();

    private Map<String, MemoryManagerListener> mListeners = new HashMap<String, MemoryManagerListener>();
    ;

    private static MemoryManager mInstance = new MemoryManager();

    private static final float thresholdPercent = 0.8f; // 内存阈值占比，80%暂定

    private long mThresholdMemory = 0;
    private long mMaxMemory = 0;

    private int[] PID;
    private ActivityManager mActivityManager;

    private static volatile long lastCheckTime = 0;
    private static final long CheckTimeInterval = 10000;

    private MemoryManager() {
    }

    public static MemoryManager getInstance() {
        return mInstance;
    }

    public void init() {
        if (mMaxMemory == 0) {
            mMaxMemory = getMaxMem();
            PID = new int[]{Process.myPid()};
            mActivityManager = (ActivityManager) IMChannel.getApplication()
                    .getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ThreadHandler.getInstance().getHandler().post(tbsRunnable);
        }
        if (mThresholdMemory == 0) {
            mThresholdMemory = (long) (mMaxMemory * thresholdPercent);
        }
    }

    public static long getMaxMem() {
        long maxRunMemory = Runtime.getRuntime().maxMemory();
        Log.d(TAG, "maxRunMemory:" + maxRunMemory);
        long memClassInt = 0;
        ActivityManager am = (ActivityManager) IMChannel.getApplication()
                .getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            int memClass = am.getMemoryClass();
            memClassInt = memClass * 1024 * 1024;
        }
        Log.d(TAG, "memClassInt:" + memClassInt);

        long maxM = 0;
        if (memClassInt < maxRunMemory)
            maxM = memClassInt;
        else
            maxM = maxRunMemory;
        return maxM;
    }

    public static long getVmMemRemained() {
        return Runtime.getRuntime().maxMemory() - getVMAlloc();
    }

    public static long getVMAlloc() {
        Runtime rt = Runtime.getRuntime();
        return rt.totalMemory() - rt.freeMemory();
    }

    public static long getNativeHeapSize() {
        return Debug.getNativeHeapSize();
    }

    /**
     * 内存管理监听接口
     */
    public interface MemoryManagerListener {

        /**
         * 获取监听者占用的内存
         */
        public int onGetMemory();

        /**
         * 响应内存不够时候，监听者应该释放内存
         */
        public void onLowMemory();
    }

    /**
     * 内存检测，如果内存不足，发起gc，通知监听者释放内存
     */
    public synchronized void memoryCheck() {
//        long currentTime = System.currentTimeMillis();
//        if (currentTime - lastCheckTime < CheckTimeInterval) {
//            return;
//        }
//        lastCheckTime = currentTime;
//        ThreadPoolMgr.getInstance().doAsyncRun(new Runnable() {
//            @Override
//            public void run() {
//                init();
//                long currentVmAllocSize = getVMAlloc();
//                long nativeHeapSize = getNativeHeapSize();
//
//                if (currentVmAllocSize + nativeHeapSize >= mThresholdMemory) {
//                    System.gc();
//                } else {
//                    return;
//                }
//
//                currentVmAllocSize = getVMAlloc();
//                nativeHeapSize = getNativeHeapSize();
//                if (currentVmAllocSize + nativeHeapSize >= mThresholdMemory) {
//                    onLowMemory();
//                } else {
//                    return;
//                }
//            }
//        });
    }

    /**
     * 内存不够时候，通知各个监听者释放内存
     */
    private synchronized void onLowMemory() {
        Log.d(TAG, "onLowMemory:" + memStatisticsToString());
        Iterator<Entry<String, MemoryManagerListener>> iter = mListeners
                .entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, MemoryManagerListener> entry = iter.next();
            MemoryManagerListener val = (MemoryManagerListener) entry
                    .getValue();
            val.onLowMemory();
        }
    }

    public synchronized void addListener(String name,
                                         MemoryManagerListener listener) {
        mListeners.put(name, listener);
    }

    public synchronized void removeListener(String name) {
        mListeners.remove(name);
    }

    public synchronized String memStatisticsToString() {
        try {
            StringBuilder memStat = new StringBuilder();
            memStat.append("MaxMem:");
            memStat.append(getMaxMem());
            memStat.append(" VMAlloc:");
            memStat.append(getVMAlloc());
            memStat.append(" NativeHeapSize");
            memStat.append(getNativeHeapSize());
            Iterator<Entry<String, MemoryManagerListener>> iter = mListeners
                    .entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, MemoryManagerListener> entry = iter.next();
                MemoryManagerListener val = (MemoryManagerListener) entry
                        .getValue();
                int memSize = val.onGetMemory();
                memStat.append(" ");
                memStat.append(entry.getKey());
                memStat.append(" ");
                memStat.append(memSize);
            }
            // userTrack不能包含\n的字符,SHIT！！！
            // memStat.append("\r\n");
            return memStat.toString();
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return null;
    }

    private Runnable tbsRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d("test", "MemoryManager tbsRunnable");
            long currentVmAllocSize = getVMAlloc();
            long nativeHeapSize = getNativeHeapSize();

            Debug.MemoryInfo[] memInfo = mActivityManager
                    .getProcessMemoryInfo(PID);
            if (memInfo != null && memInfo[0] != null) {
                int totalPss = memInfo[0].getTotalPss();
                ThreadHandler.getInstance().getHandler()
                        .postDelayed(this, 3600000);

            }
        }
    };

}
