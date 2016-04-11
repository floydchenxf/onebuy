package com.floyd.onebuy.biz.tools;

import android.text.TextUtils;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by floyd on 15-12-7.
 */
public class DateUtil {
    private static final String TAG = "DateUtil";

    public static String getDayBefore(String dateStr) {
        if (TextUtils.isEmpty(dateStr)) {
            return "";
        }

        dateStr = dateStr.replaceAll("T", " ").replaceAll("\\+000", "");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date date = null;
        try {
            date = df.parse(dateStr);
        } catch (ParseException e) {
            Log.e(TAG, e.getMessage());
            return "";
        }

        long publishDate = date.getTime();
        long now = System.currentTimeMillis();
        long minutes = (now - publishDate) / (1000 * 60);
        if (minutes < 60) {
            return minutes + "分前";
        }

        long hour = minutes / 60;
        if (hour < 24) {
            return hour + "小时前";
        }

        long day = hour/24;
        if (day < 365) {
            return day + "天前";
        }

        long year = day/365;
        return year+"年前";
    }

    public static String getDateStr(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }

    public static String getDateTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public static String getDateBefore(long createTime) {
        long after = createTime + 30 * 60 * 1000;
        long now = System.currentTimeMillis();
        if (after < now) {
            return null;
        }

        StringBuilder result = new StringBuilder();
        long secs = (after - now)/1000;
        if (secs > 60) {
            long min = secs/60;
            result.append(min).append(":");
            long sec = secs%60;
            if (sec < 10) {
                result.append("0").append(sec);
            } else {
                result.append(sec);
            }
        } else {
            result.append(secs);
        }

        return result.toString();
    }
}
