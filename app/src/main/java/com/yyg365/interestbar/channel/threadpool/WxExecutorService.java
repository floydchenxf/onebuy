package com.yyg365.interestbar.channel.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface WxExecutorService {

	ExecutorService getHttpExecutorService();

	ScheduledExecutorService getPriorityExecutorService();
	
	void destory();

}
