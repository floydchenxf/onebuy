package com.floyd.onebuy.biz.vo.json;

/**
 * Created by chenxiaofeng on 16/10/27.
 */
public class CommissionItemVO {

    public Long ID;
    public Long ClientID;
    public Double Commission;
    public int Type;
    public String Remark;
    public long RecordTime;

    public long getRecordTime() {
        return this.RecordTime * 1000;
    }
}
