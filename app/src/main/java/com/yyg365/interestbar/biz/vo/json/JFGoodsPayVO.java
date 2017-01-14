package com.yyg365.interestbar.biz.vo.json;

import java.util.List;

/**
 * Created by chenxiaofeng on 2017/1/14.
 */
public class JFGoodsPayVO {
    public Long PayJiFen;
    public ClientAddr ClientAddr;
    public JFGoodsDetailVO JFProInfo;
    public UserInfo UserInfo;
    public Integer PayStatus;
    public List<PayChannelVO> PayChannel;

    public static class ClientAddr {
        public Long ID;
        public String Name;
        public String Phone;
        public String Address;
    }

    public static class UserInfo {
        public Long ID;
        public String Name;
        public String Mobile;
        public Long JiFen;
        public Integer ClientLevel;
        public String ClientLevelName;
        public Integer ClientLevelRatio;
    }

}
