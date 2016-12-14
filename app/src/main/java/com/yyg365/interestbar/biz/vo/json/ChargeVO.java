package com.yyg365.interestbar.biz.vo.json;

/**
 * Created by floyd on 16-8-10.
 */
public class ChargeVO {
    public Long ClientId;
    public Long ChargeId;
    public int ChargeState; //0:未支付 1:已支付
    public Long Intime;
    public Long PayTime;
    public int ChargeType;
    public String TaoNo;
    public Double Money;
    public String OrderNum;
    public String ChargeTypeTitle;

    public long getIntime() {
        long result = 0;
        if (Intime != null && Intime != 0l) {
            result = Intime * 1000;
        }

        return result;
    }

    public long getPayTime() {
        long time = 0l;
        if (this.PayTime != null && this.PayTime > 0) {
            time = (long) this.PayTime * 1000;
        }
        return time;
    }

}
