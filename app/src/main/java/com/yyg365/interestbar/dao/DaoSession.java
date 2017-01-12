package com.yyg365.interestbar.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.Map;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.AbstractDaoSession;
import de.greenrobot.dao.identityscope.IdentityScopeType;
import de.greenrobot.dao.internal.DaoConfig;

import com.yyg365.interestbar.dao.BuyCarNumber;
import com.yyg365.interestbar.dao.Search;
import com.yyg365.interestbar.dao.FridayBuyCarNumber;
import com.yyg365.interestbar.dao.FundBuyCarNumber;

import com.yyg365.interestbar.dao.BuyCarNumberDao;
import com.yyg365.interestbar.dao.SearchDao;
import com.yyg365.interestbar.dao.FridayBuyCarNumberDao;
import com.yyg365.interestbar.dao.FundBuyCarNumberDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig buyCarNumberDaoConfig;
    private final DaoConfig searchDaoConfig;
    private final DaoConfig fridayBuyCarNumberDaoConfig;
    private final DaoConfig fundBuyCarNumberDaoConfig;
    private final DaoConfig jfSearchDaoConfig;

    private final BuyCarNumberDao buyCarNumberDao;
    private final SearchDao searchDao;
    private final JFSearchDao jfSearchDao;
    private final FridayBuyCarNumberDao fridayBuyCarNumberDao;
    private final FundBuyCarNumberDao fundBuyCarNumberDao;

    public DaoSession(SQLiteDatabase db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        buyCarNumberDaoConfig = daoConfigMap.get(BuyCarNumberDao.class).clone();
        buyCarNumberDaoConfig.initIdentityScope(type);

        searchDaoConfig = daoConfigMap.get(SearchDao.class).clone();
        searchDaoConfig.initIdentityScope(type);

        jfSearchDaoConfig = daoConfigMap.get(JFSearchDao.class).clone();
        jfSearchDaoConfig.initIdentityScope(type);

        fridayBuyCarNumberDaoConfig = daoConfigMap.get(FridayBuyCarNumberDao.class).clone();
        fridayBuyCarNumberDaoConfig.initIdentityScope(type);

        fundBuyCarNumberDaoConfig = daoConfigMap.get(FundBuyCarNumberDao.class).clone();
        fundBuyCarNumberDaoConfig.initIdentityScope(type);

        buyCarNumberDao = new BuyCarNumberDao(buyCarNumberDaoConfig, this);
        searchDao = new SearchDao(searchDaoConfig, this);
        jfSearchDao = new JFSearchDao(jfSearchDaoConfig, this);
        fridayBuyCarNumberDao = new FridayBuyCarNumberDao(fridayBuyCarNumberDaoConfig, this);
        fundBuyCarNumberDao = new FundBuyCarNumberDao(fundBuyCarNumberDaoConfig, this);

        registerDao(BuyCarNumber.class, buyCarNumberDao);
        registerDao(Search.class, searchDao);
        registerDao(JFSearch.class, jfSearchDao);
        registerDao(FridayBuyCarNumber.class, fridayBuyCarNumberDao);
        registerDao(FundBuyCarNumber.class, fundBuyCarNumberDao);
    }
    
    public void clear() {
        buyCarNumberDaoConfig.getIdentityScope().clear();
        searchDaoConfig.getIdentityScope().clear();
        jfSearchDaoConfig.getIdentityScope().clear();
        fridayBuyCarNumberDaoConfig.getIdentityScope().clear();
        fundBuyCarNumberDaoConfig.getIdentityScope().clear();
    }

    public BuyCarNumberDao getBuyCarNumberDao() {
        return buyCarNumberDao;
    }

    public SearchDao getSearchDao() {
        return searchDao;
    }

    public JFSearchDao getJfSearchDao() {
        return jfSearchDao;
    }

    public FridayBuyCarNumberDao getFridayBuyCarNumberDao() {
        return fridayBuyCarNumberDao;
    }

    public FundBuyCarNumberDao getFundBuyCarNumberDao() {
        return fundBuyCarNumberDao;
    }

}
