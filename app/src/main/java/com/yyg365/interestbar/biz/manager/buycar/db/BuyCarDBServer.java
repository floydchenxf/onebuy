package com.yyg365.interestbar.biz.manager.buycar.db;

import android.content.Context;

import com.yyg365.interestbar.dao.BuyCarNumber;

import java.util.List;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public interface BuyCarDBServer {

    /**
     * 删除购物车商品数量
     *
     * @param context
     * @param userId
     * @param productLssueId
     */
    void deleteBuyCarNumber(Context context, long userId, long productLssueId);

    /**
     * 批量删除购物车商品
     *
     * @param context
     * @param userId
     * @param productLssueIds
     */
    void deleteBuyCarNumber(Context context, long userId, List<Long> productLssueIds);

    /**
     * 更新购物车商品数量
     *
     * @param context
     * @param userId
     * @param productLssueId
     * @param number
     */
    void updateBuyCarNumber(Context context, long userId, long productLssueId, int number);

    /**
     * 添加商品进购物车
     *
     * @param context
     * @param userId
     * @param lssueId
     * @param number
     */
    void addBuyCarNumber(Context context, long userId, long lssueId, int number);

    /**
     * 查询当前用户的购物车商品信息
     *
     * @param context
     * @param userId
     * @return
     */
    <T> List<T> queryAllBuyNumbers(Context context, long userId);
}
