package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.product.NewsVO;
import com.floyd.onebuy.biz.vo.product.ProductTypeVO;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

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
     * 获取分类列表
     *
     * @return List<ProductTypeVO>
     */
    public AsyncJob<List<ProductTypeVO>> fetchProductType() {
        String url = APIConstants.HOST + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getProductTypeList");
        Type clazz = new TypeToken<ArrayList<ProductTypeVO>>() {
        }.getType();
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, clazz);
    }


    /**
     * 获取广告
     *
     * @return
     */
    public AsyncJob<List<AdvVO>> fetchAdvList() {
        String url = APIConstants.HOST + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getNewsList");
        Type clazz = new TypeToken<ArrayList<NewsVO>>() {
        }.getType();
        AsyncJob<List<NewsVO>> t = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, clazz);
        return t.map(new Func<List<NewsVO>, List<AdvVO>>() {
            @Override
            public List<AdvVO> call(List<NewsVO> newsVOs) {
                ArrayList<AdvVO> result = new ArrayList<AdvVO>();
                if (newsVOs != null && !newsVOs.isEmpty()) {
                    for (NewsVO vo : newsVOs) {
                        AdvVO advVO = new AdvVO();
                        advVO.type = 1;
                        advVO.imgUrl = vo.SmallPic;
                        advVO.title = vo.Url;
                        advVO.id = Long.parseLong(vo.NewsID);
                        result.add(advVO);
                    }
                }
                return result;
            }
        });

    }




}
