package com.floyd.onebuy.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipUtil {
	
	private static final String TAG = GzipUtil.class.getSimpleName();

	private static final int BUFFER = 8192;

	public static void compress(File from, File to) {
		if (!from.exists()) {
			return;
		}

		GZIPOutputStream out = null;
		BufferedInputStream bis = null;
		try {
			out = new GZIPOutputStream(new FileOutputStream(to));
			bis = new BufferedInputStream(new FileInputStream(from));
			int count;
			byte data[] = new byte[BUFFER];
			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				out.write(data, 0, count);
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (IOException e) {
				}
			}

			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

	}

}
