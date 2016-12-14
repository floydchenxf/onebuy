package com.yyg365.interestbar.biz.manager;

import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.bean.MD5Util;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.json.NewsVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.channel.request.HttpMethod;
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
