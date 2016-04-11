package com.floyd.onebuy.utils;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.List;

public class CommonUtil {



    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * 使用java正则表达式去掉多余的.与0
     *
     * @param s 传入的String
     * @return 处理过的String
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", "");//去掉多余的0  
            s = s.replaceAll("[.]$", "");//如最后一位是.则去掉  
        }
        return s;
    }



    /**
     * 从List中获取Action 构造JsonArray
     * @param actions
     * @return
     */
    public static String getActionParam(List<String> actions) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < actions.size(); i++) {
                jsonArray.put(i, actions.get(i));
            }
            jsonObject.put("action", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    /**
     * 把String转换成int，如果抛异常，那么返回defaultValue
     * @param value 要转换的String
     * @param defaultValue 默认的数据
     * @return
     */
    public static int parseInt(@Nullable String value , int defaultValue){
        try {
            return Integer.parseInt(value);
        }catch (Exception e){
            return defaultValue;
        }
    }


    /**
     * 是否同一天
     *
     * @param timeOne
     * @param timeTwo
     * @return
     */
    public static boolean isInSameDay(long timeOne, long timeTwo) {
        if (timeOne == timeTwo) {
            return true;
        }
        if (timeOne > 0 && timeTwo > 0) {
            Calendar calendarToday = Calendar.getInstance();
            calendarToday.setTimeInMillis(timeOne);
            int today = calendarToday.get(Calendar.DAY_OF_MONTH);
            int toMonth = calendarToday.get(Calendar.MONTH);
            int toYear = calendarToday.get(Calendar.YEAR);

            Calendar calendarLast = Calendar.getInstance();
            calendarLast.setTimeInMillis(timeTwo);
            int lastDay = calendarLast.get(Calendar.DAY_OF_MONTH);
            int lastMonth = calendarLast.get(Calendar.MONTH);
            int lastYear = calendarLast.get(Calendar.YEAR);

            //两次时间的天数或者月数、或者年数相同。
            return (today == lastDay && toMonth == lastMonth && toYear == lastYear);
        }
        return false;
    }

    public static String getImageType(String url, String type) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return url.concat(type);
    }

    public static String getImage_w1(String url) {
        return getImageType(url, "!w1");
    }

    /**
     * 获取图片100X100
     * @param url
     * @return
     */
    public static String getImage_200(String url) {
        return getImageType(url, "!v200");
    }

    /**
     * 获取图片800X800
     * @param url
     * @return
     */
    public static String getImage_800(String url) {
        return getImageType(url, "!v800");
    }


    /**
     * 获取图片400X400
     * @param url
     * @return
     */
    public static String getImage_400(String url) {
        return getImageType(url, "!v400");
    }

}
