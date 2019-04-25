package com.example.kaifa.essayjoke.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.kaifa.essayjoke.R;
import com.example.kaifa.essayjoke.utils.LruCacheUtils;
import com.zhbstudy.baselibrary.ioc.ViewById;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;

public class BitmapLurcacheActivity extends SkinActivity {

    private LruCacheUtils lruCacheUtils;
    private static final String DISK_CACHE_SUBDIR = "temp";//目录名
    private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; //定义磁盘缓存大小,10MB

    @ViewById(R.id.iv2)
    private ImageView iv2;

    @Override
    protected void initData() {
        super.initData();
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initTitle() {
        super.initTitle();
    }

    @Override
    protected void setContentView() {
        super.setContentView();
        setContentView(R.layout.activity_bitmap_lurcache);

    }

    public void show2Click(View view) {
        String url = "http://www.pptbz.com/pptpic/UploadFiles_6909/201401/2014012906353538.jpg";
        loadBitmap(url, iv2);
    }

    @Override
    protected void onResume() {
        super.onResume();
        lruCacheUtils = LruCacheUtils.getInstance();
        lruCacheUtils.open(this, DISK_CACHE_SUBDIR, DISK_CACHE_SIZE);
    }

    //刷新把缓存写好
    @Override
    protected void onPause() {
        super.onPause();
        lruCacheUtils.flush();
    }

    @Override
    protected void onStop() {
        super.onStop();
        lruCacheUtils.close();
    }

    public void loadBitmap(String url, final ImageView imageView) {
        //从内存缓存中取图片
        Bitmap bitmap = lruCacheUtils.getBitmapFromCache(url);
        if (bitmap == null) {
            //再从磁盘缓存中取
            InputStream in = lruCacheUtils.getDiskCache(url);
            if (in == null) {
                //去网上取
                lruCacheUtils.putCache(url, new LruCacheUtils.CallBack<Bitmap>() {
                    @Override
                    public void response(Bitmap entity) {
                        System.out.println("http load");
                        imageView.setImageBitmap(entity);
                    }
                });
            } else {
                System.out.println("disk cache");
                bitmap = BitmapFactory.decodeStream(in);
                //取完添加到内存缓存中
                lruCacheUtils.addBitmapToCache(url, bitmap);
                imageView.setImageBitmap(bitmap);
            }

        } else {
            System.out.println("memory cache");
            imageView.setImageBitmap(bitmap);
        }
    }

}
