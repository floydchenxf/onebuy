package com.floyd.onebuy.biz.vo.json;

/**
 * Created by chenxiaofeng on 16/9/30.
 */
public class InviteFriendRecordVO {

    public Long ID;
    public Long ClientID;
    public String Phone; //被邀请者号码
    public String ClientName;
    public long InviteClientID;
    public String InvitePhone;//邀请号码
    public long RecordTime;//邀请时间
    public int Status;
    public long StatusTime;


    public long getRecordTime() {
        return this.RecordTime * 1000;
    }

    public long getStatusTime() {
        return this.StatusTime * 1000;
    }


}
