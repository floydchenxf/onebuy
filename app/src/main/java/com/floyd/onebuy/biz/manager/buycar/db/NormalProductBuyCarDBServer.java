package com.floyd.onebuy.biz.manager.buycar.db;

import android.content.Context;

import com.floyd.onebuy.biz.manager.DBHelper;
import com.floyd.onebuy.dao.BuyCarNumber;
import com.floyd.onebuy.dao.BuyCarNumberDao;
import com.floyd.onebuy.dao.DaoSession;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public class NormalProductBuyCarDBServer implements BuyCarDBServer {

    @Override
    public void deleteBuyCarNumber(Context context, long userId, long productLssueId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        DeleteQuery deleteQuery = buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), BuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void deleteBuyCarNumber(Context context, long userId, List<Long> productLssueIds) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        DeleteQuery query = buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.ProductLssueId.in(productLssueIds), BuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void updateBuyCarNumber(Context context, long userId, long productLssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        List<BuyCarNumber> numbers = buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), BuyCarNumberDao.Properties.UserId.eq(userId)).list();
        if (numbers != null && !numbers.isEmpty()) {
            for (BuyCarNumber n:numbers) {
                n.setBuyNumber(number);
                buyCarNumberDao.insertOrReplace(n);
            }
        } else {
            addBuyCarNumber(context, userId, productLssueId, number);
        }
    }

    @Override
    public void addBuyCarNumber(Context context, long userId, long lssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        BuyCarNumber numberVO = new BuyCarNumber();
        numberVO.setUserId(userId);
        numberVO.setProductLssueId(lssueId);
        numberVO.setBuyNumber(number);
        buyCarNumberDao.insertWithoutSettingPk(numberVO);
    }

    @Override
    public List<BuyCarNumber> queryAllBuyNumbers(Context context, long userId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        return buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.UserId.eq(userId)).list();
    }
}
