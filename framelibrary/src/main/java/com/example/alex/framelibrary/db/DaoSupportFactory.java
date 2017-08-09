package com.example.alex.framelibrary.db;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

/**
 * Created by zhb on 2017/4/13.
 */

public class DaoSupportFactory {
    private static DaoSupportFactory mFactory;

    //持有外部数据库的引用
    private SQLiteDatabase mSqLiteDatabase;

    private DaoSupportFactory() {

        //把数据放到内存卡里面 判断是否内存卡 6.0动态权限
        File dbRoot = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath() + File.separator + "nhdz" + File.separator + "database");
        if (!dbRoot.exists()) {
            dbRoot.mkdirs();
        }
        File dbFile = new File(dbRoot, "nhdz.db");

        //打开或者创建一个数据库
        mSqLiteDatabase = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
    }

    public static DaoSupportFactory getFactory() {
        if (mFactory == null) {
            synchronized (DaoSupportFactory.class) {
                mFactory = new DaoSupportFactory();
            }
        }
        return mFactory;
    }

    public <T> IDaoSupport<T> getDao(Class<T> clazz) {
        IDaoSupport<T> daoSupport = new DaoSupport<>();
        daoSupport.init(mSqLiteDatabase, clazz);
        return daoSupport;
    }
}
