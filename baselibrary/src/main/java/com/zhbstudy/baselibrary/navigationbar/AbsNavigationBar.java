package com.zhbstudy.baselibrary.navigationbar;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by kaifa on 2017/6/30.
 * <p>
 * Descripte:头部的builder基类
 */
public abstract class AbsNavigationBar<P extends AbsNavigationBar.Builder.AbsNavigationBarParams> implements INavigationBar {
    private P mParams;
    private View mNavigationView;

    public AbsNavigationBar(P mParams) {
        this.mParams = mParams;
        createAndBindView();
    }

    public P getParams() {
        return mParams;
    }

    protected void setText(int viewId, String text) {
        TextView textView = viewById(viewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    protected void setOnClickListener(int viewId, View.OnClickListener listener) {
        viewById(viewId).setOnClickListener(listener);
    }

    protected void setVisibility(int viewId, int visibility) {
        viewById(viewId).setVisibility(visibility);
    }

    public <T extends View> T viewById(int viewId) {
        return (T) mNavigationView.findViewById(viewId);
    }

    /**
     * 绑定和创建view
     */
    private void createAndBindView() {
        //1.创建view

        if (mParams.mParent == null) {
            //获取activity的根布局，view源码
            ViewGroup activityRoot = (ViewGroup) ((Activity) (mParams.mContext)).getWindow().getDecorView();
            mParams.mParent = (ViewGroup) activityRoot.getChildAt(0);
        }
        //处理activity的源码
        if (mParams.mParent == null) {
            return;
        }

        mNavigationView = LayoutInflater.from(mParams.mContext).inflate(bindLayoutId(), mParams.mParent, false);//插件换肤
        //2.添加
        mParams.mParent.addView(mNavigationView, 0);
        applyView();
    }

    //buider设计模式

    public static abstract class Builder {

        public Builder(Context context, ViewGroup parent) {
        }

        public abstract AbsNavigationBar builder();

        public static class AbsNavigationBarParams {
            public Context mContext;
            public ViewGroup mParent;

            public AbsNavigationBarParams(Context mContext, ViewGroup mParent) {
                this.mContext = mContext;
                this.mParent = mParent;
            }
        }
    }
}
