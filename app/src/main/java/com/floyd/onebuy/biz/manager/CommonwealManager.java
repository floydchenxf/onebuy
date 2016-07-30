package com.floyd.onebuy.biz.manager;

import android.text.TextUtils;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealDetailJsonVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealDetailVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHelperList;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHomeVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealJsonVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealVO;
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

    /**
     * 公益首页接口
     *
     * @param pageSize
     * @return
     */
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

    /**
     * 分页获取公益信息
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
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

    /**
     * 获取捐款人
     *
     * @param pid      0或者空，代表获取全部
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static AsyncJob<CommonwealHelperList> fetchHelpPersonList(long pid, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.COMMONWEAL_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetHelpPersonList");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        params.put("pid", pid + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CommonwealHelperList.class);
    }

    /**
     * 获取公益详情
     *
     * @param pid
     * @return
     */
    public static AsyncJob<CommonwealDetailVO> fetchCommonwealDetail(long pid) {
        String url = APIConstants.HOST_API_PATH + APIConstants.COMMONWEAL_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetCommonwealDetail");
        params.put("pid", pid + "");
        AsyncJob<CommonwealDetailJsonVO> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CommonwealDetailJsonVO.class);
        return result.map(new Func<CommonwealDetailJsonVO, CommonwealDetailVO>() {
            @Override
            public CommonwealDetailVO call(CommonwealDetailJsonVO jsonVO) {
                CommonwealDetailVO vo = new CommonwealDetailVO();
                vo.TotalMoney = jsonVO.TotalMoney;
                vo.Content = jsonVO.Content;
                vo.Percent = jsonVO.Percent;
                vo.PersonList = jsonVO.PersonList;
                vo.ProductLssueID = jsonVO.ProductLssueID;
                vo.ProName = jsonVO.ProName;
                vo.RaiseCount = jsonVO.RaiseCount;
                vo.RaiseMoney = jsonVO.RaiseMoney;
                vo.Status = jsonVO.Status;

                if (TextUtils.isEmpty(jsonVO.TotalMoney) || TextUtils.isEmpty(jsonVO.RaiseMoney)) {
                    vo.percentNum = 0;
                } else {
                    Double total = Double.parseDouble(jsonVO.TotalMoney);
                    Double raise = Double.parseDouble(jsonVO.RaiseMoney);
                    vo.percentNum = (int)(raise * 100 / total);
                }

                if (!TextUtils.isEmpty(jsonVO.Pictures)) {
                    List<AdvVO> advVOs = new ArrayList<AdvVO>();
                    String[] pics = jsonVO.Pictures.split("\\|");
                    for (int i=0; i < pics.length; i++) {
                        AdvVO advVO = new AdvVO();
                        String pic = pics[i];
                        if (pic.startsWith("http")) {
                            advVO.imgUrl = pic;
                        } else {
                            advVO.imgUrl = APIConstants.HOST + pic;
                        }

                        advVOs.add(advVO);
                    }

                    vo.advVOList = advVOs;
                }

                return vo;
            }
        });
    }


}
