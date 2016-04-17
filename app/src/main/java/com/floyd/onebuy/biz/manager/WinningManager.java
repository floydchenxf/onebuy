package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.winning.JoinVO;
import com.floyd.onebuy.biz.vo.winning.ProgressVO;
import com.floyd.onebuy.biz.vo.winning.WinningDetailInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-17.
 */
public class WinningManager {

    public static AsyncJob<WinningDetailInfo> fetchWinningDetailInfoById(long id) {
        WinningDetailInfo info = new WinningDetailInfo();
        List<AdvVO> advList = new ArrayList<AdvVO>();
        AdvVO advVO1 = new AdvVO();
        advVO1.id=1;
        advVO1.createTime=System.currentTimeMillis();
        advVO1.imgUrl = "http://qmmt2015.b0.upaiyun.com/2016/1/6/4eadb1c6-55fd-41f7-a4d4-29a0667baeab.jpg";
        advVO1.title = "";
        advVO1.type=1;
        advVO1.updateTime = System.currentTimeMillis();
        advList.add(advVO1);

        AdvVO advVO2 = new AdvVO();
        advVO2.id=2;
        advVO2.createTime = System.currentTimeMillis();
        advVO2.updateTime = System.currentTimeMillis();
        advVO2.imgUrl = "http://qmmt2015.b0.upaiyun.com/2016/1/6/73c72456-e121-4513-b8c6-8495776b2f22.jpg";
        advVO2.type = 1;
        advList.add(advVO2);

        info.advVOList = advList;
        info.status = 1;
        info.myJoinedRecords = null;
        List<JoinVO> total = new ArrayList<JoinVO>();
        JoinVO joinVO1 = new JoinVO();
        joinVO1.headImage = "http://qmmt2015.b0.upaiyun.com/2016/2/16/b027dc63-b99b-4d17-8b41-1e6a093ae0e6.png";
        joinVO1.joinNumber = 22;
        joinVO1.userId = 1;
        joinVO1.userName = "郑美梁智";
        total.add(joinVO1);

        JoinVO joinVO2 = new JoinVO();
        joinVO2.headImage = "http://qmmt2015.b0.upaiyun.com/2016/4/2/b6d8e880-bd85-4295-9e17-ae07edb8bce6.png";
        joinVO2.joinNumber = 22;
        joinVO2.userId = 2;
        joinVO2.userName = "安娜";
        total.add(joinVO2);

        for(int i=0; i<20; i++) {
            JoinVO joinVO3 = new JoinVO();
            joinVO3.headImage = "http://qmmt2015.b0.upaiyun.com/2016/4/2/b6d8e880-bd85-4295-9e17-ae07edb8bce6.png";
            joinVO3.joinNumber = 22;
            joinVO3.userId = 2;
            joinVO3.userName = "安娜";
            total.add(joinVO3);
        }
        info.allJoinedRecords = total;

        ProgressVO progressVO = new ProgressVO();
        progressVO.left = 78;
        progressVO.total = 1909;
        progressVO.id=1;
        info.progressVO = progressVO;
        return JobFactory.createJob(info);

    }
}
