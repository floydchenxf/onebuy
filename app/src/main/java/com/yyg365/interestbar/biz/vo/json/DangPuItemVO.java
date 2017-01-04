package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.biz.tools.ImageUtils;
import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 17/1/4.
 */
public class DangPuItemVO {
    public Long ID;
    public Long ProID;
    public String ProFirstPicture;
    public String ProTitle;
    public Long ClientID;
    public String ClientName;
    public Integer ClientLevel;
    public String ClientLevelName;
    public Integer ClientLevelRatio;
    public Long PawnTime; //典当时间
    public Integer PawnPrice; //典当价格
    public Integer PawnLevel; //典当等级
    public String PawnLevelName;
    public Integer RedeemDays; //赎回剩余时间
    public Long ProLssueID; //
    public Integer ProLssueNum; //期数
    public boolean IsRedeem; //是否赎回
    public Integer RealRedeemPrice; //真正赎回价格


    public String getProductUrl() {
        return CommonUtil.getImageUrl(ProFirstPicture);
    }

}
