package com.floyd.onebuy.biz.vo.product;

import com.floyd.onebuy.biz.vo.AdvVO;

import java.util.List;

/**
 * Created by floyd on 16-4-17.
 */
public class WinningDetailInfo {

    public long id;//期数id
    public long proId;//商品id
    public String code;//期数
    public List<AdvVO> advVOList; //产品广告
    public String productTitle; //产品名称
    public String productDetailUrl; //产品详情页面url
    public long shareId;//分享id
    public int status; //状态 1 进行中, 2：开奖中，　３:已经揭晓;
    public ProgressVO progressVO; //进度信息
    public OwnerVO ownerVO; //中奖者
    public List<JoinVO> allJoinedRecords; //所有的参与号码
    public List<String> myRecords;
    public long priceTime;
}
