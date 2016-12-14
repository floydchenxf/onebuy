package com.yyg365.interestbar.biz.tools;

import com.yyg365.interestbar.bean.MD5Util;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;

/**
 * Created by floyd on 16-4-26.
 */
public class SignTool {

    public static final String FIX_SIGN_KEY = "12yDS12890ahls";

    /**
     * 产生随机字符串
     *
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 生成加密sign
     *
     * @param params
     * @return
     */
    public static String generateSign(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }

        String[] keys = params.keySet().toArray(new String[params.size()]);
        Arrays.sort(keys);
        StringBuilder md5Content = new StringBuilder();
        for (String k : keys) {
            if (k.toLowerCase().equals("pagetype")) {
                continue;
            }
            String value = params.get(k);
            md5Content.append(k).append("=").append(value);
        }
        md5Content.append(FIX_SIGN_KEY);
        return MD5Util.encodeBy32BitMD5(md5Content.toString());
    }
}
