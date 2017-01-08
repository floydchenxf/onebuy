package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 2017/1/8.
 */
public class RedeemPayVO {
    public Long ID;
    public String Name;
    public Double Balance;
    public String Pic;
    public String Url;

    public String getPicUrl() {
        return CommonUtil.getImageUrl(Pic);
    }
}
