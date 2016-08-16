package com.floyd.onebuy.biz.manager;

import android.content.Context;

import com.floyd.onebuy.biz.constants.BuyCarType;
import com.floyd.onebuy.biz.manager.buycar.db.BuyCarDBServer;
import com.floyd.onebuy.biz.manager.buycar.db.FridayBuyCarDBServer;
import com.floyd.onebuy.biz.manager.buycar.db.FundBuyCarDBServer;
import com.floyd.onebuy.biz.manager.buycar.db.NormalProductBuyCarDBServer;
import com.floyd.onebuy.dao.BuyCarNumber;
import com.floyd.onebuy.dao.BuyCarNumberDao;
import com.floyd.onebuy.dao.DaoSession;
import com.floyd.onebuy.dao.Search;
import com.floyd.onebuy.dao.SearchDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.greenrobot.dao.query.DeleteQuery;

/**
 * Created by floyd on 16-4-23.
 */
public class DBManager {

    private static Map<BuyCarType, BuyCarDBServer> buycarDBServers = new HashMap<BuyCarType, BuyCarDBServer>();

    static {
        buycarDBServers.put(BuyCarType.NORMAL, new NormalProductBuyCarDBServer());
        buycarDBServers.put(BuyCarType.FRI, new FridayBuyCarDBServer());
        buycarDBServers.put(BuyCarType.FUND, new FundBuyCarDBServer());
    }

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

    public static void updateBuyCarNumber(BuyCarType type, Context context, long userId, long productLssueId, int number) {
        buycarDBServers.get(type).updateBuyCarNumber(context, userId, productLssueId, number);
    }

    public static void addBuyCarNumber(BuyCarType type, Context context, long userId, long lssueId, int number) {
        buycarDBServers.get(type).addBuyCarNumber(context, userId, lssueId, number);
    }

    public static void deleteBuyCarNumber(BuyCarType type, Context context, long userId, long productLssueId) {
        buycarDBServers.get(type).deleteBuyCarNumber(context, userId, productLssueId);
    }


    public static void deleteBuyCarNumber(BuyCarType type, Context context, long userId, List<Long> productLssueIds) {
        buycarDBServers.get(type).deleteBuyCarNumber(context, userId, productLssueIds);
    }

    public static <T> List<T> queryAllBuyNumbers(BuyCarType type, Context context, long userId) {
        return  buycarDBServers.get(type).queryAllBuyNumbers(context, userId);
    }
}
