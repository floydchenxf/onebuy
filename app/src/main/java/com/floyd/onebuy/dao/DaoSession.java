package com.floyd.onebuy.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.floyd.onebuy.dao.Search;
import com.floyd.onebuy.dao.BuyCarNumber;

import com.floyd.onebuy.dao.SearchDao;
import com.floyd.onebuy.dao.BuyCarNumberDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig searchDaoConfig;
    private final DaoConfig buyCarNumberDaoConfig;

    private final SearchDao searchDao;
    private final BuyCarNumberDao buyCarNumberDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        searchDaoConfig = daoConfigMap.get(SearchDao.class).clone();
        searchDaoConfig.initIdentityScope(type);

        buyCarNumberDaoConfig = daoConfigMap.get(BuyCarNumberDao.class).clone();
        buyCarNumberDaoConfig.initIdentityScope(type);

        searchDao = new SearchDao(searchDaoConfig, this);
        buyCarNumberDao = new BuyCarNumberDao(buyCarNumberDaoConfig, this);

        registerDao(Search.class, searchDao);
        registerDao(BuyCarNumber.class, buyCarNumberDao);
    }
    
    public void clear() {
        searchDaoConfig.getIdentityScope().clear();
        buyCarNumberDaoConfig.getIdentityScope().clear();
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

    public BuyCarNumberDao getBuyCarNumberDao() {
        return buyCarNumberDao;
    }

}