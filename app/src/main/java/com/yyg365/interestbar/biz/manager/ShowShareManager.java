package com.yyg365.interestbar.biz.manager;

import android.content.Context;

import com.yyg365.interestbar.aync.AsyncJob;
import com.yyg365.interestbar.biz.vo.json.PrizeShowListVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;

/**
 * Created by chenxiaofeng on 16/9/7.
 */
public class ShowShareManager {

    /**
     * 获取晒单信息
     * @param context
     * @param objId 登陆用户/proId
     * @param typeId
     * @param pageSize
     * @param pageNo
     * @param isProduct 是否是商品
     * @return
     */
    public static AsyncJob<PrizeShowListVO> fetchPrizeShowList(Context context, Long objId, int typeId, int pageSize, int pageNo, boolean isProduct) {

        AsyncJob<PrizeShowListVO> result = null;
        if (isProduct || objId == 0l) {
            result = ProductManager.getPrizeShow(objId, typeId, pageSize, pageNo);
            return result;
        }

        boolean isSelf = false;
        UserVO vo = LoginManager.getLoginInfo(context);
        if (vo != null) {
            Long myUserId = vo.ID;
            isSelf = myUserId.equals(objId);
        }

        if (isSelf) {
            result = ProductManager.getMyPrizeShow(objId, typeId, pageSize, pageNo);
        } else {
            result = ProductManager.getClientPrizeShow(objId, typeId, pageSize, pageNo);
        }

        return result;
    }
}
