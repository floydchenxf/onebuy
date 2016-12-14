package com.yyg365.interestbar.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Method;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneInfo {

	public static final String IMEI = "imei";
	public static final String IMSI = "imsi";
	public static final String MACADDRESS = "mac_address";
	
	public static final String MAC_ADDRESS_PATTERN= "^[a-f0-9A-F:]+$";

	private static String generateImei() {
		StringBuilder imei = new StringBuilder();
		// 添加当前秒数 毫秒数 5位
		long time = System.currentTimeMillis();
		String currentTime = Long.toString(time);
		imei.append(currentTime.substring(currentTime.length() - 5));

		// 手机型号 6位
		StringBuilder model = new StringBuilder();
		model.append(Build.MODEL.replaceAll(" ", ""));
		while (model.length() < 6) {
			model.append('0');
		}
		imei.append(model.substring(0, 6));

		// 随机数 4位
		Random random = new Random(time);
		long tmp = 0;
		while (tmp < 0x1000) {
			tmp = random.nextLong();
		}

		imei.append(Long.toHexString(tmp).substring(0, 4));

		return imei.toString();

	}

	/**
	 * 获取imei，如果系统不能获取，则将动态产生一个唯一标识并保存
	 * 
	 * @param context
	 *            Context实例
	 * @return imsi字串
	 */
	public static String getImei(Context context) {
		String imei = null;
		SharedPreferences sp = context.getSharedPreferences(IMEI,
				Context.MODE_PRIVATE);
		imei = sp.getString(IMEI, null);
		if (imei == null || imei.length() == 0) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Activity.TELEPHONY_SERVICE);
			imei = tm.getDeviceId(); // 获取imei的方法修改
			if (imei == null || imei.length() == 0) {
				imei = generateImei();
			}
			imei = imei.replaceAll(" ", "").trim();
			// imei 小于15位补全 jiuwan
			while (imei.length() < 15) {
				imei = "0" + imei;
			}
			Editor editor = sp.edit();
			editor.putString(IMEI, imei);
			editor.commit();
		}
		return imei.trim();
	}

	/**
	 * 获取imsi，如果系统不能获取，则将动态产生一个唯一标识并保存
	 * 
	 * @param context
	 *            ： Context实例
	 * @return imsi字串
	 */
	static public String getImsi(Context context) {
		String imsi = null;
		SharedPreferences sp = context.getSharedPreferences(IMEI,
				Context.MODE_PRIVATE);
		imsi = sp.getString(IMSI, null);
		if (imsi == null || imsi.length() == 0) {
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Activity.TELEPHONY_SERVICE);
			imsi = tm.getSubscriberId();
			if (imsi == null || imsi.length() == 0) {
				imsi = generateImei();
			}
			imsi = imsi.replaceAll(" ", "").trim();
			// imei 小于15位补全 jiuwan
			while (imsi.length() < 15) {
				imsi = "0" + imsi;
			}
			Editor editor = sp.edit();
			editor.putString(IMSI, imsi);
			editor.commit();
		}
		return imsi;
	}

	/**
	 * 获取wifi 模块mac地址
	 * 
	 * @param context
	 *            ： Context实例
	 * @return wifi模块mac地址
	 */
	public static String getLocalMacAddress(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = null;
		try {
			info = wifi.getConnectionInfo();
		} catch(Exception e) {
			Log.e(PhoneInfo.class.getSimpleName(), e.getMessage());
		}
		
		if (info == null) {
			return "";
		}
		
		String wifiaddr = info.getMacAddress();

		if (wifiaddr == null || "".equals(wifiaddr)) {
			SharedPreferences sp = context.getSharedPreferences(MACADDRESS,
					Context.MODE_PRIVATE);
			wifiaddr = sp.getString(MACADDRESS, "");
		} else {
			SharedPreferences sp = context.getSharedPreferences(MACADDRESS,
					Context.MODE_PRIVATE);
			Editor editor = sp.edit();
			editor.putString(MACADDRESS, wifiaddr);
			editor.commit();
		}
		
		if (!TextUtils.isEmpty(wifiaddr)) {
			Pattern r = Pattern.compile(MAC_ADDRESS_PATTERN);
			Matcher m = r.matcher(wifiaddr);
			if (!m.find()) {
				wifiaddr = "";
			}
		}
		return wifiaddr;
	}

	/**
	 * 获取原始的imei，如果没有返回空字符串，
	 * 
	 * @param context
	 *            : Context实例
	 * @return 手机原生imei，获取失败则返回null
	 */
	static public String getOriginalImei(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		if (imei != null)
			imei = imei.trim();
		return imei;
	}

	/**
	 * 获取原始的imsi，如果没有返回空字符串，
	 * 
	 * @param context
	 *            : Context实例
	 * @return 原生imsi，获取失败则返回null
	 */
	static public String getOriginalImsi(Context context) {

		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Activity.TELEPHONY_SERVICE);
		String imsi = tm.getSubscriberId();
		if (imsi != null)
			imsi = imsi.trim();
		return imsi;
	}

	public static String getSerialNum() {
		String serialnum = null;

		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			serialnum = (String) (get.invoke(c, "ro.serialno", "unknown"));
		} catch (Exception ignored) {
		}

		return serialnum;
	}

	public static String getAndroidId(Context context) {
		String androidId = Settings.Secure.getString(
				context.getContentResolver(), Settings.Secure.ANDROID_ID);
		return androidId;
	}

	public static String getBrand() {
		return Build.BRAND;
	}
	
	public static int getOsVersion() {
		return Build.VERSION.SDK_INT;
	}

	/**
	 * 获取手机型号
	 * 
	 * @return
	 */
	public static String getPhoneModel() {
		return Build.MODEL;
	}

	/**
	 * 获取cpu信息
	 * 
	 * @return
	 */
	public static String getCPUInfo() {
		return Build.CPU_ABI;
	}

}
