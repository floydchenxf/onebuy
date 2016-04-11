package com.floyd.onebuy.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * sdk通用util方法
 * 
 * @author jiangxiang.jx
 */
public class WXUtil {

	private static final String TAG = "WXUtil";

	private static final long UA = 'W';
	private static short seqid; // UUID生成规则中用到的seqID

	private static int toUnsigned(short s) {
		return s & 0xFFFF;
	}

	/**
	 * 生成UUID，该uuid可用于消息的msgid
	 * 
	 * @return
	 */
	public synchronized static long getUUID() {
		long time = System.currentTimeMillis() / 1000;
		long uuid = (UA << 56) | ((time << 16) & 0xFFFFFFFF)
				| toUnsigned(seqid++);
		return uuid;
	}

	private static final char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	/**
	 * @hide
	 * @param b
	 * @return
	 */
	public static String toHexString(byte[] b) { // String to byte
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * 获取指定字符的md5值
	 * 
	 * @param str
	 *            需要编码的字符
	 * @param fix
	 *            混淆用字符
	 * @return
	 */
	public static String getMD5Value(String str, String fix) {
		return getMD5Value(str, fix, "utf-8");

	}

	public static String getMD5Value(String str, String fix, String charsetName) {
		if (str != null && fix != null) {
			String formalString = str + fix;
			try {
				MessageDigest algorithm = MessageDigest.getInstance("MD5");
				algorithm.reset();
				try {
					algorithm.update(formalString.getBytes(charsetName));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					Log.e("WxException",e.getMessage(), e);
					return null;
				}
				byte messageDigest[] = algorithm.digest();

				return toHexString(messageDigest);
			} catch (NoSuchAlgorithmException e) {
				Log.w(TAG, e);
				Log.e("WxException",e.getMessage(), e);
			}
		}
		return null;

	}

	/**
	 * 获取指定字符的md5值
	 * 
	 * @param str
	 *            需要编码的字符
	 * @return
	 */
	public static String getMD5Value(String str) {
		return getMD5Value(str, "");
	}

	/**
	 * 生成文件的md5
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileMd5Hash(String fileName) {
		InputStream fis = null;
		try {
			fis = new FileInputStream(fileName);
			byte[] buffer = new byte[1024];
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			int numRead = 0;
			while ((numRead = fis.read(buffer)) > 0) {
				md5.update(buffer, 0, numRead);
			}
			fis.close();
			return toHexString(md5.digest());
		} catch (FileNotFoundException e) {
			Log.w(TAG, e);
			Log.e("WxException",e.getMessage(), e);
		} catch (IOException e) {
			Log.w(TAG, e);
			Log.e("WxException",e.getMessage(), e);
		} catch (NoSuchAlgorithmException e) {
			Log.w(TAG, e);
			Log.e("WxException",e.getMessage(), e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					Log.e("WxException",e.getMessage(), e);
				}
			}
		}

		return "";
	}

	/**
	 * 生成文件的crc32校验码
	 * 
	 * @param file
	 * @return
	 */
	public static String getCRC32(File file) {
		CRC32 crc32 = new CRC32();
		CheckedInputStream checkedinputstream = null;
		String crc = null;
		try {
			checkedinputstream = new CheckedInputStream(new FileInputStream(
					file), crc32);
			byte[] buf = new byte[1024];
			while (checkedinputstream.read(buf) >= 0) {
			}
			crc = Long.toHexString(crc32.getValue()).toUpperCase();
		} catch (Exception e) {
			Log.w(TAG, e);
			Log.e("WxException",e.getMessage(), e);
		} finally {
			if (checkedinputstream != null) {
				try {
					checkedinputstream.close();
				} catch (IOException e) {
				}
			}
		}
		return crc;
	}

	private static final Pattern sPattern = Pattern
			.compile(".*filename=(.+?)&.*thumbnail=(.+?)&");
	/**
	 * 从服务器生成的文件中获取对应的md5值
	 * 
	 * @param filePathName
	 * @return
	 */
	public static String getMD5FileName(String filePathName) {
		if (!TextUtils.isEmpty(filePathName)) {
			Matcher matcher = sPattern.matcher(filePathName);
			if (matcher.find()) {
				return matcher.group(1) + matcher.group(2);

			}
		}
		return getMD5Value(filePathName);
	}

	/**
	 * 从文件路径字符串获取文件名字
	 * 
	 * @param filePathName
	 * @return
	 */
	public static String getFileName(String filePathName) {
		if (!TextUtils.isEmpty(filePathName)) {
			int index = filePathName.lastIndexOf('/');
			if (index != -1 && (index + 1) < filePathName.length()) {
				String name = filePathName.substring(index + 1);
				if (!TextUtils.isEmpty(name)) {
					int subIndex = name.lastIndexOf(".");
					if (subIndex != -1) {
						return name.substring(0, subIndex);
					}
				}
				return name;
			}
		}
		return "";
	}

	/**
	 * @param url
	 * @return true 是gif图片
	 */
	public static boolean isGif(String url) {
		if (!TextUtils.isEmpty(url)) {
			int index = url.lastIndexOf("suffix=");
			if (index >= 0 && index + 10 <= url.length()) {
				String type = url.substring(index + 7, index + 10);
				if ("gif".equals(type)) {
					return true;
				}
			}
			if (url.contains(".gif")) {
				return true;
			}
			if (url.contains("format=gif")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 获取当前应用运行的进程名
	 *
	 * @param context
	 * @return
	 */
	public static String getCurProcessName(Context context) {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		if (activityManager == null) {
			return null;
		}

		List<RunningAppProcessInfo> runningProcessList = activityManager
				.getRunningAppProcesses();
		if (runningProcessList == null || runningProcessList.isEmpty()) {
			return null;
		}

		for (ActivityManager.RunningAppProcessInfo appProcess : runningProcessList) {
			if (appProcess.pid == pid) {
				return appProcess.processName;
			}
		}
		return null;
	}

	public static String getMainProcessName(Context context) {
		Intent it = new Intent(Intent.ACTION_MAIN);
		it.setPackage(context.getPackageName());
		ResolveInfo info = context.getPackageManager().resolveActivity(it, PackageManager.GET_RESOLVED_FILTER);
		String processName = null;
		if (info != null && info.activityInfo != null) {
			processName = info.activityInfo.processName;
		} else {
			processName = context.getPackageName();
		}

		return processName;
	}
	

	public static boolean isMainProcess(Context context) {
		String currentProcessName = getCurProcessName(context);
		if (TextUtils.isEmpty(currentProcessName)) {
			return false;
		}
		String mainPrcocessName = getMainProcessName(context);
		final String packageName = context.getPackageName();
		Log.i("WxUtil", "current process name:" + currentProcessName + "---main process name:" + mainPrcocessName);
		return currentProcessName.equals(mainPrcocessName) || currentProcessName.equals(packageName);
	}

	public static boolean isDebug(Context context) {
		boolean ret = false;
		if(context == null) return false;
		PackageManager manager = context.getPackageManager();
		try {
			PackageInfo info = manager.getPackageInfo(context.getPackageName(),
					0);
			int flag = info.applicationInfo.flags;
			if ((flag & ApplicationInfo.FLAG_DEBUGGABLE) == ApplicationInfo.FLAG_DEBUGGABLE) {
				ret = true;
			}
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("WxException",e.getMessage(), e);
		}catch (Throwable e) {
			// TODO: handle exception
			return false;
		}
		return ret;
	}// 一些必须的http参数key


	public static boolean isEmpty(String str){
		if(str == null){
			return true;
		}
		for(int i = 0, length = str.length(); i < length; i ++){
			if(!Character.isWhitespace(str.charAt(i))){
				return false;
			}
		}
		return true;
	}

}
