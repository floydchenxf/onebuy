package com.floyd.onebuy.biz.vo.product;

/**
 * Created by floyd on 16-4-13.
 * 奖品信息
 */
public class WinningInfo {

    public long id;//奖品id
    public String productUrl;//产品图片url
    public String title; //标题
    public int processPrecent;//完成百分比
    public int left;//剩余组数
    public int total;//总共需要
    public int buyCount;

    public long getId() {
        return id;
    }

    public int getPrecent() {
        return processPrecent;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getTitle() {
        return title;
    }
}