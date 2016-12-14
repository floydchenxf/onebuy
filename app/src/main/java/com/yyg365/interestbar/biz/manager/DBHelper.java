package com.yyg365.interestbar.biz.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.yyg365.interestbar.dao.DaoMaster;
import com.yyg365.interestbar.dao.DaoSession;

/**
 * Created by floyd on 16-4-23.
 */
public class DBHelper {

    private static DaoMaster daoMaster;
    private static DaoSession daoSession;
    public static SQLiteDatabase db;
    //数据库名，表名是自动被创建的
    public static final String DB_NAME = "search.db";


    public static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME, null);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }


    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }
}
