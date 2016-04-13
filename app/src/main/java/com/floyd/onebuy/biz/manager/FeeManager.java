package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.vo.fee.FeeRecordVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-14.
 */
public class FeeManager {

    public static AsyncJob<List<FeeRecordVO>> fetchFeeRecords(long userId, int page, int pageSize) {
        List<FeeRecordVO> feeRecordVOs = new ArrayList<FeeRecordVO>();
        for(int i=0; i < 20; i++) {
            FeeRecordVO vo = new FeeRecordVO();
            vo.fee = i+1+"";
            vo.orderNo = "CN000" + i*100;
            vo.source = "微信";
            vo.time = System.currentTimeMillis();
//            feeRecordVOs.add(vo);
        }

        return JobFactory.createJob(feeRecordVOs);
    }
}
