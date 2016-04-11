package com.floyd.onebuy.channel.threadpool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public interface WxExecutorService {

	ExecutorService getHttpExecutorService();

	ScheduledExecutorService getPriorityExecutorService();
	
	void destory();

}
