package com.floyd.onebuy.biz.manager.buycar.server;

import android.content.Context;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.dao.BuyCarNumber;

import java.util.Collection;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public class FundCarService implements BuyCarService {
    @Override
    public AsyncJob<Boolean> addCar(long productLssueId, long userId, int number) {
        return null;
    }

    @Override
    public AsyncJob<Boolean> delCar(Collection<Long> carIds) {
        return null;
    }

    @Override
    public AsyncJob<CarListVO> fetchCarList(long userId, int pageNo, int pageSize) {
        return null;
    }

    @Override
    public AsyncJob<List<WinningInfo>> fetchBuyCarList(Context context, long userId, int pageNo, int pageSize) {
        return null;
    }
}
