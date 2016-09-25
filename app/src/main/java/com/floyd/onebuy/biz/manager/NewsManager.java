package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.bean.MD5Util;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.json.NewsVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/24.
 */
public class NewsManager {

    /**
     * 获取重要公告
     *
     * @return
     */
    public static AsyncJob<List<NewsVO>> fetchNews(int pageNum, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.NEWS_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenNewsList");
        params.put("pageNum", pageNum + "");
        params.put("pageSize", pageSize + "");
        Type type = new TypeToken<List<NewsVO>>() {
        }.getType();
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);
    }
}
