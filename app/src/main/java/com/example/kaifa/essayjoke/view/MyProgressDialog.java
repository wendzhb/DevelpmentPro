package com.example.kaifa.essayjoke.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.kaifa.essayjoke.R;

/**
 * Created by zhb on 2017/7/14.
 */

public class MyProgressDialog extends Dialog {

    public MyProgressDialog(Context context) {
        super(context);
    }

    public MyProgressDialog(Context context, int theme) {
        super(context, theme);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_loading, null); // 加载自己定义的布局
        setContentView(view);// 为Dialoge设置自己定义的布局
    }

}
