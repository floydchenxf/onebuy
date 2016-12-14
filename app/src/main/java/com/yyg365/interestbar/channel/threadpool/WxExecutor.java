package com.yyg365.interestbar.channel.threadpool;


public interface WxExecutor extends ExecutedTask {

	WxExecutor submitHighPriority(Runnable runnable);

	WxExecutor submitNormalPriority(Runnable runnable);

	WxExecutor submitLowPriority(Runnable runnable);

	WxExecutor submitHttp(Runnable runnable);

	WxExecutor submit(Runnable runnable, int delayTime);

	void executeHttp(Runnable runnable);

	void executeLocal(Runnable runnable);

}
