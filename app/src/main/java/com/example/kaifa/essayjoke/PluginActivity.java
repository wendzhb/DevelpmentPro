package com.example.kaifa.essayjoke;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.kaifa.essayjoke.plugin.PluginManager;
import com.zhbstudy.baselibrary.base.BaseActivity;

public class PluginActivity extends AppCompatActivity {

    String apkPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/plugin.apk";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plugin);

    }

    public void install(View view) {
        PluginManager.install(this, apkPath);
    }

    public void start(View view) {
        //启动插件，插件下载好了在内存卡里面
        Intent intent = new Intent();
        intent.setClassName(getPackageName(), "com.example.kaifa.essayjokeplugin.MainActivity");
        intent.putExtra("user_name", "test");
        startActivity(intent);
    }
}
