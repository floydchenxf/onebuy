package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.channel.request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-8-9.
 */
public class AppHandleManager {

    public static AsyncJob<Boolean> commitSugguest(String content, UserVO vo) {
        String url = APIConstants.HOST_API_PATH + APIConstants.APP_HANDLE_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "feedback");
        params.put("userId", vo.ID+"");
        params.put("content", content);
        params.put("mobile",vo.Mobile);

        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }
}
