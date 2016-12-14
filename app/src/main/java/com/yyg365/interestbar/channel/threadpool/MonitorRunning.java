package com.yyg365.interestbar.channel.threadpool;

import android.util.Log;


public class MonitorRunning implements Runnable {
	private Runnable runnable;

	public MonitorRunning(Runnable runnable) {
		this.runnable = runnable;
	}

	@Override
	public void run() {
		long a = System.currentTimeMillis();
		runnable.run();
		long b = System.currentTimeMillis();
		Log.d("MonitorRunning", "running " + runnable.toString() + " spent time " + (b - a) + " ms");
	}
}
