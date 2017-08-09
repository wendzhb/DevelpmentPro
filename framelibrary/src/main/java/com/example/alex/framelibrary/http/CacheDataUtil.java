package com.example.alex.framelibrary.http;

import com.example.alex.framelibrary.db.DaoSupportFactory;
import com.example.alex.framelibrary.db.IDaoSupport;
import com.zhbstudy.baselibrary.util.MD5Util;

import java.util.List;

/**
 * Created by kaifa on 2017/7/3.
 * <p>
 * Descripte:
 */
public class CacheDataUtil {
    public static String getCacheResultJson(String finalUrl) {
        IDaoSupport<CacheData> dateIDaoSupport = DaoSupportFactory.getFactory().getDao(CacheData.class);
        List<CacheData> cacheDates = dateIDaoSupport.querySupport()
                //finalutl http:w -->报错 ，所以进行finalutl加密
                .selection("mUrlKey = ?").selectionArgs(MD5Util.string2MD5(finalUrl))
                .query();
        if (cacheDates.size() != 0) {
            //代表有数据
            CacheData cacheDate = cacheDates.get(0);
            String resultJson = cacheDate.getmResultJson();
            return resultJson;
        }
        return "";
    }

    public static long cacheData(String finalUrl, String result) {
        IDaoSupport<CacheData> dateIDaoSupport = DaoSupportFactory.getFactory().getDao(CacheData.class);
        dateIDaoSupport.delete("mUrlKey = ?", MD5Util.string2MD5(finalUrl));
        long insert = dateIDaoSupport.insert(new CacheData(finalUrl, result));
        return insert;
    }
}
