package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.json.OrderVO;
import com.floyd.onebuy.channel.request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-5-5.
 */
public class OrderManager {

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
    public static AsyncJob<OrderVO> createOrder(long userId, long productLssueDetail, String linkName,
                                                String linkMobile, String receivingAdrString, String remark) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateOrder");
        params.put("userId", userId + "");
        params.put("productLssueDetail", productLssueDetail + "");
        params.put("linkName", linkName);
        params.put("linkMobile", linkMobile);
        params.put("receivingAdrString", receivingAdrString);
        params.put("remark", remark);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, OrderVO.class);
    }
}
