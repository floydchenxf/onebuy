package com.yyg365.interestbar.biz.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.yyg365.interestbar.biz.SharedPreferencesCompat;

import java.util.HashMap;

/**
 * Created by floyd on 15-11-22.
 */
public class PrefsTools {

    public static final String IS_LOGIN = "IS_LOGIN";
    public static final String LOGIN_NAME = "LOGIN_NAME";
    public static final String UNREAD_MSG_TIME = "UNREADMSGTIME";
    private static final HashMap<String, SharedPreferences> sSharedPrefs = new HashMap();
    private static SharedPreferences defalultSprefs = null;

    public PrefsTools() {
    }

    static SharedPreferences getPreferences(Context context, String name) {
        SharedPreferences sprefs = null;
        if(sSharedPrefs.get(name) != null) {
            return (SharedPreferences)sSharedPrefs.get(name);
        } else {
            sprefs = context.getSharedPreferences(name, 0);
            sSharedPrefs.put(name, sprefs);
            return sprefs;
        }
    }

    private static SharedPreferences getDefaultPreferences(Context context) {
        if(defalultSprefs != null) {
            return defalultSprefs;
        } else {
            defalultSprefs = context.getSharedPreferences("ywPrefsTools", 0);
            return defalultSprefs;
        }
    }

    public static void setLongPrefs(Context context, String key, long value) {
        SharedPreferences.Editor editor = getDefaultPreferences(context).edit();
        editor.putLong(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static long getLongPrefs(Context context, String key) {
        return getLongPrefs(context, key, 0L);
    }

    public static long getLongPrefs(Context context, String key, long defaultValue) {
        return getDefaultPreferences(context).getLong(key, defaultValue);
    }

    public static void setBooleanPrefs(Context context, String key, boolean value) {
        SharedPreferences.Editor editor = getDefaultPreferences(context).edit();
        editor.putBoolean(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static boolean getBooleanPrefs(Context context, String key) {
        return getBooleanPrefs(context, key, false);
    }

    public static boolean getBooleanPrefs(Context context, String key, boolean defaultValue) {
        return getDefaultPreferences(context).getBoolean(key, defaultValue);
    }

    public static void setStringPrefs(Context context, String key, String value) {
        if(!TextUtils.isEmpty(key) && value != null) {
            SharedPreferences.Editor editor = getDefaultPreferences(context).edit();
            editor.putString(key, value);
            SharedPreferencesCompat.apply(editor);
        }
    }

    public static String getStringPrefs(Context context, String key) {
        return getStringPrefs(context, key, "");
    }

    public static String getStringPrefs(Context context, String key, String defaultValue) {
        return getDefaultPreferences(context).getString(key, defaultValue);
    }

    public static void removePrefs(Context context, String key) {
        SharedPreferences.Editor editor = getDefaultPreferences(context).edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }

    public static void setIntPrefs(Context context, String key, int value) {
        SharedPreferences.Editor editor = getDefaultPreferences(context).edit();
        editor.putInt(key, value);
        SharedPreferencesCompat.apply(editor);
    }

    public static int getIntPrefs(Context context, String key) {
        return getIntPrefs(context, key, 0);
    }

    public static int getIntPrefs(Context context, String key, int defaultValue) {
        return getDefaultPreferences(context).getInt(key, defaultValue);
    }
}
