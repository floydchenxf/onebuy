package com.floyd.onebuy.biz.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileTools {

	public static final String TAG = FileTools.class.getSimpleName();

	/**
	 * 创建图片临时文件
	 * 
	 * @return
	 */
	public static File createImageFile(String rootPath) {
		File file = new File(rootPath);
		File imageFile = null;
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			imageFile = File.createTempFile(
					"image_" + System.currentTimeMillis(), ".jpg", file);
		} catch (IOException e) {
			Log.w(TAG, e);
			Log.w(TAG, e);
		}
		return imageFile;
	}

	/**
	 * 创建录音的音频文件
	 * 
	 * @return
	 */
	public static File createAudioFile(String rootPath) {
		File file = new File(rootPath);
		File audioFile = null;
		if (!file.exists()) {
			file.mkdirs();
		}
		try {
			audioFile = File.createTempFile(
					"record_" + System.currentTimeMillis(), "", file);
		} catch (IOException e) {
			Log.w(TAG, e);
			Log.w(TAG, e);
		}
		return audioFile;
	}

	public static Bitmap readBitmap(String pathName) {
		return readBitmap(pathName, null);
	}

	/**
	 * 读取图片
	 * 
	 * @param pathName
	 * @param options
	 * @return
	 */
	public static Bitmap readBitmap(String pathName, BitmapFactory.Options options) {
		if (pathName == null) {
			return null;
		}
		InputStream in = FileUtils.readFile(pathName);
		if (in != null) {
			Bitmap bitmap = null;
			try {
				bitmap = BitmapFactory.decodeStream(in, null, options);
			} catch (OutOfMemoryError e) {
				Log.w(TAG, e);
			} catch (Throwable e) {
				Log.w(TAG, e);
			}
			try {
				in.close();
			} catch (IOException e) {
				Log.w(TAG, e);
				Log.w(TAG, e);
			}
			return bitmap;
		}
		return null;
	}
	
	public static void writeFile(String path, String name, byte[] data) {
		if (data == null || data.length <= 0) {
			return;
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		File _file = new File(path, name);
		if (_file.exists()) {
			_file.delete();
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(_file);
			fos.write(data);
			fos.flush();
		} catch (FileNotFoundException e) {
			Log.w(TAG, e);
			Log.w(TAG, e);
		} catch (IOException e) {
			Log.w(TAG, e);
			Log.w(TAG, e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.w(TAG, e);
				}
			}
		}
	}

	public static Bitmap decodeBitmap(String localName) {
		return decodeBitmap(localName, null);
	}

	public static Bitmap decodeBitmap(String localName,BitmapFactory.Options options) {

		Bitmap bitmap = null;
		int tryTime = 0;

		while (bitmap == null && tryTime < 3) {
			try {
				bitmap = readBitmap(localName, options);
			} catch (OutOfMemoryError oe) {
				Log.e(TAG, "decodeBitmap", oe);
			} catch (Throwable throwable) {
				Log.e(TAG, "decodeBitmap", throwable);
			}

			tryTime++;
		}
		return bitmap;
	}

	public static Bitmap decodeBitmap(byte[] data) {
		return decodeBitmap(data, null);
	}

	public static Bitmap decodeBitmap(byte[] data, BitmapFactory.Options options) {

		Bitmap bitmap = null;
		int tryTime = 0;

		ByteArrayInputStream bais = new ByteArrayInputStream(data);

		while (bitmap == null && tryTime < 3) {
			try {
				bitmap = BitmapFactory.decodeStream(bais);
			} catch (OutOfMemoryError oe) {
				Log.e(TAG, "decodeBitmap", oe);
//				BitmapCache.clearCache();
			}
			tryTime++;
		}
		return bitmap;
	}

	/**
	 * 保存图片为jpg
	 *
	 * @param path
	 * @param name
	 * @param bitmap
	 */
	public static void writeBitmap(String path, String name, Bitmap bitmap) {
		writeBitmap(path, name, bitmap, "JPG");
	}

	/**
	 * 保存图片为jpg
	 *
	 * @param path
	 * @param name
	 * @param bitmap
	 */
	public static void writeBitmap(String path, String name, Bitmap bitmap,
								   String type) {
		if (TextUtils.isEmpty(path) || TextUtils.isEmpty(name)
				|| bitmap == null) {
			return;
		}
		Bitmap.CompressFormat format = Bitmap.CompressFormat.JPEG;
		if ("PNG".equals(type)) {
			format = Bitmap.CompressFormat.PNG;
		}

		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}

		File _file = new File(path,  name);
		if (_file.exists()) {
			_file.delete();
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(_file);
			bitmap.compress(format, 100, fos);
		} catch (FileNotFoundException e) {
			Log.w(TAG, e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					Log.w(TAG, e);
				}
			}
		}
	}

	
	public static void writeBitmap(String pathName, Bitmap bitmap,
			int compressRate) {
		if (null == bitmap || null == pathName)
			return;
		boolean bPng = false;
		if (pathName.endsWith(".png")) {
			bPng = true;
		}

		File _file = new File(pathName);
		boolean bNew = true;
		if (_file.exists()) {
			bNew = false;
			_file = new File(pathName + ".tmp");
			_file.delete();
		}
		FileOutputStream fos = null;
		boolean bOK = false;
		try {
			fos = new FileOutputStream(_file);
			if (bPng) {
				bitmap.compress(Bitmap.CompressFormat.PNG, compressRate, fos);
			} else {
				bitmap.compress(Bitmap.CompressFormat.JPEG, compressRate, fos);
			}
			bOK = true;
		} catch (Exception e) {
			Log.w(TAG, e);
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
					if (bNew == false && bOK) {
						_file.renameTo(new File(pathName));
					}
				} catch (IOException e) {
				}
			}
		}
	}
	
	// 返回删除的文件的大小
	public static long deleteFile(String filePath) {
		try {
			return removeDir(filePath);
		} catch (Throwable t) {
			t.printStackTrace();
		}
		return 0;
	}

	// 返回删除的文件的大小
	public static long removeDir(String filePath) {
		File f = new File(filePath);
		return removeFile(f);
	}

	// 返回删除的文件的大小
	public static long removeFile(File f) {
		if (null == f)
			return 0;
		long size = 0;
		if (f.isFile()) {
			size = f.length();
			f.delete();
			return size;
		}
		File flist[] = f.listFiles();
		if (null == flist) {
			f.delete();
			return size;
		}
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size += removeFile(flist[i]);
			} else {
				size += flist[i].length();
				flist[i].delete();
			}
		}
		f.delete();
		return size;
	}

	/**
	 * 
	 * @param originPathName
	 *            原文件路径和名字
	 * @param newPath
	 *            新文件路径
	 * @param newName
	 *            新文件名字
	 * @return
	 */
	public static boolean renameFile(String originPathName, String newPath,
			String newName) {
		File originFile = new File(originPathName);
		if (!originFile.exists()) {
			return false;
		}
		File newFile = new File(newPath, newName);
		if (newFile.exists()) {// 已经有个相同名字的文件存在
			return false;
		}
		boolean mark = false;
		try {
			mark = newFile.createNewFile();
		} catch (IOException e) {
			// AUTO_TODO Auto-generated catch block
			Log.w(TAG, e);
		}
		if (!mark)
			return false;
		return originFile.renameTo(newFile);
	}

	public static boolean copyFile(File from, File to) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			to.createNewFile();// 删除清空文件，如果存在的话
			fis = new FileInputStream(from);
			if (fis != null) {
				fos = new FileOutputStream(to);
				byte[] buffer = new byte[1024];
				int len = 0;
				while ((len = fis.read(buffer)) != -1) {
					fos.write(buffer, 0, len);
				}
				fos.flush();
				return true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.w(TAG, e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.w(TAG, e);
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Log.w(TAG, e);
				}
			}
		}
		return false;
	}

	/**
	 *
	 *
	 * @param pathName
	 *            文件路径+名字
	 * @return
	 */
	public static InputStream readFile(String pathName) {
		File file = new File(pathName);
		FileInputStream fis = null;
		if (file.exists() && file.isFile()) {
			try {
				fis = new FileInputStream(file);
			} catch (FileNotFoundException e) {
				Log.w(TAG, e);
				Log.w(TAG, e);
			}
		}
		return fis;
	}
	
	public static long getFileSize(String pathName){
		if (TextUtils.isEmpty(pathName)){
			return 0;
		}
		
		File file = new File(pathName);
		if (file.exists()){
			return file.length();
		}
		return 0;
	}
}
