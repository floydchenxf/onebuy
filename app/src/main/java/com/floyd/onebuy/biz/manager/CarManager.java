package com.floyd.onebuy.biz.manager;

import android.content.Context;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.biz.vo.json.CarItemVO;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.floyd.onebuy.dao.BuyCarNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-30.
 */
public class CarManager {

    /**
     * 添加到购物车
     *
     * @param productLssueId 期数id
     * @param userId         当前用户
     * @return
     */
    public static AsyncJob<Boolean> addCar(long productLssueId, long userId, int number) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "AddCar");
        params.put("userId", userId + "");
        params.put("productLssueID", productLssueId + "");
        params.put("number", number+"");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    /**
     * 删除购物车元素
     *
     * @param carIds
     * @return
     */
    public static AsyncJob<Boolean> delCar(Collection<Long> carIds) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "DelCar");
        String cards = carIds.toString().substring(1,carIds.toString().length()-1);
        params.put("carId", cards);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    /**
     * 获取购物车列表
     *
     * @return
     */
    public static AsyncJob<CarListVO> fetchCarList(long userId, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CarList");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CarListVO.class);
    }

    public static AsyncJob<List<WinningInfo>> fetchBuyCarList(final Context context, final long userId, int pageNo, int pageSize) {
        AsyncJob<List<WinningInfo>> result = fetchCarList(userId, pageNo, pageSize).flatMap(new Func<CarListVO, AsyncJob<List<WinningInfo>>>() {
            @Override
            public AsyncJob<List<WinningInfo>> call(final CarListVO carListVO) {
                return new AsyncJob<List<WinningInfo>>() {
                    @Override
                    public void start(ApiCallback<List<WinningInfo>> callback) {
                        if (carListVO == null) {
                            callback.onError(APIError.API_CONTENT_EMPTY, "content is empty!");
                            return;
                        }

                        List<CarItemVO> items = carListVO.list;
                        if (items == null) {
                            callback.onError(APIError.API_CONTENT_EMPTY, "content is empty!");
                            return;
                        }

                        Map<String, Integer> carNumberMap = new HashMap<String, Integer>();
                        List<BuyCarNumber> buyCarNumbers = DBManager.queryAllBuyNumbers(context, userId);
                        boolean isDbEmpty = false;
                        if (buyCarNumbers == null || buyCarNumbers.isEmpty()) {
                            isDbEmpty = true;
                        } else {
                            isDbEmpty = false;
                        }

                        if (!isDbEmpty) {
                            for (BuyCarNumber carNumber : buyCarNumbers) {
                                carNumberMap.put(carNumber.getUserId() + "-" + carNumber.getProductLssueId(), carNumber.getBuyNumber());
                            }
                        }

                        List<WinningInfo> result = new ArrayList<WinningInfo>();
                        List<Long> deleteList = new ArrayList<Long>();
                        for (CarItemVO vo : items) {
                            WinningInfo info = convert2Info(vo);
                            long productLssueId = vo.ProductLssueID;
                            String k = userId + "-" + productLssueId;
                            Integer num = carNumberMap.get(k);
                            if (num != null) {
                                info.buyCount = num;
                            } else if (!isDbEmpty && num == null) {
                                info.buyCount = 1;
                                deleteList.add(productLssueId);
                            } else {
                                info.buyCount = 1;
                            }

                            result.add(info);
                        }

                        if (!deleteList.isEmpty()) {
                            DBManager.deleteBuyCarNumber(context, userId, deleteList);
                        }
                        callback.onSuccess(result);
                    }
                };
            }
        });
        return result;
    }

    private static WinningInfo convert2Info(CarItemVO vo) {
        WinningInfo info = new WinningInfo();
        info.totalCount = vo.TotalCount;
        info.joinedCount = vo.JoinedCount;
        info.status = vo.status;
        info.id = vo.carID;
        info.productUrl = APIConstants.HOST + vo.Pictures;
        info.title = vo.ProName;
        info.lssueId = vo.ProductLssueID;
        return info;
    }

}
