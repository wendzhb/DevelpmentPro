package com.example.kaifa.essayjoke;

import android.view.View;

import com.example.alex.framelibrary.navigationbar.DefaultNavigationBar;
import com.zhbstudy.baselibrary.base.BaseActivity;

public class HomeActivity extends BaseActivity {

    @Override
    protected void initTitle() {
        DefaultNavigationBar navigationBar = new DefaultNavigationBar.Builder(this)
                .setTitle("首页")
                .setRightText("测试")
                .hideLeftIcon()
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(MainActivity.class);
                    }
                })
                .builder();
    }


    @Override
    protected void initData() {
//        HttpUtils.with(this).get()
//                .url("")
//                .cache(true)
//                .execte(new HttpCallBack<Person>() {
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//
//                    }
//        });
//        HttpUtils.with(this).post()
//                .url("")
//                .cache(true)
//                .execte(new HttpCallBack<Person>() {
//                    @Override
//                    public void onError(Exception e) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Person result) {
//
//                    }
//        });
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_home);
    }
}
