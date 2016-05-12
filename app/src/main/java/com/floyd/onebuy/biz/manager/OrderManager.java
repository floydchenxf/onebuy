package com.floyd.onebuy.biz.manager;

import android.text.TextUtils;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
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

    public static AsyncJob<OrderPayVO> createAndPayOrder(long userId, String productLssueDetail, String linkName,
                                                         String linkMobile, String receivingAdrString, String remark) {
        AsyncJob<OrderPayVO> result = createOrder(userId, productLssueDetail, linkName, linkMobile, receivingAdrString, remark).flatMap(new Func<OrderVO, AsyncJob<OrderPayVO>>() {
            @Override
            public AsyncJob<OrderPayVO> call(final OrderVO orderVO) {
                AsyncJob<OrderPayVO> a = payOrder(orderVO.orderNum, "TAOBAOTEST").map(new Func<List<String>, OrderPayVO>() {
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
}
