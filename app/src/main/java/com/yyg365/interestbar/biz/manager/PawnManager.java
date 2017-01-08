package com.yyg365.interestbar.biz.manager;

import com.google.gson.reflect.TypeToken;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.biz.vo.json.PawnLevelVO;
import com.yyg365.interestbar.biz.vo.json.PawnLogInfoVO;
import com.yyg365.interestbar.biz.vo.json.PawnLogVO;
import com.yyg365.interestbar.biz.vo.json.RedeemInfoVO;
import com.yyg365.interestbar.channel.request.HttpMethod;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 17/1/5.
 */
public class PawnManager {

    public static AsyncJob<List<DangPuItemVO>> fetchPawnProducts(Long userId, int pageNum, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PAWN_SHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenPawnLog");

        if (userId != null && userId > 0) {
            params.put("userid", userId + "");
        } else {
            params.put("userid", "");
        }
        params.put("pagesize", pageSize + "");
        params.put("pagenum", pageNum + "");

        Type type = new TypeToken<Map<String, List<DangPuItemVO>>>() {
        }.getType();

        AsyncJob<Map<String, List<DangPuItemVO>>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return result.map(new Func<Map<String, List<DangPuItemVO>>, List<DangPuItemVO>>() {
            @Override
            public List<DangPuItemVO> call(Map<String, List<DangPuItemVO>> map) {
                List<DangPuItemVO> r = new ArrayList<DangPuItemVO>();
                if (map == null || map.isEmpty()) {
                    return r;
                }
                return map.get("PawnshopLogList");
            }
        });
    }

    /**
     * 获取典当信息
     *
     * @param uid
     * @param proId
     * @param lssueId
     * @return
     */
    public static AsyncJob<PawnLogVO> fetchPawnLogVO(Long uid, Long proId, Long lssueId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PAWN_SHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenPawnLogPageInfo");
        params.put("userid", uid + "");
        params.put("proid", proId + "");
        params.put("prolssueid", lssueId + "");

        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, PawnLogVO.class);
    }

    /**
     * 创建典当
     *
     * @param uid
     * @param proId
     * @param lssueId
     * @param pawnLevelId
     * @return 返回典当价格
     */
    public static AsyncJob<Integer> createPawnLog(Long uid, Long proId, Long lssueId, Long pawnLevelId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PAWN_SHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreatePawnLog");
        params.put("userid", uid + "");
        params.put("proid", proId + "");
        params.put("prolssueid", lssueId + "");
        params.put("pawnlevel", pawnLevelId + "");
        Type type = new TypeToken<Map<String, Integer>>() {
        }.getType();
        AsyncJob<Map<String, Integer>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, Integer>, Integer>() {
            @Override
            public Integer call(Map<String, Integer> stringLongMap) {
                return stringLongMap.get("PawnPrice");
            }
        });
    }

    public static AsyncJob<RedeemInfoVO> fetchPawnRedeemInfoVO(Long id, Long uid) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PAWN_SHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenPawnLogInfo");
        params.put("userid", uid + "");
        params.put("id", id + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, RedeemInfoVO.class);
    }

    /**
     * 赎回记录创建
     *
     * @param id
     * @param uid
     * @param paychannel
     * @return 返回Id
     */
    public static AsyncJob<Long> createRedeemLog(Long id, Long uid, Long paychannel) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PAWN_SHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "Redeem");
        params.put("userid", uid + "");
        params.put("id", id + "");
        params.put("paychannel", paychannel + "");
        Type type = new TypeToken<Map<String, Long>>() {
        }.getType();
        AsyncJob<Map<String, Long>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, Long>, Long>() {
            @Override
            public Long call(Map<String, Long> stringLongMap) {
                return stringLongMap.get("RealRedeemPrice");
            }
        });
    }

}
