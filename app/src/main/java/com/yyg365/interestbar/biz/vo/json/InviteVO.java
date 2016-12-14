package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by chenxiaofeng on 16/9/25.
 */
public class InviteVO {
    public String qrcode;//二维码
    public String url;//邀请url

    public String getQrcode() {
        if (qrcode.startsWith("http")) {
            return this.qrcode;
        } else {
            return APIConstants.HOST + this.qrcode;
        }
    }
}
