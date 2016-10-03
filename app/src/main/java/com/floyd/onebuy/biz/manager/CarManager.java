package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.buycar.server.BuyCarService;
import com.floyd.onebuy.biz.manager.buycar.server.FridayCarService;
import com.floyd.onebuy.biz.manager.buycar.server.FundCarService;
import com.floyd.onebuy.biz.manager.buycar.server.NormalProductCarService;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.json.CarPayChannel;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
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

    /**
     *
     * @param payType db:夺宝 friday:快乐星期五 gy:公益 recharge:充值
     * @param userId
     * @return
     */
    public static AsyncJob<List<CarPayChannel>> getPayChannels(String payType, long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "PayChannel");
        params.put("type", payType);
        params.put("userId", userId+"");
        Type type = new TypeToken<Map<String, Object>>(){}.getType();
        AsyncJob<Map<String, Object>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, Object>, List<CarPayChannel>>() {
            @Override
            public List<CarPayChannel> call(Map<String, Object> stringListMap) {
                String payChannelString = GsonHelper.getGson().toJson(stringListMap.get("PayChannel"));
                Type ss = new TypeToken<List<CarPayChannel>>(){}.getType();
                return GsonHelper.getGson().fromJson(payChannelString, ss);
            }
        });
    }
}
