package com.zhbstudy.baselibrary.http;

import android.content.Context;

import java.util.Map;
import java.util.Objects;

/**
 * Created by kaifa on 2017/6/30.
 * <p>
 * Descripte:
 */
public interface EngineCallBack {

    //错误
    public void onError(Exception e);

    //成功  返回对象会出问题  成功  data对象{"",""}  失望  data:""
    public void onSuccess(String result);

    //开始执行，在执行之前会回调的方法
    public void onPreExecute(Context context, Map<String, Object> params);

    //默认的
    public final EngineCallBack DEFAULT_CALL_BACK = new EngineCallBack() {
        @Override
        public void onError(Exception e) {

        }

        @Override
        public void onSuccess(String result) {

        }

        @Override
        public void onPreExecute(Context context, Map<String, Object> params) {

        }
    };

}
