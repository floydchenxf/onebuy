package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.json.IndexAdvVO;
import com.floyd.onebuy.biz.vo.json.IndexVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueItemVO;
import com.floyd.onebuy.biz.vo.json.ProductLssueVO;
import com.floyd.onebuy.biz.vo.model.NewIndexVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
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
     * 获取首页数据
     *
     * @return
     */
    public static AsyncJob<NewIndexVO> fetchIndexData() {
        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "IndexData");
        AsyncJob<IndexVO> job = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, IndexVO.class);
        return job.map(new Func<IndexVO, NewIndexVO>() {
            @Override
            public NewIndexVO call(IndexVO indexVO) {
                if (indexVO == null) {
                    return null;
                }
                NewIndexVO vo = new NewIndexVO();
                vo.typeList = indexVO.typeList;
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
                        info.issueId = v.ProductLssueID;
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
     * 分页获取期数
     *
     * @param pageSize
     * @param pageNum
     * @param typeId
     * @param sort
     * @return
     */
    public static AsyncJob<List<WinningInfo>> fetchProductLssueVOs(int pageSize, int pageNum, int typeId, int sort) {

        String url = APIConstants.HOST_API_PATH + APIConstants.PRODUCT_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "ProductLssueTotal");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        params.put("typeID", typeId + "");
        params.put("sort", sort + "");
        Type type = new TypeToken<ArrayList<ProductLssueItemVO>>() {
        }.getType();
        AsyncJob<ProductLssueVO> s =  JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, ProductLssueVO.class);

        return s.map(new Func<ProductLssueVO, List<WinningInfo>>() {
            @Override
            public List<WinningInfo> call(ProductLssueVO productLssueVO) {
                List<WinningInfo> result = new ArrayList<WinningInfo>();
                if (productLssueVO == null) {
                    return result;
                }

                List<ProductLssueItemVO> productLssueVOs = productLssueVO.ProductLssueList;
                if (productLssueVOs == null || productLssueVOs.isEmpty()) {
                    return result;
                }

                for (ProductLssueItemVO vo:productLssueVOs) {
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
}
