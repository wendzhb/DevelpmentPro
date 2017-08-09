package com.zhbstudy.baselibrary.http;

import android.content.Context;

import java.util.Map;
import java.util.Objects;

/**
 * Created by kaifa on 2017/6/30.
 * <p>
 * Descripte:引擎的规范
 */
public interface IHttpEngine {

    //get请求
    void get(boolean cache, Context context, String url, Map<String, Object> params, EngineCallBack callBack);

    //post请求
    void post(boolean cache, Context context, String url, Map<String, Object> params, EngineCallBack callBack);

    //下载文件
    //上传文件
    //https 添加证书
}
