package com.yyg365.interestbar.biz.manager;

import android.text.TextUtils;

import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.aync.Func;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.vo.json.ProductLssueItemVO;
import com.yyg365.interestbar.biz.vo.json.SubjectInfoVO;
import com.yyg365.interestbar.biz.vo.json.SubjectPageDataVO;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/21.
 */
public class SpecialSubjectManager {

    public static AsyncJob<SubjectPageDataVO> fetchSubjectInfoById(Long id) {
        String url = APIConstants.HOST_API_PATH + APIConstants.SUBJECT_INFO_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GenSubjectInfo");
        params.put("id", id + "");

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        AsyncJob<Map<String, Object>> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return job.map(new Func<Map<String, Object>, SubjectPageDataVO>() {
            @Override
            public SubjectPageDataVO call(Map<String, Object> map) {
                SubjectPageDataVO vo = new SubjectPageDataVO();
                String time = map.get("ServerDateTime").toString();
                if (!TextUtils.isEmpty(time) && TextUtils.isDigitsOnly(time)) {
                    vo.ServerDateTime = Long.parseLong(time);
                }

                String subjectInfoStr = GsonHelper.getGson().toJson(map.get("SubjectInfo"));
                SubjectInfoVO infoVO = GsonHelper.getGson().fromJson(subjectInfoStr, SubjectInfoVO.class);
                vo.SubjectInfo = infoVO;

                String productListString = GsonHelper.getGson().toJson(map.get("ProductList"));
                Type type = new TypeToken<List<ProductLssueItemVO>>() {
                }.getType();
                List<ProductLssueItemVO> productVOs = GsonHelper.getGson().fromJson(productListString, type);
                if (productVOs != null && !productVOs.isEmpty()) {
                    List<WinningInfo> winningInfos = new ArrayList<WinningInfo>();
                    for (ProductLssueItemVO v : productVOs) {
                        WinningInfo info = new WinningInfo();
                        info.totalCount = v.TotalCount;
                        info.joinedCount = v.JoinedCount;
                        if (v.TotalCount == 0) {
                            info.processPrecent = "0%";
                        } else {
                            info.processPrecent = (v.JoinedCount * 100 / v.TotalCount) + "%";
                        }
                        info.productUrl = APIConstants.HOST + v.Pictures;
                        info.title = v.ProName;
                        info.id = v.ProID;
                        info.productId = v.ProID;
                        info.lssueId = v.ProductLssueID;
                        info.status = 0;
                        winningInfos.add(info);
                    }
                    vo.ProductList = winningInfos;
                }
                return vo;
            }
        });
    }
}
