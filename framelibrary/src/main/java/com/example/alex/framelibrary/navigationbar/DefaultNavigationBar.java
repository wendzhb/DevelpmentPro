package com.example.alex.framelibrary.navigationbar;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.example.alex.framelibrary.R;
import com.zhbstudy.baselibrary.navigationbar.AbsNavigationBar;

/**
 * Created by kaifa on 2017/6/30.
 * <p>
 * Descripte:
 */
public class DefaultNavigationBar extends AbsNavigationBar<DefaultNavigationBar.Builder.DefaultNavigationParams> {

    public DefaultNavigationBar(Builder.DefaultNavigationParams mParams) {
        super(mParams);
    }

    @Override
    public int bindLayoutId() {
        return R.layout.title_bar;
    }

    @Override
    public void applyView() {
        //绑定效果
        setText(R.id.title, getParams().mTitle);
        setText(R.id.right_text, getParams().mRightText);
        setOnClickListener(R.id.right_text, getParams().mRightOnClickListener);
        //左边 写一个默认 finishactivity
        setOnClickListener(R.id.back, getParams().mLeftOnClickListener);
        //设置左侧返回键是否可见
        setVisibility(R.id.back,getParams().leftIconVisiable);

    }


    public static class Builder extends AbsNavigationBar.Builder {
        DefaultNavigationParams P;

        public Builder(Context context, ViewGroup parent) {
            super(context, parent);
            P = new DefaultNavigationParams(context, parent);
        }

        public Builder(Context context) {
            this(context, null);
        }

        @Override
        public DefaultNavigationBar builder() {
            DefaultNavigationBar navigationBar = new DefaultNavigationBar(P);
            return navigationBar;
        }

        //1.设置所有的效果

        /**
         * 设置标题
         *
         * @param title
         * @return
         */
        public DefaultNavigationBar.Builder setTitle(String title) {
            P.mTitle = title;
            return this;
        }

        /**
         * 设置右侧文字
         *
         * @param rightText
         * @return
         */
        public DefaultNavigationBar.Builder setRightText(String rightText) {
            P.mRightText = rightText;
            return this;
        }

        /**
         * 设置右侧图片
         *
         * @param rightRes
         * @return
         */
        public DefaultNavigationBar.Builder setRightIcon(int rightRes) {
            P.mRightRes = rightRes;
            return this;
        }

        /**
         * 设置右侧点击事件
         *
         * @param rightOnClickListener
         * @return
         */
        public DefaultNavigationBar.Builder setRightOnClickListener(View.OnClickListener rightOnClickListener) {
            P.mRightOnClickListener = rightOnClickListener;
            return this;
        }

        /**
         * 设置左侧点击事件
         *
         * @param leftOnClickListener
         * @return
         */
        public DefaultNavigationBar.Builder setLeftOnClickListener(View.OnClickListener leftOnClickListener) {
            P.mLeftOnClickListener = leftOnClickListener;
            return this;
        }

        public DefaultNavigationBar.Builder hideLeftIcon() {
            P.leftIconVisiable = View.INVISIBLE;
            return this;
        }

        public static class DefaultNavigationParams extends AbsNavigationBarParams {

            //所有效果放置
            public String mTitle;
            public String mRightText;
            public int mRightRes;
            public int leftIconVisiable = View.VISIBLE;
            public View.OnClickListener mRightOnClickListener;
            public View.OnClickListener mLeftOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //关闭当前的activity
                    ((Activity) mContext).finish();
                }
            };

            //2.所有的效果
            public DefaultNavigationParams(Context mContext, ViewGroup mParent) {
                super(mContext, mParent);
            }
        }
    }
}
