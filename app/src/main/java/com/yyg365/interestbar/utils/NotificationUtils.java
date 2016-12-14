package com.yyg365.interestbar.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.yyg365.interestbar.ui.R;


public class NotificationUtils {

	private static long lastShowTime = 0;
	private static Handler handler = new Handler(Looper.getMainLooper());

	public static void showToast(final String msg, final Context context) {
		showToast(msg,context, Toast.LENGTH_SHORT);
	}

	public static void showToast(final String msg, final Context context,int duration) {
		if ((context instanceof Activity && !((Activity) context).isFinishing())
				|| (context instanceof Application)) {
		
			final int tempDuration = duration;
			handler.post(new Runnable() {

				@Override
				public void run() {
					// AUTO_TODO Auto-generated method stub
					Toast toast = Toast.makeText(context, msg, tempDuration);
					toast.show();
				}
			});
		}
	}
	
	public static void showToast(final int msg, final Context context, int duration) {
		if ((context instanceof Activity && !((Activity) context).isFinishing())
				|| (context instanceof Application)) {
			final int tempDuration = duration;
			handler.post(new Runnable() {

				@Override
				public void run() {
					// AUTO_TODO Auto-generated method stub
					if ((R.string.net_null == msg)
							&& (lastShowTime != 0)
							&& (System.currentTimeMillis() - lastShowTime <= 3000)) { // 防止在短时间内重复显示无网络提示
						return;
					}
					if (R.string.net_null == msg) {
						lastShowTime = System.currentTimeMillis();
					}
					Toast toast = Toast.makeText(context, msg, tempDuration);
					toast.show();
				}
			});
		}
	}
	
	public static void showToast(final int msg, final Context context) {
		showToast(msg, context, Toast.LENGTH_SHORT);
	}
}
