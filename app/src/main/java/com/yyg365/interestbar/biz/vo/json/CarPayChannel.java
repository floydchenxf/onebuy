package com.yyg365.interestbar.biz.vo.json;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.constants.APIConstants;

/**
 * Created by chenxiaofeng on 16/9/8.
 */
public class CarPayChannel {

    public Long ID;
    public String Name;
    public String Pic;
    public String Url;

    public String getPicUrl() {
        String result = this.Pic ;
        if (!TextUtils.isEmpty(this.Pic) && !this.Pic.startsWith("http")) {
            result = APIConstants.HOST + this.Pic;
        }

        return result;
    }
}
