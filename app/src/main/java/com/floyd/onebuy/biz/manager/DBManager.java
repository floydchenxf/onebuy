package com.floyd.onebuy.biz.manager;

import android.content.Context;

import com.floyd.onebuy.dao.BuyCarNumber;
import com.floyd.onebuy.dao.BuyCarNumberDao;
import com.floyd.onebuy.dao.DaoSession;
import com.floyd.onebuy.dao.Search;
import com.floyd.onebuy.dao.SearchDao;

import java.util.List;

/**
 * Created by floyd on 16-4-23.
 */
public class DBManager {

    public static void addSearchRecord(Context context, String searchContent) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        boolean isExists = isExists(context, searchContent);
        if (isExists) {
            return;
        }
        Search search = new Search(null, searchContent);
        searchDao.insertWithoutSettingPk(search);
    }

    public static boolean isExists(Context context, String content) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        List<Search> existsList = searchDao.queryBuilder().where(SearchDao.Properties.Content.eq(content)).list();
        return existsList!=null && !existsList.isEmpty();
    }

    public static void deleteSearchRecords(Context context) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        searchDao.deleteAll();
    }

    public static List<Search> queryAllSearchRecords(Context context){
        DaoSession daoSession = DBHelper.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        return searchDao.loadAll();
    }

    public static void updateBuyCarNumber(Context context, long userId, long productLssueId, int number) {
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

    public static void addBuyCarNumber(Context context, long userId, long lssueId, int number) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        BuyCarNumber numberVO = new BuyCarNumber();
        numberVO.setUserId(userId);
        numberVO.setProductLssueId(lssueId);
        numberVO.setBuyNumber(number);
        buyCarNumberDao.insertWithoutSettingPk(numberVO);
    }

    public static void deleteBuyCarNumber(Context context, long userId, long productLssueId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.ProductLssueId.eq(productLssueId), BuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
    }


    public static void deleteBuyCarNumber(Context context, long userId, List<Long> productLssueIds) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.ProductLssueId.in(productLssueIds), BuyCarNumberDao.Properties.UserId.eq(userId)).buildDelete();
    }

    public static List<BuyCarNumber> queryAllBuyNumbers(Context context, long userId) {
        DaoSession daoSession = DBHelper.getDaoSession(context);
        BuyCarNumberDao buyCarNumberDao = daoSession.getBuyCarNumberDao();
        return buyCarNumberDao.queryBuilder().where(BuyCarNumberDao.Properties.UserId.eq(userId)).list();
    }
}
