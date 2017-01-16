package com.yyg365.interestbar.biz.vo.json;

import java.util.List;

/**
 * Created by chenxiaofeng on 2017/1/14.
 */
public class JFGoodsPayVO {
    public Long PayJiFen;
    public ClientAddr ClientAddr;
    public JFGoodsDetailVO JFProInfo;
    public JFUserInfoVO UserInfo;
    public Integer PayStatus;
    public List<PayChannelVO> PayChannel;
}
