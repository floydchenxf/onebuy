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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public class NormalProductCarService implements BuyCarService {

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
        params.put("pageType", "DelCar");
        String cards = carIds.toString().substring(1, carIds.toString().length() - 1);
        params.put("carId", cards);
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Boolean.class);
    }

    @Override
    public AsyncJob<CarListVO> fetchCarList(long userId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.CAR_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "CarList");
        params.put("userId", userId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, CarListVO.class);
    }
}
