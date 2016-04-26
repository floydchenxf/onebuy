package com.floyd.onebuy.biz.manager;

import android.util.Log;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.vo.IndexVO;
import com.floyd.onebuy.biz.vo.mote.MoteInfoVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.channel.request.HttpMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 15-12-3.
 */
public class IndexManager {

    /**
     * 获取首页模特信息
     *
     * @param moteType 1女模 2男模 3童摸
     * @param pageNo
     * @param pageSize
     * @return
     */
    public static AsyncJob<List<MoteInfoVO>> fetchMoteList(int moteType, int pageNo, int pageSize) {
        String url = APIConstants.DIAMOND_HOST + APIConstants.API_GET_MOTE_LIST;
        Log.e("TAG", moteType + "," + pageNo + "," + pageSize);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("moteType", moteType + "");
        params.put("pageNo", pageNo + "");
        params.put("pageSize", pageSize + "");

        AsyncJob<Motes> jj = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, Motes.class);
        return jj.map(new Func<Motes, List<MoteInfoVO>>() {
            @Override
            public List<MoteInfoVO> call(Motes motes) {
                return motes.moteVOs;
            }
        });
    }

    class Motes {
        public List<MoteInfoVO> moteVOs;
    }

    public static AsyncJob<IndexVO> getIndexInfoJob() {
        String url = APIConstants.DIAMOND_HOST + APIConstants.API_INDEX_INFO;
        return JsonHttpJobFactory.getJsonAsyncJob(url, null, HttpMethod.POST, IndexVO.class);

    }


    /**
     * 获取参加抽奖产品
     *
     * @param page
     * @param pageSize
     * @return
     */
    public static AsyncJob<List<WinningInfo>> fetchWinningInfos(int page, int pageSize) {
        List<WinningInfo> result = new ArrayList<WinningInfo>();
        WinningInfo info1 = new WinningInfo();
        info1.id = 1l;
        info1.processPrecent="30%";
        info1.title="【第888期】小米手机";
        info1.productUrl="http://qmmt2015.b0.upaiyun.com/2016/3/30/7f1777fa-6ac6-4476-a7f9-39c9c8967016.png";

        WinningInfo info2 = new WinningInfo();
        info2.id = 2l;
        info2.processPrecent="40%";
        info2.title="【第888期】红米手机";
        info2.productUrl="http://qmmt2015.b0.upaiyun.com/2016/1/19/21c558ce-e7c3-4947-9a68-5c2373568fc3.jpeg";

        WinningInfo info3 = new WinningInfo();
        info3.id = 3l;
        info3.processPrecent="50%";
        info3.title="【第888期】小米手机";
        info3.productUrl="http://qmmt2015.b0.upaiyun.com/2016/4/2/d2af5365-f9e0-4578-8f5a-265db6d77094.jpg";

        WinningInfo info4 = new WinningInfo();
        info1.id = 4l;
        info1.processPrecent="60%";
        info1.title="【第888期】小米手机";
        info1.productUrl="http://qmmt2015.b0.upaiyun.com/2016/4/2/d8a49489-9193-47a3-b8ae-b6fc0dcd9542.png";

        result.add(info1);
        result.add(info2);
        result.add(info3);
        result.add(info4);
        return JobFactory.createJob(result);
    }

}

