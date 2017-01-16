package com.yyg365.interestbar.biz.manager;

import com.google.gson.JsonElement;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.json.JFGoodsDetailVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsPayVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsVO;
import com.yyg365.interestbar.biz.vo.json.JFOrderDtailInfoVO;
import com.yyg365.interestbar.biz.vo.json.JiFenVO;
import com.yyg365.interestbar.biz.vo.json.SignInVO;
import com.yyg365.interestbar.channel.request.HttpMethod;
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
public class JiFenManager {

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

    public static AsyncJob<List<JiFenVO>> fetchJiFengList(long userId, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JIFENG_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "list");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("jftype", "");
        Type type = new TypeToken<List<JiFenVO>>() {
        }.getType();
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
    }

    /**
     * 获取积分商城list
     *
     * @param uid      用户id
     * @param protype
     * @param jfdown
     * @param jfup
     * @param sort
     * @param key
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static AsyncJob<List<JFGoodsVO>> fetchJFGoodList(Long uid, int protype, String jfdown, String jfup, int sort, String key, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JFSHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "JiFenProList");
        if (uid == null || uid <= 0) {
            params.put("userid", "");
        } else {
            params.put("userid", uid + "");
        }

        params.put("protype", protype + "");
        params.put("jfdown", jfdown);
        params.put("jfup", jfup);
        params.put("key", key);
        params.put("sort", sort + "");
        params.put("pagenum", pageNo + "");
        params.put("pagesize", pageSize + "");


        Type type = new TypeToken<Map<String, JsonElement>>() {
        }.getType();
        AsyncJob<Map<String, JsonElement>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, JsonElement>, List<JFGoodsVO>>() {
            @Override
            public List<JFGoodsVO> call(Map<String, JsonElement> map) {
                Type listType = new TypeToken<List<JFGoodsVO>>() {
                }.getType();
                return GsonHelper.getGson().fromJson(map.get("JFProductList"), listType);
            }
        });
    }

    public static AsyncJob<JFGoodsDetailVO> fetchJFGoodsDetail(Long id) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JFSHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "JiFenProInfo");
        params.put("id", id + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, JFGoodsDetailVO.class);
    }

    public static AsyncJob<JFGoodsPayVO> payJiFengGoods(Long uid, Long proId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JFSHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenJiFenProOrderInfo");
        params.put("userid", uid + "");
        params.put("proid", proId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, JFGoodsPayVO.class);
    }

    public static AsyncJob<Long> createJiFenProOrder(Long uid, Long proId, Long addressId, String phone) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JFSHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreateJiFenProOrder");
        params.put("userid", uid + "");
        params.put("proid", proId + "");
        params.put("addrid", addressId + "");
        params.put("phone", phone);
        Type mapType = new TypeToken<Map<String, Long>>(){}.getType();
        AsyncJob<Map<String, Long>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, mapType);
        return job.map(new Func<Map<String, Long>, Long>() {
            @Override
            public Long call(Map<String, Long> map) {
                return map.get("payJiFen");
            }
        });
    }

    public static AsyncJob<List<JFGoodsVO>> fetchMyJFGoods(Long uid, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JFSHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "MyJiFenProList");
        params.put("userid", uid + "");
        params.put("pagenum", pageNo + "");
        params.put("pagesize", pageSize + "");
        Type type = new TypeToken<Map<String, JsonElement>>() {
        }.getType();
        AsyncJob<Map<String, JsonElement>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, JsonElement>, List<JFGoodsVO>>() {
            @Override
            public List<JFGoodsVO> call(Map<String, JsonElement> map) {
                Type listType = new TypeToken<List<JFGoodsVO>>() {
                }.getType();
                return GsonHelper.getGson().fromJson(map.get("JFProductList"), listType);
            }
        });
    }

    public static AsyncJob<JFOrderDtailInfoVO> fetchMyJFOrderDetailInfo(Long uid, Long id) {
        String url = APIConstants.HOST_API_PATH + APIConstants.JFSHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenMyJiFenProOrderInfo");
        params.put("userid", uid + "");
        params.put("id", id + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, JFOrderDtailInfoVO.class);
    }

}
