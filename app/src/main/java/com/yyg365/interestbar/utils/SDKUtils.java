package com.yyg365.interestbar.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SDKUtils {

	/**
	 * 获取设备唯一ID
	 * @param context
	 * @return
	 */
	public static String getDeviceId(Context context) {
		String deviceId = null;
		TelephonyManager manager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (manager != null) {
			deviceId = manager.getDeviceId();
		}
		if (deviceId != null) {
			return "IMEI:" + deviceId;
		}
		deviceId = Secure.getString(context.getContentResolver(),
				Secure.ANDROID_ID);
		if (deviceId != null) {
			return "ANDROID:" + deviceId;
		}
		deviceId = Installation.id(context);
		return "UUID:" + deviceId;
	}
	
	/**
	 * 查询系统中已安装的app信息
	 * @param context
	 * @param all true:所有app信息，false:只返回非系统应用信息
	 * @return
	 */
	public static List<PackageInfo> getAllApps(Context context, boolean all) {
		List<PackageInfo> apps = new ArrayList<PackageInfo>();
		PackageManager pManager = context.getPackageManager();
		// 获取手机内所有应用
		List<PackageInfo> paklist = pManager.getInstalledPackages(0);
		for (int i = 0; i < paklist.size(); i++) {
			PackageInfo pak = (PackageInfo) paklist.get(i);
			if (all) {
				apps.add(pak);
			} else {
				// 判断是否为非系统预装的应用程序
				if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
					// customs applications
					apps.add(pak);
				}
			}
		}
		return apps;
	}
	
	/**
	 * 判断是否已经安装某个包的apk
	 * @param context
	 * @param packageName
	 * @return
	 */
	public static boolean isInstall(Context context, String packageName) {
		if (TextUtils.isEmpty(packageName)) {
			throw new IllegalArgumentException("packageName must not blank.");
		}
		if (context == null) {
			throw new IllegalArgumentException("context must not null.");
		}
		List<PackageInfo> apps = getAllApps(context, true);
		if (apps == null) {
			return false;
		}
		for (PackageInfo pkg : apps) {
			if (packageName.equals(pkg.packageName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 解决{@code JSONObject}对与value为null时返回“null”字符串的问题
	 * @param json
	 * @param key
	 * @return
	 */
	public static String getJsonString(JSONObject json,String key){
		if(json.isNull(key)){
			return null;
		}else{
			return json.optString(key);
		}
	}
	
	/**
	 * 给请求做HMAC签名。
	 * 
	 * @param sortedParams
	 *            所有字符型的TOP请求参数
	 * @param secret
	 *            签名密钥
	 * @return 签名
	 * @throws IOException
	 */
	public static String signRequest(TreeMap<String, String> sortedParams, String secret)
			throws IOException {
		// 第一步：把字典按Key的字母顺序排序,参数使用TreeMap已经完成排序
		Set<Entry<String, String>> paramSet = sortedParams.entrySet();

		// 第二步：把所有参数名和参数值串在一起
		StringBuilder query = new StringBuilder();
		for (Entry<String, String> param : paramSet) {
			if (!TextUtils.isEmpty(param.getKey())
					&& !TextUtils.isEmpty(param.getValue())) {
				query.append(param.getKey()).append(param.getValue());
			}
		}

		// 第三步：使用MD5/HMAC加密
		byte[] bytes = encryptHMAC(query.toString(), secret);

		// 第四步：把二进制转化为大写的十六进制
		return byte2hex(bytes);
	}
	
	private static byte[] encryptHMAC(String data, String secret)
			throws IOException {
		byte[] bytes = null;
		try {
			SecretKey secretKey = new SecretKeySpec(secret.getBytes("UTF-8"),
					"HmacMD5");
			Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			bytes = mac.doFinal(data.getBytes("UTF-8"));
		} catch (GeneralSecurityException gse) {
			String msg = getStringFromException(gse);
			throw new IOException(msg);
		}
		return bytes;
	}

	private static String getStringFromException(Throwable e) {
		String result = "";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(bos);
		e.printStackTrace(ps);
		try {
			result = bos.toString("UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// won't happen
		}
		return result;
	}

	@SuppressLint("DefaultLocale")
	private static String byte2hex(byte[] bytes) {
		StringBuilder sign = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(bytes[i] & 0xFF);
			if (hex.length() == 1) {
				sign.append("0");
			}
			sign.append(hex.toUpperCase());
		}
		return sign.toString();
	}

	/**
	 * 获取文件的真实后缀名。目前只支持JPG, GIF, PNG, BMP四种图片文件。
	 * 
	 * @param bytes
	 *            文件字节流
	 * @return JPG, GIF, PNG or null
	 */
	public static String getFileSuffix(byte[] bytes) {
		if (bytes == null || bytes.length < 10) {
			return null;
		}

		if (bytes[0] == 'G' && bytes[1] == 'I' && bytes[2] == 'F') {
			return "GIF";
		} else if (bytes[1] == 'P' && bytes[2] == 'N' && bytes[3] == 'G') {
			return "PNG";
		} else if (bytes[6] == 'J' && bytes[7] == 'F' && bytes[8] == 'I'
				&& bytes[9] == 'F') {
			return "JPG";
		} else if (bytes[0] == 'B' && bytes[1] == 'M') {
			return "BMP";
		} else {
			return null;
		}
	}

	/**
	 * 获取文件的真实媒体类型。目前只支持JPG, GIF, PNG, BMP四种图片文件。
	 * 
	 * @param bytes
	 *            文件字节流
	 * @return 媒体类型(MEME-TYPE)
	 */
	@SuppressLint("DefaultLocale")
	public static String getMimeType(byte[] bytes) {
		String suffix = getFileSuffix(bytes);
		String mimeType;

		if ("JPG".equals(suffix)) {
			mimeType = "image/jpeg";
		} else if ("GIF".equals(suffix)) {
			mimeType = "image/gif";
		} else if ("PNG".equals(suffix)) {
			mimeType = "image/png";
		} else if ("BMP".equals(suffix)) {
			mimeType = "image/bmp";
		} else {
			mimeType = "application/octet-stream";
		}

		return mimeType;
	}
	
	/*
	 * 读取 AndroidManifest.xml中的meta-data字段
	 * 注意:如果value是0-9组成的字符串，只能以数字的方式读取，因此有一些恶心的做法.
	 */
	public static String getAppMeta(Context context, String metaKey){
		ApplicationInfo appInfo;
		String value = null;
		try {
			appInfo = context.getPackageManager()
			        .getApplicationInfo(context.getPackageName(),
			        		PackageManager.GET_META_DATA);
			if (appInfo == null || appInfo.metaData == null) {
				return value;
			}
			
			value = appInfo.metaData.getString(metaKey);
			if(TextUtils.isEmpty(value)){
				value = appInfo.metaData.getInt(metaKey)+"";	
			}
			return value;
		} catch (NameNotFoundException e) {
//			PushLog.i("SDKUtils", e.toString());
		}
		return value;
	}
	
	public static boolean isDebug(Context context) {
		boolean ret = false;
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			int flag = info.applicationInfo.flags;
			if ((flag & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
				ret = true;
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return ret;
	}// 一些必须的http参数key
	
}
