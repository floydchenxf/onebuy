package com.floyd.onebuy.biz.manager;

import android.content.Context;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.biz.vo.json.PrizeShowListVO;
import com.floyd.onebuy.biz.vo.json.UserVO;

/**
 * Created by chenxiaofeng on 16/9/7.
 */
public class ShowShareManager {

    public static AsyncJob<PrizeShowListVO> fetchPrizeShowList(Context context, Long userId, int typeId, int pageSize, int pageNo) {

        AsyncJob<PrizeShowListVO> result = null;
        if (userId == 0l) {
            //首页晒单
            return result;
        }

        boolean isSelf = false;
        UserVO vo = LoginManager.getLoginInfo(context);
        if (vo != null) {
            Long myUserId = vo.ID;
            isSelf = myUserId.equals(userId);
        }

        if (isSelf) {
            result = ProductManager.getMyPrizeShow(userId, typeId, pageSize, pageNo);
        } else {
            result = ProductManager.getClientPrizeShow(userId, typeId, pageSize, pageNo);
        }

        return result;
    }
}
