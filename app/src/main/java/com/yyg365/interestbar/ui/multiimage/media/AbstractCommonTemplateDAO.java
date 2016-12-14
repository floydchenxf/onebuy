package com.yyg365.interestbar.ui.multiimage.media;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommonTemplateDAO<T> {
	private static final String TAG = "AbstractCommonTemplateDAO";

	protected Uri uri;
	protected Context context;

	public AbstractCommonTemplateDAO(Context context, Uri uri) {
		this.context = context.getApplicationContext();
		this.uri = uri;
	}

	public boolean insert(T t) {
		ContentValues contentValues = fillContentValue(t);
		try {
			context.getContentResolver().insert(uri, contentValues);
		} catch (Exception e) {
			Log.e(TAG, "execute insert error:" + e.getMessage());
			return false;
		}
		return true;
	}

	public T queryOne(String[] projection, String select, String[] args) {
		T t = null;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, projection,  select, args, "_id desc");
			if (cursor == null) {
				return t;
			}

			if (cursor.moveToNext()) {
				t = fillObject(cursor);
			}
		} catch (Exception e) {
			Log.e(TAG, "execute queryOne cause error:" + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return t;
	}

	public List<T> queryList(String[] projection, String select, String[] args) {
		List<T> t = null;
		Cursor cursor = null;
		try {
			cursor = context.getContentResolver().query(uri, projection, select, args, "_id desc");
			if (cursor == null) {
				return null;
			}

			t = new ArrayList<T>();

			while (cursor.moveToNext()) {
				T tmp = fillObject(cursor);
				t.add(tmp);
			}
		} catch (Exception e) {
			Log.e(TAG, "execute queryList cause error:" + e.getMessage());
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return t;
	}

	protected boolean update(Uri updateUri, ContentValues contentValues, String where, String[] selectionArgs) {
		int count = 0;
		try {
			context.getContentResolver().update(uri, contentValues, where, selectionArgs);
		} catch (Exception e) {
			Log.e(TAG, "execute update cause error uri:" + updateUri + "---error:" + e.getMessage());
		}
		return count > 0;
	}

	protected boolean delete(Uri deleteUri, String select, String[] selectArgs) {
		int count = 0;
		try {
			context.getContentResolver().delete(deleteUri, select, selectArgs);
		} catch (Exception e) {
			Log.e(TAG, "execute delete cause error uri:" + deleteUri + "---error:" + e.getMessage());
		}
		return count > 0;
	}

	/**
	 * 从数据库数据转换为对象
	 * 
	 * @param cursor
	 * @return
	 */
	public abstract T fillObject(Cursor cursor);

	/**
	 * 填充对象到资源对象
	 * 
	 * @param t
	 * @return
	 */
	public abstract ContentValues fillContentValue(T t);

}
