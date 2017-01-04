package com.yyg365.interestbar.biz.manager;

import com.google.gson.reflect.TypeToken;
import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.AdvVO;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealHomeVO;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealJsonNewVO;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealVO;
import com.yyg365.interestbar.biz.vo.json.CommonwealAdvVO;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
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
            params.put("userid", userId+"");
        } else {
            params.put("userid", "");
        }
        params.put("pagesize", pageSize + "");
        params.put("pagenum", pageNum+"");

        Type type = new TypeToken<Map<String, List<DangPuItemVO>>>() {
        }.getType();

        AsyncJob<Map<String, List<DangPuItemVO>>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return result.map(new Func<Map<String, List<DangPuItemVO>>, List<DangPuItemVO>>() {
            @Override
            public List<DangPuItemVO> call(Map<String, List<DangPuItemVO>> map) {
                List<DangPuItemVO> r = new ArrayList<DangPuItemVO>();
                if (map == null||map.isEmpty()) {
                    return r;
                }
                return map.get("PawnshopLogList");
            }
        });
    }
}
