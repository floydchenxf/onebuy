package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 2017/1/8.
 */
public class PawnRedeemVO {
    public Long ID;
    public Long ProID;
    public String ProFirstPicture;
    public String Pictures;
    public String ProTitle;
    public Long ClientID;
    public String ClientName;
    public Integer ClientLevel;
    public String ClientLevelName;
    public Integer ClientLevelRatio;
    public Long PawnTime;
    public Integer PawnPrice;
    public Integer PawnLevel;
    public String PawnLevelName;
    public Integer PawnLevelRatio; //典当比率
    public Integer RedeemDays;//赎回剩余时间
    public Long ProLssueID;//期数ID
    public Integer ProLssueNum; //商品期数
    public Integer IsRedeem; //是否已经赎回
    public Long RedeemTime; //赎回时间。没有赎回则为0
    public Integer RedeemPrice;//赎回价格
    public Integer RealRedeemPrice;//真正赎回价格

    public Integer RedeemClientLevel; //客户端赎回等级
    public String RedeemClientLevelName;//客户端赎回登录描述
    public Integer RedeemClientLevelRetio;//客户端赎回等级折扣
    public Double PawnRedeemRatio;
    public String Explain;


    public String getProductImage() {
        return CommonUtil.getImageUrl(ProFirstPicture);
    }

    public String getPawnTime() {
        return DateUtil.getDateTime("yyyy-MM-dd HH:mm", PawnTime * 1000);
    }
}
