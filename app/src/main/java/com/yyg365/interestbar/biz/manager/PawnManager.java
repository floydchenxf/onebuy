package com.yyg365.interestbar.biz.manager;

import com.google.gson.reflect.TypeToken;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.biz.vo.json.PawnLevelVO;
import com.yyg365.interestbar.biz.vo.json.PawnLogInfoVO;
import com.yyg365.interestbar.biz.vo.json.PawnLogVO;
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
//        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, PawnLogVO.class);
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        AsyncJob<Map<String, Object>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, Object>, PawnLogVO>() {
            @Override
            public PawnLogVO call(Map<String, Object> map) {
                PawnLogVO vo = new PawnLogVO();
                Object a = map.get("PawnLogInfo");
                if (a != null) {
                    vo.pawnLogInfoVO = GsonHelper.getGson().fromJson(GsonHelper.getGson().toJson(a), PawnLogInfoVO.class);
                }

                Object b = map.get("PawnLevelList");
                if (b != null) {
                    Type type = new TypeToken<List<PawnLevelVO>>() {
                    }.getType();
                    vo.pawnLevelVOs = GsonHelper.getGson().fromJson(GsonHelper.getGson().toJson(b), type);
                }
                return vo;
            }
        });
    }

    public static AsyncJob<Boolean> createPawnLog(Long uid, Long proId, Long lssueId, Long pawnLevelId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PAWN_SHOP_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CreatePawnLog");
        params.put("userid", uid + "");
        params.put("proid", proId + "");
        params.put("prolssueid", lssueId + "");
        params.put("pawnlevel", pawnLevelId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, Boolean.class);
    }
}
