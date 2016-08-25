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
import com.floyd.onebuy.biz.vo.fund.FundJsonVO;
import com.floyd.onebuy.biz.vo.json.HistoryPrizeListVO;
import com.floyd.onebuy.biz.vo.json.IndexAdvVO;
import com.floyd.onebuy.biz.vo.json.IndexVO;
import com.floyd.onebuy.biz.vo.json.OwnerExtVO;
import com.floyd.onebuy.biz.vo.json.PrizeShowListVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueItemVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueListVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueWithWinnerVO;
import com.floyd.onebuy.biz.vo.model.NewIndexVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.biz.vo.product.JoinVO;
import com.floyd.onebuy.biz.vo.product.OwnerVO;
import com.floyd.onebuy.biz.vo.product.ProgressVO;
import com.floyd.onebuy.biz.vo.product.WinningDetailInfo;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
                        if (a == null) {
                            continue;
                        }
                        AdvVO b = new AdvVO();
                        b.title = a.Url;
                        if (a.NewsID != null) {
                            b.id = a.NewsID;
                        }
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
                    vo.theNewList = winningInfos;
                }

                return vo;
            }
        });
    }


    public static AsyncJob<NewIndexVO> fetchAllProducts(int pageSize, long typeId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetAllProducts");
        params.put("pageSize", pageSize + "");
        params.put("typeId", typeId + "");
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
                        if (a == null) {
                            continue;
                        }
                        AdvVO b = new AdvVO();
                        b.title = a.Url;
                        if (a.NewsID != null) {
                            b.id = a.NewsID;
                        }
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

                List<ProductLssueVO> productLssueVOs = productLssueVO.ProductLssueList;
                if (productLssueVOs == null || productLssueVOs.isEmpty()) {
                    return result;
                }

                for (ProductLssueVO vo : productLssueVOs) {
                    WinningInfo info = new WinningInfo();
                    info.joinedCount = vo.JoinedCount;
                    info.totalCount = vo.TotalCount;
                    info.lssueId = vo.ProductLssueID;
                    info.id = vo.ProID;
                    info.productId = vo.ProID;
                    info.status = vo.Status;
                    if (vo.TotalCount == 0) {
                        info.processPrecent = "0%";
                    } else {
                        info.processPrecent = (vo.JoinedCount * 100 / vo.TotalCount) + "%";
                    }
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
     * @param lssueId 可以不传 0
     * @param cid     必须有，商品id
     * @param userId  可以不传
     * @return
     */
    public static AsyncJob<WinningDetailInfo> fetchProductLssueDetail(Long lssueId, long cid, Long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetProductLssueDetail");
        params.put("cId", cid + "");
        String lssueIdStr = "";
        if (lssueId != null) {
            lssueIdStr = lssueId + "";
        }
        params.put("productLssueID", lssueIdStr);

        String userIdStr = "";
        if (userId != null && userId != 0l) {
            userIdStr = userId + "";
        }
        params.put("userId", userIdStr);

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
                    vo.imgUrl = APIConstants.PIC_PATH_360 + i;
                    vo.title = APIConstants.PIC_PATH_360 + i;
                    vo.id = ++idx;
                    advVOs.add(vo);
                }
                detailInfo.advVOList = advVOs;
            }

        }
        String proName = productLssue.getString("ProName");
        long proId = productLssue.getLong("ProID");
        detailInfo.proId = proId;
        detailInfo.productTitle = proName;
        detailInfo.id = productLssue.getLong("ProductLssueID");
        detailInfo.status = productLssue.getInt("Status");
        detailInfo.code = productLssue.getString("ProductLssueCode");

        if (productLssue.has("winnerInfo")) {
            JSONObject o = productLssue.getJSONObject("winnerInfo");
            OwnerVO ownerVO = new OwnerVO();
            ownerVO.userId = o.getLong("PrizeClientID");
            ownerVO.userName = o.getString("PrizeClientName");
            //FIXME 服务端放在了外面对象中
            ownerVO.winNumber = productLssue.getString("PrizeCode");
            ownerVO.avatar = o.getString("PrizeClientPic");
            ownerVO.winTime = o.getLong("PriceTime") * 1000;
            detailInfo.ownerVO = ownerVO;
        }

        if (productLssue.has("processVO")) {
            String processVO = productLssue.getString("processVO");
            ProgressVO progressVO = GsonHelper.getGson().fromJson(processVO, ProgressVO.class);
            detailInfo.progressVO = progressVO;
        }

        if (productLssue.has("PriceTime")) {
            detailInfo.priceTime = (productLssue.getLong("PriceTime") + 10) * 1000;
        }

        if (data.has("joinList")) {
            String joinStr = data.getString("joinList");
            Type type = new TypeToken<ArrayList<JoinVO>>() {
            }.getType();
            List<JoinVO> joinVOs = GsonHelper.getGson().fromJson(joinStr, type);
            detailInfo.allJoinedRecords = joinVOs;
        }

        //FIXME new add
        if (data.has("ClientCodeList")) {
            String codes = data.getString("ClientCodeList");
            if (!TextUtils.isEmpty(codes)) {
                String[] codeArray = codes.split(",");
                detailInfo.myRecords = Arrays.asList(codeArray);
            }
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

    /**
     * 往期中奖信息揭晓
     *
     * @param pageSize 　页面容量大小
     * @param pageNum  页数
     * @param proId    商品ID
     */
    public static AsyncJob<HistoryPrizeListVO> getHistoryPrizes(int pageSize, int pageNum, long proId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetHistoryPrize");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("cId", proId + "");
        AsyncJob<HistoryPrizeListVO> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, HistoryPrizeListVO.class);
        return result;
    }


    /**
     * 获取商品期数晒单
     *
     * @param productLssueID
     * @param pageSize
     * @param pageNum
     * @return
     */
    public static AsyncJob<PrizeShowListVO> getPrizeShow(long productLssueID, int typeID, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getPrizeShow");
        params.put("typeID", typeID + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("productLssueID", productLssueID + "");
        AsyncJob<PrizeShowListVO> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, PrizeShowListVO.class);
        return result;
    }

    /**
     * 获取我的晒单信息
     *
     * @param userId
     * @param typeID
     * @param pageSize
     * @param pageNum
     * @return
     */
    public static AsyncJob<PrizeShowListVO> getMyPrizeShow(long userId, int typeID, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetClientPrizeShow");
        params.put("typeID", typeID + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        AsyncJob<PrizeShowListVO> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, PrizeShowListVO.class);
        return result;
    }

    /**
     * 搜索产品信息
     *
     * @param keywords
     * @param pageSize
     * @param pageNum
     * @return
     */
    public static AsyncJob<List<WinningInfo>> searchProduct(String keywords, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "Search");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("keyWords", keywords);
        AsyncJob<ProductLssueListVO> s = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, ProductLssueListVO.class);
        return s.map(new Func<ProductLssueListVO, List<WinningInfo>>() {
            @Override
            public List<WinningInfo> call(ProductLssueListVO productLssueVO) {
                List<WinningInfo> result = new ArrayList<WinningInfo>();
                if (productLssueVO == null) {
                    return result;
                }

                List<ProductLssueVO> productLssueVOs = productLssueVO.ProductLssueList;
                if (productLssueVOs == null || productLssueVOs.isEmpty()) {
                    return result;
                }

                for (ProductLssueVO vo : productLssueVOs) {
                    WinningInfo info = new WinningInfo();
                    info.joinedCount = vo.JoinedCount;
                    info.totalCount = vo.TotalCount;
                    info.id = vo.ProID;
                    info.productId = vo.ProID;
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
     * 获取最新揭晓商品
     *
     * @param pageSize
     * @param pageNum
     * @return
     */
    public static AsyncJob<List<WinningInfo>> getNewestProductLssues(int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetNewestCount");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");

        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<List<WinningInfo>>>() {
            @Override
            public AsyncJob<List<WinningInfo>> call(final String s) {
                return new AsyncJob<List<WinningInfo>>() {
                    @Override
                    public void start(ApiCallback<List<WinningInfo>> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONObject data = j.getJSONObject("data");
                                List<WinningInfo> winningInfos = convert2WinningInfs(data);
//                                if (winningInfos == null || winningInfos.isEmpty()) {
//                                    callback.onError(APIError.API_CONTENT_EMPTY, "内容为空");
//                                    return;
//                                }
                                callback.onSuccess(winningInfos);
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

    private static List<WinningInfo> convert2WinningInfs(JSONObject data) throws JSONException {
        JSONArray winningInfoJsonArray = data.getJSONArray("ProductLssueList");
        if (winningInfoJsonArray == null || winningInfoJsonArray.length() <= 0) {
            return Collections.EMPTY_LIST;
        }
        List<WinningInfo> infoList = new ArrayList<WinningInfo>();
        for (int i = 0; i < winningInfoJsonArray.length(); i++) {
            WinningInfo info = new WinningInfo();
            JSONObject wjson = winningInfoJsonArray.getJSONObject(i);
            info.lssueId = wjson.getLong("ProductLssueID");
            info.id = info.lssueId;
            info.code = wjson.getString("Code");
            info.productId = wjson.getLong("ProID");
            info.totalCount = wjson.getInt("TotalCount");
            info.joinedCount = wjson.getInt("JoinedCount");
            info.title = wjson.getString("ProName");
            info.status = wjson.getInt("Status");
            info.productUrl = APIConstants.HOST + wjson.getString("Pictures");
            if (wjson.has("PriceTime")) {
                info.lotteryTime = (wjson.getLong("PriceTime") + 10) * 1000;
            }
            if (wjson.has("winnerInfo")) {
                OwnerVO ownerVO = convert2OwnerVO(wjson);
                ownerVO.joinNumber = info.myPrizeCodes == null ? 0 : info.myPrizeCodes.size();
                info.ownerVO = ownerVO;
            }
            infoList.add(info);
        }
        return infoList;
    }

    private static OwnerVO convert2OwnerVO(JSONObject wjson) throws JSONException {
        OwnerVO ownerVO = new OwnerVO();
        JSONObject ownerInfoJson = wjson.getJSONObject("winnerInfo");
        ownerVO.userId = ownerInfoJson.getLong("PrizeClientID");
        ownerVO.avatar = ownerInfoJson.getString("PrizeClientPic");
        ownerVO.userName = ownerInfoJson.getString("PrizeClientName");
        if (ownerInfoJson.has("PrizeCode")) {
            ownerVO.winNumber = ownerInfoJson.getString("PrizeCode");
        }

        if (ownerInfoJson.has("PriceTime")) {
            ownerVO.winTime = ownerInfoJson.getLong("PriceTime") * 1000;
        }
        return ownerVO;
    }

    /**
     * 根据订单号获取夺宝记录
     *
     * @param userId
     * @param orderNo
     * @return
     */
    public static AsyncJob<List<WinningInfo>> getWinningInfosByOrderNo(Long userId, String orderNo) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetProductLssueRecordByOrderNumber");
        params.put("userId", userId + "");
        params.put("orderNo", orderNo + "");
        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<List<WinningInfo>>>() {
            @Override
            public AsyncJob<List<WinningInfo>> call(final String s) {
                return new AsyncJob<List<WinningInfo>>() {
                    @Override
                    public void start(ApiCallback<List<WinningInfo>> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONArray data = j.getJSONArray("data");
                                List<WinningInfo> winningInfos = convert2MyOrderCodes(data);
                                if (winningInfos == null || winningInfos.isEmpty()) {
                                    callback.onError(APIError.API_CONTENT_EMPTY, "内容为空");
                                    return;
                                }
                                callback.onSuccess(winningInfos);
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

    private static List<WinningInfo> convert2MyOrderCodes(JSONArray data) throws JSONException {
        if (data == null || data.length() <= 0) {
            return null;
        }

        List<WinningInfo> infoList = new ArrayList<WinningInfo>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject oo = data.getJSONObject(i);
            JSONArray orderArray = oo.getJSONArray("order");
            for (int k = 0; k < orderArray.length(); k++ ) {
                WinningInfo info = new WinningInfo();
                JSONObject wjson = orderArray.getJSONObject(k);
                info.lssueId = wjson.getLong("ProductLssueID");
                info.id = info.lssueId;
                info.code = wjson.getString("ProductLssueCode");
                info.productId = wjson.getLong("ProID");
                info.totalCount = wjson.getInt("TotalCount");
                info.joinedCount = wjson.getInt("JoinCount");
                info.title = wjson.getString("ProName");
                info.status = wjson.getInt("Status");
                info.productUrl = APIConstants.HOST + wjson.getString("Pictures");
                String priceTime = wjson.getString("PriceTime");
                info.lotteryTime = TextUtils.isEmpty(priceTime) ? 0 : (Long.parseLong(priceTime) + 10) * 1000;
                if (wjson.has("ClientCodeList")) {
                    List<String> codeList = new ArrayList<String>();
                    String aa = wjson.getString("ClientCodeList");
                    if (!TextUtils.isEmpty(aa)) {
                        String[] jj = aa.split(",");
                        for (int j = 0; j < jj.length; j++) {
                            String clientCode = jj[j];
                            codeList.add(clientCode);
                        }
                    }

                    info.myPrizeCodes = codeList;
                }
                infoList.add(info);
            }
        }
        return infoList;
    }

    /**
     * 根据期数获取获奖者信息
     *
     * @param lssueId
     * @return
     */
    public static AsyncJob<OwnerExtVO> getWinnerInfo(final long lssueId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetWinnerInfo");
        params.put("productLssueID", lssueId + "");
        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<OwnerExtVO>>() {
            @Override
            public AsyncJob<OwnerExtVO> call(final String s) {

                return new AsyncJob<OwnerExtVO>() {
                    @Override
                    public void start(ApiCallback<OwnerExtVO> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONObject data = j.getJSONObject("data");
                                int winnerStatus = data.getInt("Status");
                                OwnerVO ownerVO = convert2OwnerVO(data);
                                if (ownerVO == null) {
                                    callback.onError(APIError.API_CONTENT_EMPTY, "内容为空");
                                    return;
                                }

                                OwnerExtVO ownerExtVO = new OwnerExtVO();
                                ownerExtVO.status = winnerStatus;
                                ownerExtVO.userName = ownerVO.userName;
                                ownerExtVO.winNumber = ownerVO.winNumber;
                                ownerExtVO.avatar = ownerVO.avatar;
                                ownerExtVO.userId = ownerVO.userId;
                                ownerExtVO.winTime = ownerVO.winTime;
                                ownerExtVO.joinNumber = ownerVO.joinNumber;
                                ownerExtVO.lssueId = lssueId;

                                callback.onSuccess(ownerExtVO);
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

    /**
     * 获取用户的夺宝记录
     *
     * @param userId
     * @param pageSize
     * @param pageNum
     * @param orderType
     * @return
     */
    public static AsyncJob<List<WinningInfo>> getPrizeHistory(long userId, int pageSize, int pageNum, int orderType) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getPrizeHistory");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("orderType", orderType + "");

        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<List<WinningInfo>>>() {
            @Override
            public AsyncJob<List<WinningInfo>> call(final String s) {
                return new AsyncJob<List<WinningInfo>>() {
                    @Override
                    public void start(ApiCallback<List<WinningInfo>> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONArray data = j.getJSONArray("data");
                                List<WinningInfo> winningInfos = convert2MyRecords(data);
                                callback.onSuccess(winningInfos);
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

    private static List<WinningInfo> convert2MyRecords(JSONArray data) throws JSONException {
        if (data == null || data.length() <= 0) {
            return new ArrayList<WinningInfo>();
        }

        List<WinningInfo> infoList = new ArrayList<WinningInfo>();
        for (int i = 0; i < data.length(); i++) {
            WinningInfo info = new WinningInfo();
            JSONObject wjson = data.getJSONObject(i);
            info.lssueId = wjson.getLong("ProductLssueID");
            info.id = info.lssueId;
            info.code = wjson.getString("ProductLssueCode");
            info.productId = wjson.getLong("ProID");
            info.totalCount = wjson.getInt("TotalCount");
            info.joinedCount = wjson.getInt("JonidedCount");
            info.title = wjson.getString("ProName");
            info.status = wjson.getInt("Status");
            info.productUrl = APIConstants.HOST + wjson.getString("Pictures");
            if (wjson.has("ClientCodeList")) {
                List<String> codeList = new ArrayList<String>();
                String aa = wjson.getString("ClientCodeList");
                if (!TextUtils.isEmpty(aa)) {

                    String[] jj = aa.split(",");
                    for (int j = 0; j < jj.length; j++) {
                        String clientCode = jj[j];
                        codeList.add(clientCode);
                    }
                }

                info.myPrizeCodes = codeList;
            }

            if (wjson.has("PriceTime")) {
                //加10秒，等待服务器计算
                info.lotteryTime = (wjson.getLong("PriceTime") + 10) * 1000;
            }

            if (wjson.has("winnerInfo")) {
                OwnerVO ownerVO = convert2OwnerVO(wjson);
                ownerVO.joinNumber = info.myPrizeCodes == null ? 0 : info.myPrizeCodes.size();
                info.ownerVO = ownerVO;
            }

            infoList.add(info);

        }
        return infoList;
    }

    private static List<WinningInfo> convert2FridayVO(JSONArray data) throws JSONException {
        if (data == null || data.length() <= 0) {
            return new ArrayList<WinningInfo>();
        }

        List<WinningInfo> infoList = new ArrayList<WinningInfo>();
        for (int i = 0; i < data.length(); i++) {
            WinningInfo info = new WinningInfo();
            JSONObject wjson = data.getJSONObject(i);
            info.lssueId = wjson.getLong("ProductLssueID");
            info.id = info.lssueId;
            info.code = wjson.getString("Code");
            info.productId = wjson.getLong("ProID");
            info.totalCount = wjson.getInt("TotalCount");
            info.joinedCount = wjson.getInt("JoinedCount");
            info.title = wjson.getString("ProName");
            info.status = wjson.getInt("Status");
            info.productUrl = APIConstants.HOST + wjson.getString("Pictures");
            if (wjson.has("ClientCodeList")) {
                List<String> codeList = new ArrayList<String>();
                String aa = wjson.getString("ClientCodeList");
                if (!TextUtils.isEmpty(aa)) {

                    String[] jj = aa.split(",");
                    for (int j = 0; j < jj.length; j++) {
                        String clientCode = jj[j];
                        codeList.add(clientCode);
                    }
                }

                info.myPrizeCodes = codeList;
            }

            if (wjson.has("PriceTime")) {
                //加10秒，等待服务器计算
                info.lotteryTime = (wjson.getLong("PriceTime") + 10) * 1000;
            }

            if (wjson.has("winnerInfo")) {
                OwnerVO ownerVO = convert2OwnerVO(wjson);
                ownerVO.joinNumber = info.myPrizeCodes == null ? 0 : info.myPrizeCodes.size();
                info.ownerVO = ownerVO;
            }

            infoList.add(info);

        }
        return infoList;
    }


    public static AsyncJob<List<ProductLssueWithWinnerVO>> fetchMyLuckRecords(long uid, int pageNum, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetLuckRecord");
        params.put("userId", uid + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");

        Type type = new TypeToken<List<ProductLssueWithWinnerVO>>() {
        }.getType();
        AsyncJob<List<ProductLssueWithWinnerVO>> a = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);
        return a;
    }

    public static AsyncJob<FundJsonVO> fetchFundData(int pageSize, int pageNum, long typeId, Long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetFoundsPageData");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("userId", (userId == null||userId ==0) ? "" : userId + "");
        String typeIdStr = "";
        if (typeId != 0) {
            typeIdStr = typeId + "";
        }
        params.put("typeId", typeIdStr);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, FundJsonVO.class);
    }

    public static AsyncJob<List<ProductLssueVO>> fetcchFundList(int typeID, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetFoundsList");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("typeID", typeID + "");
        Type type = new TypeToken<Map<String, List<ProductLssueVO>>>() {
        }.getType();

        AsyncJob<Map<String, List<ProductLssueVO>>> asyncJob = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, type);

        return asyncJob.map(new Func<Map<String, List<ProductLssueVO>>, List<ProductLssueVO>>() {
            @Override
            public List<ProductLssueVO> call(Map<String, List<ProductLssueVO>> stringListMap) {
                return stringListMap.get("ProductLssueList");
            }
        });
    }

    public static AsyncJob<List<WinningInfo>> fetchFridayProduct(int pageNo, int pageSize, Long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "GetFridayPageData");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        if (userId != null) {
            params.put("userId", userId + "");
        } else {
            params.put("userId", 0+"");
        }

        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<List<WinningInfo>>>() {
            @Override
            public AsyncJob<List<WinningInfo>> call(final String s) {
                return new AsyncJob<List<WinningInfo>>() {
                    @Override
                    public void start(ApiCallback<List<WinningInfo>> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONObject productData = j.getJSONObject("data");
                                JSONArray data = productData.getJSONArray("ProductLssueList");
                                List<WinningInfo> winningInfos = convert2FridayVO(data);
                                callback.onSuccess(winningInfos);
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
}
