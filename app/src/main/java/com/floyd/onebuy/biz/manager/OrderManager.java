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
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-5.
 */
public class OrderManager {

    public static final String TAOBAOTEST = "TAOBAOTEST";

    /**
     * 创建订单
     *
     * @param userId
     * @param productLssueDetail ProductLssueDetail 为购物详情 内
     *                           容为 期数 ID|数量 多个产品用逗号
     *                           相连接
     * @param linkName
     * @param linkMobile
     * @param receivingAdrString
     * @param remark
     * @return
     */
    public static AsyncJob<OrderVO> createOrder(long userId, String productLssueDetail, String linkName,
                                                String linkMobile, String receivingAdrString, String remark) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateOrder");
        params.put("userId", userId + "");
        params.put("productLssueDetail", productLssueDetail);
        params.put("linkName", linkName);
        params.put("linkMobile", linkMobile);
        params.put("receivingAdrString", receivingAdrString);
        params.put("remark", remark);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, OrderVO.class);
    }

    public static AsyncJob<List<String>> payOrder(String orderNo, String taobaoNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "PayOrder");
        params.put("orderNum", orderNo);
        params.put("taobaoNum", taobaoNum);
        Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        AsyncJob<Map<String, String>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, mapType);
        return result.map(new Func<Map<String, String>, List<String>>() {
            @Override
            public List<String> call(Map<String, String> map) {
                String list = map.get("ClientCodeList");
                if (TextUtils.isEmpty(list)) {
                    return null;
                }
                String[] l = list.split(",");
                return Arrays.asList(l);
            }
        });
    }


    /**
     * 创建订单
     *
     * @param userId
     * @param productLssueDetail
     * @param linkName
     * @param linkMobile
     * @param receivingAdrString
     * @param remark
     * @return
     */
    public static AsyncJob<OrderPayVO> createAndPayOrder(BuyCarType type, long userId, String productLssueDetail, String linkName,
                                                         String linkMobile, String receivingAdrString, String remark) {

        AsyncJob<OrderVO> orderJob = null;
        if (type == BuyCarType.FRI) {
            orderJob = createFridayOrder(userId, productLssueDetail, linkName, linkMobile, receivingAdrString,remark);
        } else if (type == BuyCarType.NORMAL) {
            orderJob = createOrder(userId, productLssueDetail, linkName, linkMobile, receivingAdrString, remark);
        }
        AsyncJob<OrderPayVO> result = orderJob.flatMap(new Func<OrderVO, AsyncJob<OrderPayVO>>() {
            @Override
            public AsyncJob<OrderPayVO> call(final OrderVO orderVO) {
                AsyncJob<OrderPayVO> a = payOrder(orderVO.orderNum,  TAOBAOTEST).map(new Func<List<String>, OrderPayVO>() {
                    @Override
                    public OrderPayVO call(List<String> strings) {
                        OrderPayVO orderPayVO = new OrderPayVO();
                        orderPayVO.orderNum = orderVO.orderNum;
                        orderPayVO.payList = strings;
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

    public static AsyncJob<Double> payCharge(String orderNum, String taobaoNum) {

        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "PayCharge");
        params.put("orderNum", orderNum);
        params.put("taobaoNum", taobaoNum);
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
    public static AsyncJob<ChargeOrderVO> createChargeOrder(Long userId, String money) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "Recharge");
        params.put("userId", userId + "");
        params.put("money", money);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, ChargeOrderVO.class);
    }

    /**
     * 创建订单并支付
     *
     * @param userId
     * @param money
     * @return
     */
    public static AsyncJob<Double> createOrderAndPayCharge(Long userId, String money) {
        final AsyncJob<Double> chargeOrderJob = createChargeOrder(userId, money).flatMap(new Func<ChargeOrderVO, AsyncJob<Double>>() {
            @Override
            public AsyncJob<Double> call(final ChargeOrderVO chargeOrderVO) {
                String orderNum = chargeOrderVO.orderNum;
                return payCharge(orderNum, TAOBAOTEST);
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

    public static AsyncJob<OrderVO> createFridayOrder(long userId, String productLssueDetail, String linkName,
                                                String linkMobile, String receivingAdrString, String remark) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateFridayOrder");
        params.put("userId", userId + "");
        params.put("productLssueDetail", productLssueDetail);
        params.put("linkName", linkName);
        params.put("linkMobile", linkMobile);
        params.put("receivingAdrString", receivingAdrString);
        params.put("remark", remark);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, OrderVO.class);
    }


}
