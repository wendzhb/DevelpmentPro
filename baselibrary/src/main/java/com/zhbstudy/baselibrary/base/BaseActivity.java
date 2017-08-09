package com.zhbstudy.baselibrary.base;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.zhbstudy.baselibrary.ioc.ViewUtils;

/**
 * Created by alex on 2017/5/12.
 *
 * Des:整个应用的BaseActivity
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置布局layout
        setContentView();

        //一些特定的算法，子类基本都会使用的
        ViewUtils.inject(this);

        //初始化头部
        initTitle();

        //初始化界面
        initView();

        //初始化数据
        initData();
    }

    //初始化数据
    protected abstract void initData();

    //初始化界面
    protected abstract void initView();

    //初始化头部
    protected abstract void initTitle();

    //设置布局layout
    protected abstract void setContentView();

    /**
     * 启动Activity
     * @param clazz
     */
    protected void startActivity(Class<?> clazz){
        Intent intent = new Intent(this,clazz);
        startActivity(intent);
    }

    /**
     * findViewById
     * @param viewId
     * @param <T>
     * @return View
     */
    protected <T extends View>  T viewById(int viewId){
        return (T) findViewById(viewId);
    }

    //只能放一些通用的方法，基本每个Activity都需要使用的方法，readDataBase最好不要放进来
    //如果是两个或两个以上的地方要使用，最好写一个工具类。
    //为什么？下周末会讲热修复： 阿里开源的 divalk层的方法是怎么加载进来的；    腾讯使用的分包问题
    //永远预留一层
}
