package com.floyd.onebuy.biz.manager;

import android.content.Context;
import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.buycar.server.BuyCarService;
import com.floyd.onebuy.biz.manager.buycar.server.FridayCarService;
import com.floyd.onebuy.biz.manager.buycar.server.FundCarService;
import com.floyd.onebuy.biz.manager.buycar.server.NormalProductCarService;
import com.floyd.onebuy.biz.vo.json.CarItemVO;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.floyd.onebuy.dao.BuyCarNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-30.
 */
public class CarManager {

    private static Map<BuyCarType, BuyCarService> buycarServers = new HashMap<BuyCarType, BuyCarService>();

    static {
        buycarServers.put(BuyCarType.FUND, new FundCarService());
        buycarServers.put(BuyCarType.NORMAL, new NormalProductCarService());
        buycarServers.put(BuyCarType.FRI, new FridayCarService());
    }

    /**
     * 添加到购物车
     *
     * @param productLssueId 期数id
     * @param userId         当前用户
     * @return
     */
    public static AsyncJob<Boolean> addCar(BuyCarType type, long productLssueId, long userId, int number) {
        return buycarServers.get(type).addCar(productLssueId, userId, number);
    }

    /**
     * 删除购物车元素
     *
     * @param carIds
     * @return
     */
    public static AsyncJob<Boolean> delCar(BuyCarType type, Collection<Long> carIds) {
        return buycarServers.get(type).delCar(carIds);
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    public static AsyncJob<CarListVO> fetchCarList(BuyCarType type, long userId) {
        return buycarServers.get(type).fetchCarList(userId);

    }

    public static AsyncJob<List<WinningInfo>> fetchBuyCarList(BuyCarType type, final Context context, final long userId) {
        return buycarServers.get(type).fetchBuyCarList(context, userId);
    }

}
