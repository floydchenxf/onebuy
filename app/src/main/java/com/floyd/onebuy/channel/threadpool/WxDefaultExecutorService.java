package com.floyd.onebuy.channel.threadpool;

import android.util.Log;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WxDefaultExecutorService implements WxExecutorService {

	private static WxDefaultExecutorService executor;

	private ScheduledExecutorService priorityExecutorService;
	private ThreadPoolExecutor httpExecutorService;
	private LinkedBlockingQueue<Runnable> httpBlockingQueue;

	public WxDefaultExecutorService() {
	};

	public static synchronized WxDefaultExecutorService getInstance() {
		if (executor == null) {
			executor = new WxDefaultExecutorService();
			executor.init();
		}

		return executor;
	}

	public void init() {
		priorityExecutorService = new ScheduledThreadPoolExecutor(2,
				new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "priority-thread");
					}
				}) {
			@Override
			public Future<?> submit(final Runnable task) {
				Runnable wrappedTask = new Runnable() {
					@Override
					public void run() {
						try {
							task.run();
						} catch (Throwable e) {
							Log.e("priorityExecutorService", e.getMessage());
						}
					}
				};
				return super.submit(wrappedTask);
			}
		};

		httpBlockingQueue = new LinkedBlockingQueue<Runnable>();
		httpExecutorService = new ThreadPoolExecutor(6, 30, 30,
				TimeUnit.SECONDS, httpBlockingQueue, new ThreadFactory() {

					@Override
					public Thread newThread(Runnable r) {
						return new Thread(r, "http-thread");
					}

				}) {


			@Override
			public Future<?> submit(final Runnable task) {
				Runnable wrappedTask = new Runnable() {
					@Override
					public void run() {
						try {
							task.run();
						} catch (Throwable e) {
							Log.e("httpExecutorService", e.getMessage());
						}
					}
				};
				return super.submit(wrappedTask);
			}
		};

	}

	public void destory() {
		if (priorityExecutorService != null) {
			priorityExecutorService.shutdown();
		}

		if (httpBlockingQueue != null) {
			httpBlockingQueue.clear();
		}

		if (httpExecutorService != null) {
			httpExecutorService.shutdown();
		}
	}

	public ThreadPoolExecutor getHttpExecutorService() {
		return httpExecutorService;
	}

	public ScheduledExecutorService getPriorityExecutorService() {
		return priorityExecutorService;
	}

}
