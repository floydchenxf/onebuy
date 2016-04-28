package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.json.SignInVO;
import com.floyd.onebuy.channel.request.HttpMethod;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-4-29.
 *
 * 积分管理类
 */
public class JiFengManager {

    /**
     * 每日签到
     *
     * @return
     */
    public static AsyncJob<SignInVO> dailySignIn(long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JIFENG_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "add");
        params.put("userId", userId+"");
        AsyncJob<SignInVO> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, SignInVO.class);
        return job;
    }
}
