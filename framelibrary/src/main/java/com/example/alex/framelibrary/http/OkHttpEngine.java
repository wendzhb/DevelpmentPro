package com.example.alex.framelibrary.http;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.zhbstudy.baselibrary.http.EngineCallBack;
import com.zhbstudy.baselibrary.http.HttpUtils;
import com.zhbstudy.baselibrary.http.IHttpEngine;

import java.io.File;
import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by kaifa on 2017/6/30.
 * <p>
 * Descripte:
 */
public class OkHttpEngine implements IHttpEngine {

    /**
     * OkHttpClient客户端
     */
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    public void get(final boolean cache, Context context, String url, Map<String, Object> params, final EngineCallBack callBack) {
        //请求路径
        final String finalUrl = HttpUtils.jointParams(url, params);//用来打印请求
        Log.e("http", "get请求参数?" + finalUrl);
        //判断是否需要缓存，然后判断有没有
        if (cache) {
            //需要缓存，从数据库拿缓存
            //数据库缓存在 framelibrary
            String resultJson = CacheDataUtil.getCacheResultJson(finalUrl);
            if (!TextUtils.isEmpty(resultJson)) {
                //需要缓存，而且数据库有缓存，直接执行回调
                callBack.onSuccess(resultJson);
            }
        }
        Request.Builder requestBuild = new Request.Builder().url(finalUrl).tag(context);
        requestBuild.method("GET", null);
        final Request request = requestBuild.build();
        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                //判断是否需要缓存，然后判断有没有
                if (cache) {
                    String resultJson = CacheDataUtil.getCacheResultJson(finalUrl);
                    if (!TextUtils.isEmpty(resultJson)) {
                        if (resultJson.equals(result)) {
                            return;
                        }
                    }
                    CacheDataUtil.cacheData(finalUrl, result);
                }
                //获取数据之后会执行成功方法
                //每次获取数据
                callBack.onSuccess(result);
            }
        });
    }

    @Override
    public void post(boolean cache, Context context, String url, Map<String, Object> params, final EngineCallBack callBack) {

        final String joinUrl = HttpUtils.jointParams(url, params);//用来打印请求
        Log.e("http", "post请求参数:" + joinUrl);

        RequestBody requestBody = appenBuild(params);

        Request request = new Request.Builder().url(url).tag(context)
                .post(requestBody).build();

        mOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callBack.onError(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                //这两个回调都不是在主线程中
                String result = response.body().string();
                callBack.onSuccess(result);
            }
        });
    }

    /**
     * 组装post请求参数body
     *
     * @param params
     * @return
     */
    private RequestBody appenBuild(Map<String, Object> params) {
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        addParams(builder, params);
        return builder.build();
    }

    //添加参数
    private void addParams(MultipartBody.Builder builder, Map<String, Object> params) {

        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {

                builder.addFormDataPart(key, params.get(key) + "");
                Object value = params.get(key);

                if (value instanceof File) {
                    //处理文件 --> object File
                    File file = (File) value;
                    builder.addFormDataPart(key, file.getName(), RequestBody
                            .create(MediaType.parse(guessMimeType(file.getAbsolutePath())), file));
                } else if (value instanceof List) {
                    //代表提交的是 list集合
                    List<File> listFiles = (List<File>) value;
                    for (int i = 0; i < listFiles.size(); i++) {
                        //获取文件
                        File file = listFiles.get(i);
                        builder.addFormDataPart(key + i, file.getName(), RequestBody.create(
                                MediaType.parse(guessMimeType(file.getAbsolutePath())), file
                        ));
                    }
                } else {
                    builder.addFormDataPart(key, value + "");
                }
            }
        }
    }

    /**
     * 猜测文件的类型
     *
     * @param path
     * @return
     */
    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }
}
