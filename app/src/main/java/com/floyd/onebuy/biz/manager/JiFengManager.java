package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.json.JiFengVO;
import com.floyd.onebuy.biz.vo.json.SignInVO;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-29.
 * <p/>
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
        params.put("userId", userId + "");
        AsyncJob<SignInVO> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, SignInVO.class);
        return job;
    }

    public static AsyncJob<List<JiFengVO>> fetchJiFengList(long userId, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JIFENG_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "list");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("jftype", "");
        Type type = new TypeToken<List<JiFengVO>>(){}.getType();
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
    }


}
