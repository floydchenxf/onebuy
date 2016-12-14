package com.yyg365.interestbar.aync;

/**
 * Created by floyd on 15-11-19.
 */
public interface ApiCallback<T> {

    /**
     * 错误信息回调
     *
     * @param code
     * @param errorInfo
     */
    void onError(int code, String errorInfo);

    /**
     * 成功回调
     *
     * @param t
     */
    void onSuccess(T t);

    /**
     * 进度回调
     *
     * @param progress
     */
    void onProgress(int progress);
}
