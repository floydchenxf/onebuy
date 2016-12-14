package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by chenxiaofeng on 16/9/21.
 */
public class SubjectInfoVO {
    public String Name;
    public String QRCode;
    public String BackGround;

    public Long BeginTime;
    public Long EndTime;


    public String getQRcode() {
        if (this.QRCode.startsWith("http")) {
            return this.QRCode;
        }

        return APIConstants.HOST + this.QRCode;
    }

    public String getBackGround() {
        if (this.BackGround.startsWith("http")) {
            return this.BackGround;
        }

        return APIConstants.HOST + this.BackGround;
    }

    public Long getBeginTime() {
        return this.BeginTime * 1000;
    }

    public Long getEndTime() {
        return this.EndTime * 1000;
    }
}
