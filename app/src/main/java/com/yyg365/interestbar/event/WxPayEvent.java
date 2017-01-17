package com.yyg365.interestbar.event;

/**
 * Created by chenxiaofeng on 2017/1/18.
 */
public class WxPayEvent {

    public int errorCode;

    public WxPayEvent(int code) {
        this.errorCode = code;
    }
}
