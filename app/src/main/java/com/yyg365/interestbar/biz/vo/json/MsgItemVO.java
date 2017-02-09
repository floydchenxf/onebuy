package com.yyg365.interestbar.biz.vo.json;

/**
 * Created by chenxiaofeng on 2017/2/9.
 */

public class MsgItemVO {
    public Long ID;
    public Long ClientID;
    public String Title;
    public Long SendTime;
    public Integer IsAll;

    public Long getSendTime() {
        long result = 0l;
        if (SendTime != null) {
            result = SendTime * 1000;
        }

        return result;
    }

}
