package com.yyg365.interestbar.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;

import com.yyg365.interestbar.biz.constants.EnvConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class Tools {

	public static final String imageRootPath = EnvConstants.imageRootPath;

	public static boolean isAppInstalled(Context context, String packageName) {
		PackageInfo packageInfo = null;
		if (TextUtils.isEmpty(packageName)) {
			return false;
		}
		PackageManager packageManager = context.getPackageManager();
		List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
		if (packageInfos == null) {
			return false;
		}
		for (int index = 0; index < packageInfos.size(); index++) {
			packageInfo = packageInfos.get(index);
			String name = packageInfo.packageName;
			if (packageName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public static void saveBitmap(Bitmap destBitmap, String filePath)
			throws IOException {
		File dir = new File(imageRootPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		File f = new File(filePath);
		f.createNewFile();
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(f);
			// 每当执行到这时就抛出异常FileNotFoundException
			destBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String processText(String shareText) {
		String text;
		if (shareText != null) // 在分享链接至微信朋友圈时，文本中如果有\n，朋友圈中只能显示\n之前的部分。所以需要把\n过滤掉。
			text = shareText.replace('\n', ' ');
		else {
			text = "";
		}
		return text;
	}

	public static Bitmap getViewBitmap(View v) {

		v.clearFocus(); //
		v.setPressed(false); //

		// 能画缓存就返回false
		boolean willNotCache = v.willNotCacheDrawing();
		v.setWillNotCacheDrawing(false);
		int color = v.getDrawingCacheBackgroundColor();
		v.setDrawingCacheBackgroundColor(0);
		if (color != 0) {
			v.destroyDrawingCache();
		}
		v.buildDrawingCache();
		Bitmap cacheBitmap = v.getDrawingCache();
		if (cacheBitmap == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

		// Restore the view
		v.destroyDrawingCache();
		v.setWillNotCacheDrawing(willNotCache);
		v.setDrawingCacheBackgroundColor(color);
		return bitmap;
	}

//	public static Bitmap getBitmapFromView(final View view) {
//		view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
//				MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//		view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				// 获得绘图缓存中的Bitmap
//				Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
//						view.getHeight(), Bitmap.Config.ARGB_8888);
//				Canvas c = new Canvas(bitmap);
//				view.draw(c);
//				return bitmap;
//				final String filePath = Tools.imageRootPath
//						+ System.currentTimeMillis() + ".jpg";
//				try {
//					Tools.saveBitmap(bitmap, filePath);
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				bitmap.recycle();
//				mShareData.setText(Tools.processText(mText));
//				mShareData.setPicLocalUrl(filePath);
//				mShareData.setPicUri(Uri.fromFile(new File(filePath)));
//				Log.e(TAG, "分享数据准备完毕");
//				mIsDataPrepared = true;
//
//			}
//		}).start();
//	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
//	public static void deleteFile(){
//		boolean isDeleted = false;
//		if (imageFilePath != null) {
//			File file = new File(imageFilePath);
//			if (file != null) {
//				if (file.delete()) {
//					isDeleted = true;
//				}
//			}
//		}
//		if (isDeleted) {
//			WxLog.e(ShareSDK.TAG, "删除文件成功！");
//		}
//	}

}
