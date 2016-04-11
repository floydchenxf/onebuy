package com.floyd.onebuy.channel.threadpool;

import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


public class WxDefaultExecutor implements WxExecutor {
	
	private static final String TAG = WxDefaultExecutor.class.getSimpleName();
	private WxDefaultExecutor() {
	};

	private WxExecutorService executorService;
	private Future future;

	public static WxExecutor getInstance() {
		WxDefaultExecutor executor = new WxDefaultExecutor();
		executor.executorService = WxDefaultExecutorService.getInstance();
		return executor;
	}

	@Override
	public WxExecutor submitHighPriority(Runnable runnable) {
		Log.d(TAG, "----high priority running:" + runnable.getClass().getName());
		future = executorService.getPriorityExecutorService().submit(runnable);
		return this;
	}

	@Override
	public WxExecutor submitNormalPriority(Runnable runnable) {
		Log.d(TAG, "----begin normal priority running:" + runnable);
		return submit(runnable, 5000);
	}

	@Override
	public WxExecutor submitLowPriority(Runnable runnable) {
		Log.d(TAG, "----begin low priority running:" + runnable.getClass().getName());
		return submit(runnable, 20000);
	}
	
	public WxExecutor submit(Runnable runnable, int delayTime) {
		future = null;
		executorService.getPriorityExecutorService().schedule(runnable, delayTime, TimeUnit.MICROSECONDS);
		return this;
	}

	public Future getFuture() {
		return future;
	}

	public void cancel(boolean mayInterruptIfRunning) {
		Log.d(TAG, "cancel request");
		if (future != null) {
			future.cancel(mayInterruptIfRunning);
		}
	}

	public void cancel() {
		this.cancel(true);
	}

	@Override
	public WxExecutor submitHttp(Runnable runnable) {
		Log.d(TAG, "----http running:" + runnable.getClass().getName());
		future = executorService.getHttpExecutorService().submit(runnable);
		return this;
	}

	@Override
	public boolean isCancelled() {
		if (future == null) {
			return false;
		}
		return future.isCancelled();
	}

	@Override
	public void executeLocal(Runnable runnable) {
		Log.d(TAG, "----Local running:" + runnable.getClass().getName());
		MonitorRunning monitorRunning = new MonitorRunning(runnable);
		new Thread(monitorRunning).start();
	}

	@Override
	public void executeHttp(Runnable runnable) {
		executorService.getHttpExecutorService().execute(runnable);
	}
}
