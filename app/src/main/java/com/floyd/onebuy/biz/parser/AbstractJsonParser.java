package com.floyd.onebuy.biz.parser;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by floyd on 15-12-5.
 */
public abstract class AbstractJsonParser<T> implements Parser<T> {


    @Override
    public void doParse(ApiCallback<T> apiCallback, String s) {
        JSONObject j = null;
        try {
            j = new JSONObject(s);
            int status = j.getInt("status");
            if (status == 1) {
                String data = j.getString("data");
                T r = convert2Obj(data);
                apiCallback.onSuccess(r);
            } else {
                String msg = j.getString("info");
                apiCallback.onError(APIError.API_BIZ_ERROR, msg);
            }
        } catch (JSONException e) {
            apiCallback.onError(APIError.API_JSON_PARSE_ERROR, e.getMessage());
        }
    }

    protected abstract T convert2Obj(String data);

}
