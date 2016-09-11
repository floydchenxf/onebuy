package com.floyd.onebuy.biz.manager;

import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.vo.json.ChargeOrderVO;
import com.floyd.onebuy.biz.vo.json.ChargeVO;
import com.floyd.onebuy.biz.vo.json.OrderPayVO;
import com.floyd.onebuy.biz.vo.json.OrderVO;
import com.floyd.onebuy.biz.vo.json.PayResultList;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-5.
 */
public class OrderManager {

    /**
     * 创建订单
     *
     * @param userId
     * @param productLssueDetail ProductLssueDetail 为购物详情 内
     *                           容为 期数 ID|数量 多个产品用逗号
     *                           相连接
     * @return
     */
    public static AsyncJob<OrderVO> createOrder(long userId, String productLssueDetail, int type) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateOrder");
        params.put("userId", userId + "");
        params.put("productLssueDetail", productLssueDetail);
        params.put("type", type + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, OrderVO.class);
    }

    public static AsyncJob<Boolean> payOrder(String orderNo) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "PayOrder");
        params.put("orderNum", orderNo);
        AsyncJob<Boolean> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, Boolean.class);
        return result;
    }


    /**
     * 创建订单
     *
     * @param userId
     * @param productLssueDetail
     * @return
     */
    public static AsyncJob<OrderPayVO> createAndPayOrder(BuyCarType type, long userId, String productLssueDetail, int payType) {

        AsyncJob<OrderVO> orderJob = null;
        if (type == BuyCarType.FRI) {
            orderJob = createFridayOrder(userId, productLssueDetail, payType);
        } else if (type == BuyCarType.NORMAL) {
            orderJob = createOrder(userId, productLssueDetail, payType);
        }
        AsyncJob<OrderPayVO> result = orderJob.flatMap(new Func<OrderVO, AsyncJob<OrderPayVO>>() {
            @Override
            public AsyncJob<OrderPayVO> call(final OrderVO orderVO) {
                AsyncJob<OrderPayVO> a = payOrder(orderVO.orderNum).map(new Func<Boolean, OrderPayVO>() {
                    @Override
                    public OrderPayVO call(Boolean b) {
                        OrderPayVO orderPayVO = new OrderPayVO();
                        orderPayVO.orderNum = orderVO.orderNum;
                        orderPayVO.result = b;
                        return orderPayVO;
                    }
                });
                return a;
            }
        });


        return result;

    }

    /**
     * 捐款模拟支付
     *
     * @param orderNum
     * @return
     */
    public static AsyncJob<Boolean> payWeal(String orderNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "PayWeal");
        params.put("orderNum", orderNum);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    /**
     * 模拟充值接口
     *
     * @param orderNum
     * @return
     */
    public static AsyncJob<Double> payCharge(String orderNum) {

        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "PayCharge");
        params.put("orderNum", orderNum);
        AsyncJob<Map<String, String>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Map.class);
        return result.map(new Func<Map<String, String>, Double>() {
            @Override
            public Double call(Map<String, String> m) {
                String moneyString = m.get("clientMoney");
                double result = 0;
                if (!TextUtils.isEmpty(moneyString)) {
                    result = Double.parseDouble(moneyString);
                }
                return result;
            }
        });
    }


    /**
     * 创建充值订单
     *
     * @param userId
     * @param money
     * @return
     */
    public static AsyncJob<ChargeOrderVO> createChargeOrder(Long userId, String money, int type) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "Recharge");
        params.put("userId", userId + "");
        params.put("money", money);
        params.put("type", type+"");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, ChargeOrderVO.class);
    }

    /**
     * 创建订单并支付
     *
     * @param userId
     * @param money
     * @return
     */
    public static AsyncJob<Double> createOrderAndPayCharge(Long userId, String money, int type) {
        final AsyncJob<Double> chargeOrderJob = createChargeOrder(userId, money, type).flatMap(new Func<ChargeOrderVO, AsyncJob<Double>>() {
            @Override
            public AsyncJob<Double> call(final ChargeOrderVO chargeOrderVO) {
                String orderNum = chargeOrderVO.orderNum;
                return payCharge(orderNum);
            }
        });

        return chargeOrderJob;
    }

    /**
     * 获取充值记录
     *
     * @param userId
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static AsyncJob<List<ChargeVO>> getRecharge(Long userId, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "ChargeList");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        Type type = new TypeToken<List<ChargeVO>>() {
        }.getType();
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);
    }

    /**
     * 创建星期五订单
     *
     * @param userId
     * @param productLssueDetail
     * @return
     */
    public static AsyncJob<OrderVO> createFridayOrder(long userId, String productLssueDetail, int type) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateFridayOrder");
        params.put("userId", userId + "");
        params.put("productLssueDetail", productLssueDetail);
        params.put("type", type + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, OrderVO.class);
    }


}
