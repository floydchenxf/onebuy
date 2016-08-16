package com.floyd.onebuy.biz.manager.buycar.db;

import android.content.Context;

import com.floyd.onebuy.biz.manager.DBHelper;
import com.floyd.onebuy.dao.BuyCarNumber;
import com.floyd.onebuy.dao.DaoSession;
import com.floyd.onebuy.dao.FridayBuyCarNumber;
import com.floyd.onebuy.dao.FridayBuyCarNumberDao;
import com.floyd.onebuy.dao.FundBuyCarNumber;
import com.floyd.onebuy.dao.FundBuyCarNumberDao;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by chenxiaofeng on 16/8/17.
 */
public class FundBuyCarDBServer implements BuyCarDBServer{
    @Override
    public void deleteBuyCarNumber(Context context, long userId, long productLssueId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FundBuyCarNumberDao fundBuyCarNumberDao = daoSession.getFundBuyCarNumberDao();
        DeleteQuery deleteQuery = fundBuyCarNumberDao.queryBuilder().where(FundBuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), FundBuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void deleteBuyCarNumber(Context context, long userId, List<Long> productLssueIds) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FundBuyCarNumberDao fundBuyCarNumberDao = daoSession.getFundBuyCarNumberDao();
        DeleteQuery query = fundBuyCarNumberDao.queryBuilder().where(FundBuyCarNumberDao.Properties.ProductLssueId.in(productLssueIds), FundBuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void updateBuyCarNumber(Context context, long userId, long productLssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FundBuyCarNumberDao fundBuyCarNumberDao = daoSession.getFundBuyCarNumberDao();
        List<FundBuyCarNumber> numbers = fundBuyCarNumberDao.queryBuilder().where(FundBuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), FundBuyCarNumberDao.Properties.UserId.eq(userId)).list();
        if (numbers != null && !numbers.isEmpty()) {
            for (FundBuyCarNumber n:numbers) {
                n.setBuyNumber(number);
                fundBuyCarNumberDao.insertOrReplace(n);
            }
        } else {
            addBuyCarNumber(context, userId, productLssueId, number);
        }
    }

    @Override
    public void addBuyCarNumber(Context context, long userId, long lssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FundBuyCarNumberDao fundBuyCarNumberDao = daoSession.getFundBuyCarNumberDao();
        FundBuyCarNumber numberVO = new FundBuyCarNumber();
        numberVO.setUserId(userId);
        numberVO.setProductLssueId(lssueId);
        numberVO.setBuyNumber(number);
        fundBuyCarNumberDao.insertWithoutSettingPk(numberVO);
    }

    @Override
    public  List<FundBuyCarNumber> queryAllBuyNumbers(Context context, long userId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FundBuyCarNumberDao fundBuyCarNumberDao = daoSession.getFundBuyCarNumberDao();
        return fundBuyCarNumberDao.queryBuilder().where(FundBuyCarNumberDao.Properties.UserId.eq(userId)).list();
    }
}
