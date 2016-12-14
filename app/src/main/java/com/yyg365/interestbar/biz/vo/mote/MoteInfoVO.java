package com.yyg365.interestbar.biz.vo.mote;

import android.text.TextUtils;

import com.yyg365.interestbar.biz.vo.IKeepClassForProguard;
import com.yyg365.interestbar.utils.CommonUtil;

/**
 * Created by floyd on 15-11-28.
 */
public class MoteInfoVO implements IKeepClassForProguard {
    public long id;
    public String nickname;//别名
    private String avartUrl;//avatarUrl;//头像地址
    private String avatarUrl;
    public int orderNum;
    public int fenNum;
    public int followNum;
    public int goodeEvalRate;
    public int fee;
    public String authenPic1;//验证图片1
    public String authenPic2;//验证图片2
    public String authenPic3;//验证图片3
    public String authenStatus;//验证状态

    public String getHeadUrl() {
        if (!TextUtils.isEmpty(this.avartUrl)) {
            return CommonUtil.getImage_200(this.avartUrl);
        }

        if (!TextUtils.isEmpty(this.avatarUrl)) {
            return CommonUtil.getImage_200(this.avatarUrl);
        }

        return null;
    }

    public String getDetailUrl() {
        if (!TextUtils.isEmpty(this.avartUrl)) {
            return CommonUtil.getImage_800(this.avartUrl);
        }

        if (!TextUtils.isEmpty(this.avatarUrl)) {
            return CommonUtil.getImage_800(this.avatarUrl);
        }

        return null;
    }

}
