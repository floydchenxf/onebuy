package com.floyd.onebuy.biz;

import android.content.SharedPreferences;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by floyd on 15-11-22.
 */
public class SharedPreferencesCompat {
    private static final Method sApplyMethod = findApplyMethod();

    public SharedPreferencesCompat() {
    }

    private static Method findApplyMethod() {
        try {
            Class unused = SharedPreferences.Editor.class;
            return unused.getMethod("apply", new Class[0]);
        } catch (NoSuchMethodException var1) {
            return null;
        }
    }

    public static void apply(SharedPreferences.Editor editor) {
        if (sApplyMethod != null) {
            try {
                sApplyMethod.invoke(editor, new Object[0]);
                return;
            } catch (InvocationTargetException var2) {
                ;
            } catch (IllegalAccessException var3) {
                ;
            }
        }

        editor.commit();
    }
}
