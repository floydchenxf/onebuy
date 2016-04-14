package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.vo.fee.FeeRecordVO;
import com.floyd.onebuy.biz.vo.fee.WinningRecordVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-14.
 */
public class FeeManager {

    public static AsyncJob<List<FeeRecordVO>> fetchFeeRecords(long userId, int page, int pageSize) {
        List<FeeRecordVO> feeRecordVOs = new ArrayList<FeeRecordVO>();
        for (int i = 0; i < 20; i++) {
            FeeRecordVO vo = new FeeRecordVO();
            vo.fee = i + 1 + "";
            vo.orderNo = "CN000" + i * 100;
            vo.source = "微信";
            vo.time = System.currentTimeMillis();
            feeRecordVOs.add(vo);
        }

        return JobFactory.createJob(feeRecordVOs);
    }

    public static AsyncJob<List<WinningRecordVO>> fetchWinningRecords(long userId, int status, int page, int pageSize) {
        List<WinningRecordVO> winningRecordVOs = new ArrayList<WinningRecordVO>();
        for (int i = 0; i < 20; i++) {
            WinningRecordVO vo = new WinningRecordVO();
            vo.productImage = "http://qmmt2015.b0.upaiyun.com/2016/4/12/70242b33-34df-4db5-a334-46000335e8f4.png";
            vo.leftTips=i+1;
            vo.currentTips = 10;
            vo.needTips = 1000;
            vo.productTitle = "小米手机５｜｜精彩开奖就送苹果";
            vo.status=1;
            winningRecordVOs.add(vo);
        }
        return JobFactory.createJob(winningRecordVOs);
    }
}
