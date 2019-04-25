package com.example.alex.framelibrary.http;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.zhbstudy.baselibrary.http.EngineCallBack;
import com.zhbstudy.baselibrary.http.HttpUtils;

import java.util.Map;

/**
 * Created by kaifa on 2017/7/3.
 * <p>
 * Descripte:
 */
public abstract class HttpCallBack<T> implements EngineCallBack {

    private Context mContext;

    @Override
    public void onPreExecute(Context context, Map<String, Object> params) {
        //添加功用参数 与项目业务逻辑有关的
        params.put("app_name", "joke_essay");
        params.put("version_name", "5.7.0");
        params.put("ac", "wifi");
        params.put("device_id", "30036118478");
        params.put("device_brand", "Xiaomi");
        params.put("update_version_code", "5701");
        params.put("mainfest_version_code", "570");
        params.put("longitude", "113.000366");
        params.put("latitude", "28.171377");
        params.put("device_platform", "android");

        onPreExecute();
    }

    //开始执行了
    public void onPreExecute() {

    }

    @Override
    public void onSuccess(final String result) {
        //result --> 对象     转换成可操作的对象

        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e("tag", "onSuccess");
                Gson gson = new Gson();
                T objResult = (T) gson.fromJson(result, HttpUtils.analysisClazzInfo(this));
                onSuccess(objResult);
            }
        });

    }

    //返回可以直接操作的对象
    public abstract void onSuccess(T result);
}
