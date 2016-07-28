package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHomeVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealJsonVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealVO;
import com.floyd.onebuy.biz.vo.commonweal.HelpPersionVO;
import com.floyd.onebuy.biz.vo.json.IndexAdvVO;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-7-24.
 */
public class CommonwealManager {

    public static AsyncJob<CommonwealHomeVO> fetchCommonwealHomeData(int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.COMMONWEAL_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetCommonwealHomeData");
        params.put("pageSize", pageSize + "");
        AsyncJob<CommonwealJsonVO> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CommonwealJsonVO.class);
        return result.map(new Func<CommonwealJsonVO, CommonwealHomeVO>() {
            @Override
            public CommonwealHomeVO call(CommonwealJsonVO v) {
                if (v == null) {
                    return null;
                }

                CommonwealHomeVO r = new CommonwealHomeVO();
                r.TotalMoney = v.TotalMoney;
                r.TypeList = v.TypeList;
                r.FoundationList = v.FoundationList;
                if (v.Advertis != null) {
                    List<AdvVO> advList = new ArrayList<AdvVO>();
                    for (IndexAdvVO iav : v.Advertis) {
                        AdvVO av = new AdvVO();
                        av.title = iav.Url;
                        if (iav.NewsID != null) {
                            av.id = iav.NewsID;
                        }
                        av.imgUrl = APIConstants.HOST + iav.SmallPic;
                        advList.add(av);
                    }

                    r.Advertis = advList;
                }

                return r;
            }
        });
    }

    public static AsyncJob<List<CommonwealVO>> fetchCommonwealList(int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.COMMONWEAL_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetCommonwealList");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        Type type = new TypeToken<Map<String, List<CommonwealVO>>>() {
        }.getType();
        AsyncJob<Map<String, List<CommonwealVO>>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);

        return result.map(new Func<Map<String, List<CommonwealVO>>, List<CommonwealVO>>() {
            @Override
            public List<CommonwealVO> call(Map<String, List<CommonwealVO>> stringListMap) {
                return stringListMap.get("FoundationList");
            }
        });
    }

    public static AsyncJob<List<HelpPersionVO>> fetchHelpPersonList(long pid, int pageNo, int  pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.COMMONWEAL_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetHelpPersonList");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        params.put("pid", pid + "");
        return null;
    }

}
