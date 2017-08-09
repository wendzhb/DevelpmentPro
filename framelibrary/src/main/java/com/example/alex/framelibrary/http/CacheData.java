package com.example.alex.framelibrary.http;

/**
 * Created by zhb on 2017/7/3.
 * <p>
 * Descripte:缓存的实体类
 */
public class CacheData {
    //请求链接
    private String mUrlKey;
    //返回的json
    private String mResultJson;

    public CacheData() {
    }

    public CacheData(String mUrlKey, String mResultJson) {
        this.mUrlKey = mUrlKey;
        this.mResultJson = mResultJson;
    }

    public String getmUrlKey() {
        return mUrlKey;
    }

    public void setmUrlKey(String mUrlKey) {
        this.mUrlKey = mUrlKey;
    }

    public String getmResultJson() {
        return mResultJson;
    }

    public void setmResultJson(String mResultJson) {
        this.mResultJson = mResultJson;
    }
}
