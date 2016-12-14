package com.yyg365.interestbar.ui;

/**
 * Created by chenxiaofeng on 16/10/3.
 */
public interface PrizeListener {

    /**
     * 中奖回调
     *
     * @param prizeCode
     */
    public void prizeCallback(String prizeCode, String productTitle, String productUrl);
}
