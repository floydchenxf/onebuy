package com.yyg365.interestbar.ui.buycar;

import android.app.Activity;
import android.content.Context;

/**
 * Created by chenxiaofeng on 16/8/14.
 */
public interface BuycarOperator {

    /**
     * 设置ID
     * @param id
     */
    void setId(Long id);

    /**
     * 立即购买
     */
    void buyAtOnce();

    /**
     * 添加购物车
     */
    void addBuyCar();


    /**
     * 查看购物车
     */
    void viewBuyCar();
}
