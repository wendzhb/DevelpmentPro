package com.zhbstudy.baselibrary.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.HashMap;
import java.util.IllegalFormatException;

/**
 * Created by kaifa on 2017/6/16.
 * <p>
 * Descripte:
 */
class MyAlertController {

    private MyAlertDialog mDialog;
    private Window mWindow;
    private DialogViewHelper mViewHelper;

    public MyAlertController(MyAlertDialog dialog, Window window) {
        this.mDialog = dialog;
        this.mWindow = window;
    }

    public void setViewHelper(DialogViewHelper mViewHelper) {
        this.mViewHelper = mViewHelper;
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param charSequence
     */
    public void setText(int viewId, CharSequence charSequence) {
        mViewHelper.setText(viewId,charSequence);
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        mViewHelper.setOnClickListener(viewId,listener);
    }

    public <T extends View> T getView(int viewId) {
        return mViewHelper.getView(viewId);
    }
    /**
     * 获取dialog
     *
     * @return
     */
    public MyAlertDialog getDialog() {
        return mDialog;
    }

    /**
     * 获取dialog的Window
     *
     * @return
     */
    public Window getWindow() {
        return mWindow;
    }

    public static class MyAlertParams {

        public Context mContext;
        public int mThemeResId;

        //点击空白是否能够取消 默认点击阴影可以取消
        public boolean mCancelable = true;
        //dialog Cancel监听
        public DialogInterface.OnCancelListener mOnCancelListener;
        //dialog Dismiss监听
        public DialogInterface.OnDismissListener mOnDismissListener;
        //dialog KeyListener监听
        public DialogInterface.OnKeyListener mOnKeyListener;
        //布局view
        public View mView;
        //布局layout id
        public int mViewLayoutResId;
        //存放字体的修改
        public SparseArray<CharSequence> mTextArray = new SparseArray<>();
        //存放点击事件
        public SparseArray<View.OnClickListener> mClickArray = new SparseArray<>();
        //宽度
        public int mWidth = ViewGroup.LayoutParams.WRAP_CONTENT;
        public int mHeight = ViewGroup.LayoutParams.WRAP_CONTENT;
        //位置
        public int mGravity = Gravity.CENTER;
        //动画
        public int mAnimations = 0;
        //新位置X坐标
        public int MX = 0;
        //新位置Y坐标
        public int MY = 0;


        public MyAlertParams(Context context, int themeResId) {
            this.mContext = context;
            this.mThemeResId = themeResId;
        }
        /**
         * 绑定和设置参数
         *
         * @param mAlert
         */
        public void apply(MyAlertController mAlert) {
            //完善这个方法，设置参数

            //1.设置dialog布局 DialogViewHelper
            DialogViewHelper viewHelper = null;
            if (mViewLayoutResId != 0) {
                viewHelper = new DialogViewHelper(mContext, mViewLayoutResId);
            }

            if (mView != null) {
                viewHelper = new DialogViewHelper();
                viewHelper.setContentView(mView);
            }

            if (viewHelper == null) {
                throw new IllegalArgumentException("请设置布局 setContenView");
            }

            //设置controller的辅助类
            mAlert.setViewHelper(viewHelper);

            //给dialog设置布局
            mAlert.getDialog().setContentView(viewHelper.getContentView());

            //2.设置文本
            int textArraySize = mTextArray.size();
            for (int i = 0; i < textArraySize; i++) {
                viewHelper.setText(mTextArray.keyAt(i), mTextArray.valueAt(i));
            }
            //3.设置点击
            int clickArraySize = mClickArray.size();
            for (int i = 0; i < clickArraySize; i++) {
                viewHelper.setOnClickListener(mClickArray.keyAt(i), mClickArray.valueAt(i));
            }
            //4.配置自定义的效果 全屏 从底部弹出 默认动画
            Window window = mAlert.getWindow();

            //设置位置
            window.setGravity(mGravity);

            //设置动画
            if (mAnimations != 0) {
                window.setWindowAnimations(mAnimations);
            }

            //设置宽高
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = mWidth;
            params.height = mHeight;
            window.setAttributes(params);

            //设置坐标位置
            if (MX != 0 || MY != 0) {
                params.x = MX; // 新位置X坐标
                params.y = MY; // 新位置Y坐标
                mAlert.getDialog().onWindowAttributesChanged(params);
            }


        }
    }
}
