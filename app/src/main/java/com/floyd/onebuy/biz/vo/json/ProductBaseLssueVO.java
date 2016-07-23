package com.floyd.onebuy.biz.vo.json;

import android.text.TextUtils;

import com.floyd.onebuy.biz.constants.APIConstants;

/**
 * Created by floyd on 16-7-23.
 */
public class ProductBaseLssueVO {
    public long ProductLssueID;//商品期数
    public int TotalCount; //总数
    public int JoinedCount; //已加入人数
    public long ProID; //商品id
    public String ProName; //商品名称
    public String Pictures; //商品图片,不带前最

    public String getPicUrl() {
        if (TextUtils.isEmpty(this.Pictures)) {
            return null;
        }
        return APIConstants.HOST + Pictures;
    }
}
