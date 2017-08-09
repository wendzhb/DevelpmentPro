package com.zhbstudy.baselibrary.http;

import android.content.Context;
import android.support.v4.util.ArrayMap;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Created by kaifa on 2017/6/30.
 * <p>
 * Descripte:自己的一套实现
 */
public class HttpUtils {

    //url，链式调用
    private String mUrl;

    //请求方式
    private int mType;
    public static final int POST_TYPE = 0X0011;
    public static final int GET_TYPE = 0X0012;

    //参数
    private Map<String, Object> mParams;

    //上下文
    private Context mContext;

    //是否读取缓存
    private boolean mCache = false;

    private HttpUtils(Context mContext) {
        this.mContext = mContext;
        mParams = new HashMap<>();
    }

    public static HttpUtils with(Context context) {
        return new HttpUtils(context);
    }

    //请求方式
    public HttpUtils post() {
        mType = POST_TYPE;
        return this;
    }

    public HttpUtils get() {
        mType = GET_TYPE;
        return this;
    }

    public HttpUtils url(String mUrl) {
        this.mUrl = mUrl;
        return this;
    }

    //添加参数
    public HttpUtils addParams(String key, Object value) {
        mParams.put(key, value);
        return this;
    }

    public HttpUtils addParams(Map<String, Object> params) {
        mParams.putAll(params);
        return this;
    }

    //是否配置缓存
    public HttpUtils cache(boolean isCache) {
        mCache = isCache;
        return this;
    }

    //添加回调
    public void execte(EngineCallBack callBack) {

        //每次执行都会进入这个方法，这里添加不好
        //1、baselibrary里面不包含业务逻辑
        //2、如果每个项目有多条业务线，会起冲突
        //解决办法：让callback回调

        if (callBack == null) {
            callBack = EngineCallBack.DEFAULT_CALL_BACK;
        }

        callBack.onPreExecute(mContext, mParams);

        //判断执行方法
        if (mType == POST_TYPE) {
            post(mUrl, mParams, callBack);
        }

        if (mType == GET_TYPE) {
            get(mUrl, mParams, callBack);
        }
    }

    public void execte() {
        execte(null);
    }

    //默认okhttpengine
    private static IHttpEngine mHttpEngine = null;

    //在apllication初始化引擎
    public static void init(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
    }

    /**
     * 每次可以自带引擎
     *
     * @param httpEngine
     */
    public HttpUtils exchangeEngine(IHttpEngine httpEngine) {
        mHttpEngine = httpEngine;
        return this;
    }

    private void get(String url, Map<String, Object> params, EngineCallBack callBack) {
        mHttpEngine.get(mCache, mContext, url, params, callBack);
    }

    private void post(String url, Map<String, Object> params, EngineCallBack callBack) {
        mHttpEngine.post(mCache, mContext, url, params, callBack);
    }

    /**
     * 拼接参数
     *
     * @param url
     * @param params
     * @return
     */
    public static String jointParams(String url, Map<String, Object> params) {
        if (params == null || params.size() <= 0) {
            return url;
        }

        StringBuffer stringBuffer = new StringBuffer(url);
        if (!url.contains("?")) {
            stringBuffer.append("?");
        } else {
            if (!url.endsWith("?")) {
                stringBuffer.append("&");
            }
        }

        for (Map.Entry<String, Object> entry : params.entrySet()) {
            stringBuffer.append(entry.getKey() + "=" + entry.getValue() + "&");
        }

        stringBuffer.deleteCharAt(stringBuffer.length() - 1);

        return stringBuffer.toString();
    }

    public static Class<?> analysisClazzInfo(Object objects) {
        //getGenericSuperclass获得带有泛型的父类
        Type genType = objects.getClass().getGenericSuperclass();
        //getActualTypeArguments获取参数化类型的数组，泛型可能有多个
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        return (Class<?>) params[0];
    }
}
