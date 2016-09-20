package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-5-4.
 * 商品晒单
 */
public class PrizeShowVO {
    public long ProductLssueID;//商品期数
    public String Pictures;
    public String GuestTitle;
    public String GuestContent;
    public String GuestPic;
    public long ProID;
    public String GuestName;
    public String GuestTime;
    public long GuestID;

    public String MediaUrl;
    public Integer CommentNum;
    public int TypeBook; //类型 1:图片 2:视频

    public int isVerify;

    public List<String> getMediaUrls() {
        List<String> result = new ArrayList<String>();
        if (!TextUtils.isEmpty(MediaUrl)) {
            String[] urls = MediaUrl.split("\\|");
            for (String s : urls) {
                result.add(APIConstants.HOST + s);
            }
        }
        return result;
    }

    public boolean isPic() {
        return this.TypeBook == APIConstants.SHARE_SHOW_PIC_TYPE;
    }
}
