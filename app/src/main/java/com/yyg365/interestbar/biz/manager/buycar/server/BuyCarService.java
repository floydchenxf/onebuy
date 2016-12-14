package com.yyg365.interestbar.biz.manager.buycar.server;

import android.content.Context;

import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.biz.vo.json.CarListVO;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;

import java.util.Collection;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public interface BuyCarService {

    /**
     * 添加购物车
     *
     * @param productLssueId
     * @param userId
     * @param number
     * @return
     */
    AsyncJob<Boolean> addCar(long productLssueId, long userId, int number);


    /**
     * 删除购物车
     *
     * @param carIds
     * @return
     */
    AsyncJob<Boolean> delCar(Collection<Long> carIds);

    /**
     * 获取购物车信息
     *
     * @param userId
     * @return
     */
    AsyncJob<CarListVO> fetchCarList(long userId);

}
