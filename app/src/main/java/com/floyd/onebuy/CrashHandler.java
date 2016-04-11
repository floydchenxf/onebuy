package com.floyd.onebuy;

import android.os.Process;
import android.util.Log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

public class CrashHandler implements UncaughtExceptionHandler {

	private static CrashHandler instance = new CrashHandler();

	private UncaughtExceptionHandler mDefaultCrashHandler;

	private CrashHandler() {
	};

	public static CrashHandler getInstance() {
		return instance;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		StringWriter sw = null;
		try {
			sw = new StringWriter();
			ex.printStackTrace(new PrintWriter(sw));
			String value = sw.toString();
			LogClickedOperator logClickOperator = LogClickedOperator.getInstance();
			logClickOperator.writeLog(value, 1);
		} catch (Exception e) {
			Log.e("crashHandler", e.getMessage());
		} finally {
			try {
				sw.close();
			} catch (IOException e) {
			}
		}

		if (mDefaultCrashHandler != null) {
			mDefaultCrashHandler.uncaughtException(thread, ex);
		} else {
			Process.killProcess(Process.myPid());
		}

	}

	public void init() {
		// 获取系统默认的异常处理器
		mDefaultCrashHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
}
