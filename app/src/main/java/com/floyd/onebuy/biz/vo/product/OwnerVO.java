package com.floyd.onebuy.biz.vo.product;

import com.floyd.onebuy.utils.CommonUtil;

/**
 * Created by floyd on 16-4-17.
 */
public class OwnerVO {

    public long userId;
    public String userName;
    public int joinNumber;
    public String winNumber;//中奖号码
    public String avatar;//头像
    public long winTime;

    public String getHeadImage() {
        return CommonUtil.getImageUrl(this.avatar);
    }
}
