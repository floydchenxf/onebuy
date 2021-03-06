package com.floyd.onebuy.biz.manager;

import android.app.Activity;
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
import com.floyd.onebuy.ui.share.ShareConstants;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;
import com.wangyin.payment.jdpaysdk.JDPay;
import com.wangyin.payment.jdpaysdk.open.model.JDPOpenPayParam;

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
     * @param type 用来传商品类别，1表示普通商品   2表示快乐星期五  3表示基金
     * @param paychannel 支付类型
     * @return
     */
    public static AsyncJob<OrderVO> createOrder(long userId, String productLssueDetail, int type, int paychannel) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateOrder");
        params.put("userId", userId + "");
        params.put("productLssueDetail", productLssueDetail);
        params.put("type", type + "");
        params.put("paychannel", paychannel + "");
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


    public static AsyncJob<OrderVO> createOrder(BuyCarType type, long userId, String productLssueDetail, int payType) {
        AsyncJob<OrderVO> orderJob = createOrder(userId, productLssueDetail, type.getCode(), payType);
        return orderJob;
    }


    /**
     * 创建订单
     *
     * @param userId
     * @param productLssueDetail
     * @return
     */
    public static AsyncJob<OrderPayVO> createAndPayOrder(BuyCarType type, long userId, String productLssueDetail, int payType) {

        AsyncJob<OrderVO> orderJob = createOrder(type, userId, productLssueDetail, payType);
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
     * 创建捐款
     *
     * @param userId
     * @param proId
     * @param money
     * @param remark
     * @param paychannel
     * @return
     */
    public static AsyncJob<OrderVO> createCommonwealOrder(Long userId, Long proId, String money, String remark, int paychannel) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateCommonwealOrder");
        params.put("userid", userId + "");
        params.put("id", proId + "");
        params.put("money", money);
        params.put("remark", remark);
        params.put("paychannel", paychannel + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, OrderVO.class);
    }

    public static AsyncJob<OrderVO> createPoolOrder(Long userId, Long id, String money, String remark, int paychannel) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreatePoolOrder");
        params.put("userid", userId + "");
        params.put("id", id + "");
        params.put("money", money);
        params.put("remark", remark);
        params.put("paychannel", paychannel + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, OrderVO.class);
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
        params.put("type", type + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, ChargeOrderVO.class);
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

    /**
     * 确认收货
     *
     * @param orderId
     * @param userId
     * @return
     */
    public static AsyncJob<Boolean> receiptGoods(long orderId, long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "ReceiptGoods");
        params.put("id", orderId+"");
        params.put("userid", userId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }


    public static void jdPay(Activity context, String orderId) {
        JDPOpenPayParam params = new JDPOpenPayParam();
        params.merchant = ShareConstants.MECHANT_ID;
        params.orderId = orderId;
        JDPay.openPay(context, params);
    }


}
