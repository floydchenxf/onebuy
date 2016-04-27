package com.floyd.onebuy.biz.func;

import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by floyd on 15-11-28.
 */
public abstract class AbstractJsonApiCallback<R> implements ApiCallback<String> {

    private ApiCallback<R> mCallback;

    protected AbstractJsonApiCallback(ApiCallback<R> callback) {
        this.mCallback = callback;
    }

    @Override
    public void onError(int code, String errorInfo) {
        this.mCallback.onError(code, errorInfo);
    }

    @Override
    public void onProgress(int progress) {
        this.mCallback.onProgress(progress);
    }

    @Override
    public void onSuccess(String s) {
        if (TextUtils.isEmpty(s)) {
            mCallback.onError(APIError.API_CONTENT_EMPTY, "content is empty!");
            return;
        }

        try {
            JSONObject j = new JSONObject(s);
            int status = j.getInt("status");
            if (status == 1) {
                String data = j.getString("data");
                R r = convert2Obj(data);
                mCallback.onSuccess(r);
            } else {
                String msg = j.getString("info");
                mCallback.onError(APIError.API_BIZ_ERROR, msg);
            }
        } catch (JSONException e) {
            mCallback.onError(APIError.API_JSON_PARSE_ERROR, e.getMessage());
            return;
        }
    }

    protected abstract R convert2Obj(String data) throws JSONException;
}
