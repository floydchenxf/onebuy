package com.floyd.onebuy.biz.manager.buycar.db;

import android.content.Context;

import com.floyd.onebuy.biz.manager.DBHelper;
import com.floyd.onebuy.dao.DaoSession;
import com.floyd.onebuy.dao.FridayBuyCarNumber;
import com.floyd.onebuy.dao.FridayBuyCarNumberDao;

import java.util.List;

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by chenxiaofeng on 16/8/16.
 */
public class FridayBuyCarDBServer implements BuyCarDBServer {
    @Override
    public void deleteBuyCarNumber(Context context, long userId, long productLssueId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FridayBuyCarNumberDao fridayBuyCarNumberDao = daoSession.getFridayBuyCarNumberDao();
        DeleteQuery deleteQuery = fridayBuyCarNumberDao.queryBuilder().where(FridayBuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), FridayBuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void deleteBuyCarNumber(Context context, long userId, List<Long> productLssueIds) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FridayBuyCarNumberDao fridayBuyCarNumberDao = daoSession.getFridayBuyCarNumberDao();
        DeleteQuery query = fridayBuyCarNumberDao.queryBuilder().where(FridayBuyCarNumberDao.Properties.ProductLssueId.in(productLssueIds), FridayBuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
        query.executeDeleteWithoutDetachingEntities();
    }

    @Override
    public void updateBuyCarNumber(Context context, long userId, long productLssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FridayBuyCarNumberDao fridayBuyCarNumberDao = daoSession.getFridayBuyCarNumberDao();
        List<FridayBuyCarNumber> numbers = fridayBuyCarNumberDao.queryBuilder().where(FridayBuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), FridayBuyCarNumberDao.Properties.UserId.eq(userId)).list();
        if (numbers != null && !numbers.isEmpty()) {
            for (FridayBuyCarNumber n:numbers) {
                n.setBuyNumber(number);
                fridayBuyCarNumberDao.insertOrReplace(n);
            }
        } else {
            addBuyCarNumber(context, userId, productLssueId, number);
        }
    }

    @Override
    public void addBuyCarNumber(Context context, long userId, long lssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FridayBuyCarNumberDao fridayBuyCarNumberDao = daoSession.getFridayBuyCarNumberDao();
        FridayBuyCarNumber numberVO = new FridayBuyCarNumber();
        numberVO.setUserId(userId);
        numberVO.setProductLssueId(lssueId);
        numberVO.setBuyNumber(number);
        fridayBuyCarNumberDao.insertWithoutSettingPk(numberVO);
    }

    @Override
    public List<FridayBuyCarNumber> queryAllBuyNumbers(Context context, long userId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        FridayBuyCarNumberDao fridayBuyCarNumberDao = daoSession.getFridayBuyCarNumberDao();
        return fridayBuyCarNumberDao.queryBuilder().where(FridayBuyCarNumberDao.Properties.UserId.eq(userId)).list();
    }
}
