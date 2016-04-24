package com.floyd.onebuy.biz.manager;

import android.content.Context;

import com.floyd.onebuy.dao.DaoSession;
import com.floyd.onebuy.dao.Search;
import com.floyd.onebuy.dao.SearchDao;

import java.util.List;

/**
 * Created by floyd on 16-4-23.
 */
public class SearchManager {

    public static void addSearchRecord(Context context, String searchContent) {
        DaoSession daoSession = DBManager.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        boolean isExists = isExists(context, searchContent);
        if (isExists) {
            return;
        }
        Search search = new Search(null, searchContent);
        searchDao.insertWithoutSettingPk(search);
    }

    public static boolean isExists(Context context, String content) {
        DaoSession daoSession = DBManager.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        List<Search> existsList = searchDao.queryBuilder().where(SearchDao.Properties.Content.eq(content)).list();
        return existsList!=null && !existsList.isEmpty();
    }

    public static void deleteSearchRecords(Context context) {
        DaoSession daoSession = DBManager.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        searchDao.deleteAll();
    }

    public static List<Search> queryAllSearchRecords(Context context){
        DaoSession daoSession = DBManager.getDaoSession(context);
        SearchDao searchDao = daoSession.getSearchDao();
        return searchDao.loadAll();
    }
}
