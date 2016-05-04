package com.floyd.onebuy.biz.vo.json;

import com.floyd.onebuy.biz.constants.APIConstants;

/**
 * Created by floyd on 16-5-3.
 * 往期获奖者
 */
public class HistoryPrizeVO {
    public long ProductLssueID;//期数
    public String ProductLssueCode;
    public int JoinedCount;//参与者数量
    public String ClientPic;//客户头像
    public String ClientName;//客户名称
    public long ClientID;//客户ID
    public String ClientIP;//客户的来源IP
    public String PrizeCode;//中奖号码
    public String PrizeTime;//中奖时间
    public int status;//状态

    public String getClientPic() {
        return APIConstants.HOST + this.ClientPic;
    }
}
