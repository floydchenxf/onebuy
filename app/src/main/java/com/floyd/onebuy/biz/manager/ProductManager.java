package com.floyd.onebuy.biz.manager;

import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.aync.HttpJobFactory;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.biz.func.StringFunc;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.json.IndexAdvVO;
import com.floyd.onebuy.biz.vo.json.IndexVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueItemVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueListVO;
import com.floyd.onebuy.biz.vo.model.NewIndexVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.biz.vo.product.JoinVO;
import com.floyd.onebuy.biz.vo.product.ProgressVO;
import com.floyd.onebuy.biz.vo.product.WinningDetailInfo;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-21.
 */
public class ProductManager {

    /**
     * 获取首页数据
     *
     * @return
     */
    public static AsyncJob<NewIndexVO> fetchIndexData() {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "IndexData");
        AsyncJob<IndexVO> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, IndexVO.class);
        return job.map(new Func<IndexVO, NewIndexVO>() {
            @Override
            public NewIndexVO call(IndexVO indexVO) {
                if (indexVO == null) {
                    return null;
                }
                NewIndexVO vo = new NewIndexVO();
                vo.wordList = indexVO.wordList;
                vo.typeList = indexVO.typeList;
                vo.newsImageUrl = APIConstants.HOST + indexVO.Image;
                List<IndexAdvVO> adv = indexVO.advertisList;
                if (adv != null && !adv.isEmpty()) {
                    List<AdvVO> advList = new ArrayList<AdvVO>();
                    for (IndexAdvVO a : adv) {
                        AdvVO b = new AdvVO();
                        b.title = a.Url;
                        b.id = a.NewsID;
                        b.imgUrl = APIConstants.HOST + a.SmallPic;
                        advList.add(b);
                    }
                    vo.advertisList = advList;
                }

                List<ProductLssueItemVO> productVOs = indexVO.theNewList;
                if (productVOs != null && !productVOs.isEmpty()) {
                    List<WinningInfo> winningInfos = new ArrayList<WinningInfo>();
                    for (ProductLssueItemVO v : productVOs) {
                        WinningInfo info = new WinningInfo();
                        info.totalCount = v.TotalCount;
                        info.joinedCount = v.JoinedCount;
                        info.processPrecent = v.Percent;
                        info.productUrl = APIConstants.HOST + v.Pictures;
                        info.title = v.ProName;
                        info.id = v.ProID;
                        info.lssueId = v.ProductLssueID;
                        info.status = 1;
                        winningInfos.add(info);
                    }
                    vo.theNewList = winningInfos;
                }

                return vo;
            }
        });
    }


    /**
     * 获取商品列表
     *
     * @param pageSize
     * @param pageNum
     * @param typeId
     * @param sort
     * @return
     */
    public static AsyncJob<List<WinningInfo>> fetchProductLssueVOs(int pageSize, int pageNum, long typeId, int sort) {

        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "ProductLssueTotal");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("typeID", typeId + "");
        params.put("sort", sort + "");
        Type type = new TypeToken<ArrayList<ProductLssueItemVO>>() {
        }.getType();
        AsyncJob<ProductLssueListVO> s = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, ProductLssueListVO.class);

        return s.map(new Func<ProductLssueListVO, List<WinningInfo>>() {
            @Override
            public List<WinningInfo> call(ProductLssueListVO productLssueVO) {
                List<WinningInfo> result = new ArrayList<WinningInfo>();
                if (productLssueVO == null) {
                    return result;
                }

                List<ProductLssueItemVO> productLssueVOs = productLssueVO.ProductLssueList;
                if (productLssueVOs == null || productLssueVOs.isEmpty()) {
                    return result;
                }

                for (ProductLssueItemVO vo : productLssueVOs) {
                    WinningInfo info = new WinningInfo();
                    info.joinedCount = vo.JoinedCount;
                    info.totalCount = vo.TotalCount;
                    info.id = vo.ProID;
                    info.status = 1;
                    info.productUrl = APIConstants.HOST + vo.Pictures;
                    info.title = vo.ProName;
                    result.add(info);
                }
                return result;
            }
        });
    }

    /**
     * 获取期数商品详情
     *
     * @param lssueId
     * @param userId
     * @return
     */
    public static AsyncJob<WinningDetailInfo> fetchProductLssuePageData(long lssueId, Long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getProductLssuePageData");
        params.put("ID", lssueId + "");
        if (userId != null) {
            params.put("userId", userId + "");
        }

        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<WinningDetailInfo>>() {
            @Override
            public AsyncJob<WinningDetailInfo> call(final String s) {
                return new AsyncJob<WinningDetailInfo>() {
                    @Override
                    public void start(ApiCallback<WinningDetailInfo> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONObject data = j.getJSONObject("data");
                                WinningDetailInfo detailInfo = convert2Obj(data);
                                callback.onSuccess(detailInfo);
                            } else {
                                String msg = j.getString("info");
                                callback.onError(APIError.API_BIZ_ERROR, msg);
                            }
                        } catch (JSONException e) {
                            callback.onError(APIError.API_JSON_PARSE_ERROR, e.getMessage());
                        }

                    }
                };
            }
        });
    }

    private static WinningDetailInfo convert2Obj(JSONObject data) throws JSONException {
        WinningDetailInfo detailInfo = new WinningDetailInfo();

        JSONObject productLssue = data.getJSONObject("ProductLssue");
        if (productLssue.has("productImages")) {
            String images = productLssue.getString("productImages");
            if (!TextUtils.isEmpty(images)) {
                List<AdvVO> advVOs = new ArrayList<AdvVO>();
                String[] ii = images.split("\\|");
                int idx = 0;
                for (String i : ii) {
                    AdvVO vo = new AdvVO();
                    vo.imgUrl = APIConstants.PIC_PATH_110 + i;
                    vo.title = APIConstants.PIC_PATH_110 + i;
                    vo.id = ++idx;
                    advVOs.add(vo);
                }
                detailInfo.advVOList = advVOs;
            }

        }
        String proName = productLssue.getString("ProName");
        detailInfo.productTitle = proName;
        detailInfo.id = productLssue.getLong("ProductLssueID");
        detailInfo.status = productLssue.getInt("Status");

        if (productLssue.has("processVO")) {
            String processVO = productLssue.getString("processVO");
            ProgressVO progressVO = GsonHelper.getGson().fromJson(processVO, ProgressVO.class);
            detailInfo.progressVO = progressVO;
        }

        if (data.has("joinList")) {
            String joinStr = data.getString("joinList");
            Type type = new TypeToken<ArrayList<JoinVO>>() {
            }.getType();
            List<JoinVO> joinVOs = GsonHelper.getGson().fromJson(joinStr, type);
            detailInfo.allJoinedRecords = joinVOs;
        }

        return detailInfo;
    }

    /**
     * 获取期数商品的购买者列表
     *
     * @param pageSize
     * @param pageNum
     * @param productLssueId
     * @return
     */
    public static AsyncJob<List<JoinVO>> getProductLssueJoinedList(int pageSize, int pageNum, long productLssueId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetClientProductLssueRecord");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("productLssueID", productLssueId + "");
        Type type = new TypeToken<List<JoinVO>>() {
        }.getType();
        AsyncJob<List<JoinVO>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return result;
    }
}
