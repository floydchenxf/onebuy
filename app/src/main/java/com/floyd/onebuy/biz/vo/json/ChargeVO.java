package com.floyd.onebuy.biz.vo.json;

/**
 * Created by floyd on 16-8-10.
 */
public class ChargeVO {
    public Long ClientId;
    public Long ChargeId;
    public int ChargeState;
    public Long Intime;
    public Long PayTime;
    public int ChargeType;
    public String TaoNo;
    public Double Money;
    public String OrderNum;

    public long getIntime() {
        long result = 0;
        if (Intime != null && Intime != 0l) {
            result = Intime * 1000;
        }

        return result;
    }

    public long getPayTime() {
        long result = 0;
        if (PayTime != null && PayTime != 0l) {
            result = PayTime * 1000;
        }

        return result;
    }

}
