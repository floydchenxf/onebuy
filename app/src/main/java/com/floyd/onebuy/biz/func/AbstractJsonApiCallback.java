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

//        ApiResult<R> result = new ApiResult<R>();
        try {
            JSONObject j = new JSONObject(s);
            boolean success = j.getBoolean("success");
            if (success) {
                String data = j.getString("data");
                R r = convert2Obj(s, data);
//                result.result = r;
//                result.code = 200;
                mCallback.onSuccess(r);
            } else {
                String msg = j.getString("msg");
//                result.msg = msg;
                mCallback.onError(APIError.API_BIZ_ERROR, msg);
            }
        } catch (JSONException e) {
            mCallback.onError(APIError.API_JSON_PARSE_ERROR, e.getMessage());
            return;
        }
    }

    protected abstract R convert2Obj(String s, String data) throws JSONException;
}
