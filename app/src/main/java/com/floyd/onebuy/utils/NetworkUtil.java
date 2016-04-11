package com.floyd.onebuy.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * 检查网络是否可用.不知道放哪里好，先放到调用包里。
 * 
 * @author floyd.chenxf
 * 
 */
public class NetworkUtil {

	/**
	 * 获取当前网络是否可用
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo networkInfo = getNetworkInfo(context);
		if (networkInfo == null) {
			return false;
		}
		return networkInfo.isConnectedOrConnecting();
	}

	/**
	 * 获取是否是wifi连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isWifi(Context context) {
		NetworkInfo networkInfo = getNetworkInfo(context);
		if (networkInfo.isAvailable()) {
			return networkInfo.getType() == ConnectivityManager.TYPE_WIFI
					&& networkInfo.getState() == State.CONNECTED;
		}

		return false;
	}

	/**
	 * 获取网络类型名称，如:wifi mobile
	 * 
	 * @param context
	 * @return
	 */
	public static String getNetworkType(Context context) {
		NetworkInfo networkInfo = getNetworkInfo(context);
		return networkInfo.getTypeName();
	}

	public static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		return networkInfo;
	}

	/**
	 * 获取wifi info
	 * 
	 * @param context
	 * @return
	 */
	public static WifiInfo getWifiInfo(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info;
	}

}
