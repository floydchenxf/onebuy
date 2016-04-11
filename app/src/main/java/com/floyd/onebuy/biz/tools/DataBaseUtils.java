package com.floyd.onebuy.biz.tools;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.floyd.onebuy.biz.databases.WxCursor;

/**
 * Created by floyd on 15-11-29.
 */
public class DataBaseUtils {
    private static final String TAG = "DataBaseUtils";

    public static Cursor doContentResolverQueryWrapper(Context context,
                                                       Uri uri, String[] projection, String selection,
                                                       String[] selectionArgs, String sortOrder) {
        if (context == null) {
            return null;
        }
        try {
            Cursor cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, sortOrder);
            if (cursor == null) {
                return null;
            }
            return new WxCursor(cursor);
        } catch (Exception e) {
            Log.w(TAG, e);
        }
        return null;
    }
}
