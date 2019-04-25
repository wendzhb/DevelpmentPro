package com.example.kaifa.essayjoke.imageselect;

import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.alex.framelibrary.skin.BaseSkinActivity;
import com.example.alex.framelibrary.util.StatusBarUtil;
import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.base.BaseActivity;

import java.util.ArrayList;

public class PreviewActivity extends BaseSkinActivity {

    private ViewPagerFixed mVpImage;
    @Override
    protected void initData() {

        ArrayList<String> arrayList = getIntent().getStringArrayListExtra(SelectImageActivity.EXTRA_RESULT);
        mVpImage.setAdapter(new ImageAdapter(this,arrayList));

    }

    @Override
    protected void initView() {
        mVpImage = (ViewPagerFixed) findViewById(R.id.vp_preview);
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_preview);
    }
}
