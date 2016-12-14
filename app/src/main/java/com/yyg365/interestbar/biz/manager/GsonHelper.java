package com.yyg365.interestbar.biz.manager;

import com.google.gson.Gson;

/**
 * Created by floyd on 16-4-30.
 */
public class GsonHelper {

    public static Gson gson = new Gson();

    public static Gson getGson() {
        return gson;
    }
}
