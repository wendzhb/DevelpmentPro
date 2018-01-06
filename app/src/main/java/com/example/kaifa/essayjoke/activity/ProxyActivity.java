package com.example.kaifa.essayjoke.activity;

import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;

import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.base.BaseActivity;

public class ProxyActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.getDeviceId();

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_proxy);
    }
}
