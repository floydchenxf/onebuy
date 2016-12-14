package com.yyg365.interestbar.biz.manager;

import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.channel.request.HttpMethod;

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
