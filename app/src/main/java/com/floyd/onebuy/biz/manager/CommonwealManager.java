package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHomeVO;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-7-24.
 */
public class CommonwealManager {

    public AsyncJob<CommonwealHomeVO> fetchCommonwealHomeData(int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ORDER_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetCommonwealHomeData");
        params.put("pageSize", pageSize+"");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CommonwealHomeVO.class);
    }
}
