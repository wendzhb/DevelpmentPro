package com.zhbstudy.baselibrary.dialog;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.lang.ref.WeakReference;

/**
 * Created by kaifa on 2017/6/16.
 * <p>
 * Descripte:Dialog View的辅助处理类
 */
class DialogViewHelper {

    private View mContenView;

    private SparseArray<WeakReference<View>> mViews;


    public DialogViewHelper(Context mContext, int mViewLayoutResId) {
        this();
        mContenView = LayoutInflater.from(mContext).inflate(mViewLayoutResId, null);
    }

    public DialogViewHelper() {
        mViews = new SparseArray<>();
    }

    /**
     * 设置布局
     *
     * @param contentView
     */
    public void setContentView(View contentView) {
        this.mContenView = contentView;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param charSequence
     */
    public void setText(int viewId, CharSequence charSequence) {
        //每次都findviewbyid 减少findviewbyid的次数
        TextView textView = getView(viewId);
        if (textView != null) {
            textView.setText(charSequence);
        }
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        View view = getView(viewId);
        if (view != null) {
            view.setOnClickListener(listener);
        }
    }

    public <T extends View> T getView(int viewId) {
        WeakReference<View> viewWeakReference = mViews.get(viewId);
        //软引用 系统优化
        View view = null;
        if (viewWeakReference != null) {
            view = viewWeakReference.get();
        }
        if (view == null) {
            view = mContenView.findViewById(viewId);
            if (view != null) {
                mViews.put(viewId, new WeakReference<View>(view));
            }
        }
        return (T) view;
    }

    /**
     * 获取ContentView
     *
     * @return
     */
    public View getContentView() {
        return mContenView;
    }
}
