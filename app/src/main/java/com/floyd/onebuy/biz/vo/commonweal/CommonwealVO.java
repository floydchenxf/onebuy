package com.floyd.onebuy.biz.vo.commonweal;

import com.floyd.onebuy.biz.constants.APIConstants;

/**
 * Created by floyd on 16-7-24.
 */
public class CommonwealVO {

    public Long ProductLssueID;
    public String Code;
    public String Percent;
    public String TotalMoney;
    public String RaiseMoney;
    public Integer RaiseCount;
    public String ProName;//产品名称
    public String Content; //内容
    public String Pictures; //图片
    public Integer Status;//状态

    public String getPicUrl() {
        return APIConstants.HOST + Pictures;
    }
}
