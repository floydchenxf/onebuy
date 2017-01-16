package com.yyg365.interestbar.biz.vo.json;

/**
 * Created by chenxiaofeng on 2017/1/16.
 */
public class JFOrderInfoVO {

    public Long ID;
    public Long OrderTime;
    public Integer IsSend;//是否已发货
    public String Remarks;//备注
    public Long AddressID;
    public String Mobile;

    public Long OriginalJiFen;//原价积分
    public Long PayJiFen;//付积分
    public Integer ClientLevel;
    public Integer ClientLevelRatio;
    public Integer ExpressCharge;
    public String ExpressOrder;
    public Long ExpressCompanyID;
    public String ExpressCompany;

}
