package com.example.kaifa.essayjoke.activity;

import android.content.Intent;
import android.os.Environment;
import android.widget.Button;
import android.widget.Toast;

import com.example.alex.framelibrary.skin.BaseSkinActivity;
import com.example.alex.framelibrary.skin.SkinManager;
import com.example.alex.framelibrary.skin.SkinResource;
import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.ioc.OnClick;
import com.zhbstudy.baselibrary.ioc.ViewById;

import java.io.File;

public class SkinActivity extends BaseSkinActivity {

    /****换肤****/
    @ViewById(R.id.btn1)
    private Button mBtn1;
    /****默认****/
    @ViewById(R.id.btn2)
    private Button mBtn2;
    /****跳转****/
    @ViewById(R.id.btn3)
    private Button mBtn3;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_skin);

    }

    @OnClick(R.id.btn1)
    private void btn1Click(Button btn1) {
        //从服务器上下载
        String skinPath = Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+"red.skin";
        //换肤
        int result = SkinManager.getInstance().loadSkin(skinPath);
    }

    @OnClick(R.id.btn2)
    private void btn2Click(Button btn2) {
        //恢复默认
        int result = SkinManager.getInstance().restoreDefault();
    }

    @OnClick(R.id.btn3)
    private void btn3Click(Button btn3) {
        startActivity(new Intent(this,SkinActivity.class));
    }

    @Override
    public void changeSkin(SkinResource skinResource) {
        //处理自定义view
        Toast.makeText(this, "处理自定义view", Toast.LENGTH_SHORT).show();
    }
}
