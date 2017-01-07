package com.yyg365.interestbar.biz.vo.json;

import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by chenxiaofeng on 2017/1/6.
 */
public class PawnLogInfoVO {
    public Long ProID;
    public String ProFirstPicture;//图片封面
    public String Pictures;
    public String ProTitle;//产品名称
    public Double ProPrice;
    public Double ProMSPrice;//官方价格
    public Long ClientID;//当前用户id
    public String ClientName;//当前用户名称
    public Integer ClientLevel; //当前用户等级
    public String ClientLevelName;//当前用户等级名称
    public Integer ClientLevelRatio;
    public Long ProLssueID; //期数ID
    public Integer ProLssueNum;//产品期数

    public String getProductImage() {
        return CommonUtil.getImageUrl(ProFirstPicture);
    }

}
