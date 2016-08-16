package com.floyd.onebuy.biz.manager.buycar.server;

import android.content.Context;
import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.DBManager;
import com.floyd.onebuy.biz.manager.JsonHttpJobFactory;
import com.floyd.onebuy.biz.vo.json.CarItemVO;
import com.floyd.onebuy.biz.vo.json.CarListVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.floyd.onebuy.dao.BuyCarNumber;
import com.floyd.onebuy.dao.FridayBuyCarNumber;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public class FridayCarService implements BuyCarService {

    @Override
    public AsyncJob<Boolean> addCar(long productLssueId, long userId, int number) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "AddCar");
        params.put("userId", userId + "");
        params.put("productLssueID", productLssueId + "");
        params.put("number", number + "");
        AsyncJob<Map<String, String>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Map.class);
        return result.map(new Func<Map<String, String>, Boolean>() {
            @Override
            public Boolean call(Map<String, String> map) {
                String i = map.get("number");
                if (TextUtils.isDigitsOnly(i)) {
                    return Boolean.TRUE;
                }
                return Boolean.FALSE;
            }
        });
    }

    @Override
    public AsyncJob<Boolean> delCar(Collection<Long> carIds) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "DelFCar");
        String cards = carIds.toString().substring(1,carIds.toString().length()-1);
        params.put("carId", cards);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    @Override
    public AsyncJob<CarListVO> fetchCarList(long userId, int pageNo, int pageSize) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "FCarList");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNo + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CarListVO.class);
    }

    @Override
    public AsyncJob<List<WinningInfo>> fetchBuyCarList(final Context context, final long userId, int pageNo, int pageSize) {
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
                        List<FridayBuyCarNumber> buyCarNumbers = DBManager.queryAllBuyNumbers(BuyCarType.FRI, context, userId);
                        boolean isDbEmpty = false;
                        if (buyCarNumbers == null || buyCarNumbers.isEmpty()) {
                            isDbEmpty = true;
                        } else {
                            isDbEmpty = false;
                        }

                        if (!isDbEmpty) {
                            for (FridayBuyCarNumber carNumber : buyCarNumbers) {
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
                            DBManager.deleteBuyCarNumber(BuyCarType.FRI, context, userId, deleteList);
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
