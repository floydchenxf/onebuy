package com.floyd.onebuy.biz.vo.model;

import com.floyd.onebuy.biz.vo.product.OwnerVO;

import java.util.List;

/**
 * Created by floyd on 16-4-13.
 * 奖品信息
 */
public class WinningInfo {
    public static int STATUS_CHOOSE = 0;
    public static int STATUS_LOTTERY = 1;
    public static int STATUS_LOTTERYED = 2;

    public long id;//奖品id
    public long lssueId;//期数ID
    public String code;//期数
    public long productId;//商品Id
    public String productUrl;//产品图片url
    public String title; //标题
    public String processPrecent;//完成百分比
    public int joinedCount;//剩余组数
    public int totalCount;//总共需要
    public int buyCount;

    public long lotteryTime;//开奖时间
    public int status;//类型 0 选择商品，1：开奖　2．已开奖
    public OwnerVO ownerVO; //中奖者
    public List<String> myPrizeCodes; //我购买的号码

    public int productType;//商品类型

    public boolean isExist;//是否退出状态

    public long getId() {
        return id;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public String getTitle() {
        return title;
    }

}
