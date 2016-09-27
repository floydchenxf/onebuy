package com.floyd.onebuy.biz.vo.commonweal;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;

/**
 * Created by floyd on 16-7-24.
 */
public class CommonwealVO {

    public Long FoundationID;
    public String Code;
    public String Percent;
    public String TotalMoney;
    public String RaiseMoney;
    public Integer RaiseCount;
    public String FoundationName;//产品名称
    public String Brief; //内容
    public String Pictures; //图片
    public Integer Status;//状态
    public Integer IsCompany;//是否是公司的

    public String getPicUrl() {
        if (TextUtils.isEmpty(Pictures)) {
            return null;
        }

        if (Pictures.startsWith("http")) {
            return Pictures;
        }
        return APIConstants.HOST + Pictures;
    }
}
