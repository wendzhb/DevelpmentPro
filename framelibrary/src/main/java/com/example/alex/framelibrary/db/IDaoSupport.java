package com.example.alex.framelibrary.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by zhb on 2017/4/13.
 */

public interface IDaoSupport<T> {

    void init(SQLiteDatabase sqLiteDatabase, Class<T> clazz);

    //插入
    long insert(T t);

    //批量插入 检测性能
    void insert(List<T> datas);

    /**
     * 获取查询支持
     *
     * @return
     */
    QuerySupport querySupport();

    /**
     * 更新
     *
     * @param obj
     * @param whereClause
     * @param whereArgs
     * @return
     */
    long update(T obj, String whereClause, String... whereArgs);

    /**
     * 删除
     *
     * @param whereClause
     * @param whereArgs
     * @return
     */
    long delete(String whereClause, String... whereArgs);
}
