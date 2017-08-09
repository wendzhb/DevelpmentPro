package com.zhbstudy.baselibrary.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.text.style.CharacterStyle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhbstudy.baselibrary.R;

import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * Created by kaifa on 2017/6/16.
 * <p>
 * Descripte:自定义的万能dialog
 */
public class MyAlertDialog extends Dialog {
    private MyAlertController mAlert;

    public MyAlertDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
        mAlert = new MyAlertController(this, getWindow());
    }

    /**
     * 设置文本
     *
     * @param viewId
     * @param charSequence
     */
    public void setText(int viewId, CharSequence charSequence) {
        mAlert.setText(viewId,charSequence);
    }

    /**
     * 设置点击事件
     *
     * @param viewId
     * @param listener
     */
    public void setOnClickListener(int viewId, View.OnClickListener listener) {
        mAlert.setOnClickListener(viewId,listener);
    }

    public <T extends View> T getView(int viewId) {
        return mAlert.getView(viewId);
    }

    public static class Builer {

        private final MyAlertController.MyAlertParams P;

        public Builer(@NonNull Context context) {
            this(context, R.style.dialog);
        }

        public Builer(@NonNull Context context, @StyleRes int themeResId) {
            P = new MyAlertController.MyAlertParams(context, themeResId);
        }

        /**
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder.
         * <p>
         * Calling this method does not display the dialog. If no additional
         * processing is needed, {@link #show()} may be called instead to both
         * create and display the dialog.
         */
        public MyAlertDialog create() {
            // We can't use Dialog's 3-arg constructor with the createThemeContextWrapper param,
            // so we always have to re-set the theme
            final MyAlertDialog dialog = new MyAlertDialog(P.mContext, P.mThemeResId);
            P.apply(dialog.mAlert);
            dialog.setCancelable(P.mCancelable);
            if (P.mCancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }
            dialog.setOnCancelListener(P.mOnCancelListener);
            dialog.setOnDismissListener(P.mOnDismissListener);
            if (P.mOnKeyListener != null) {
                dialog.setOnKeyListener(P.mOnKeyListener);
            }
            return dialog;
        }

        /**
         * Creates an {@link AlertDialog} with the arguments supplied to this
         * builder and immediately displays the dialog.
         * <p>
         * Calling this method is functionally identical to:
         * <pre>
         *     AlertDialog dialog = builder.create();
         *     dialog.show();
         * </pre>
         */
        public MyAlertDialog show() {
            final MyAlertDialog dialog = create();
            dialog.show();
            return dialog;
        }


        /**
         * Sets a custom view to be the contents of the alert dialog.
         * <p>
         * When using a pre-Holo theme, if the supplied view is an instance of
         * a {@link ListView} then the light background will be used.
         * <p>
         * <strong>Note:</strong> To ensure consistent styling, the custom view
         * should be inflated or constructed using the alert dialog's themed
         * context obtained via {@link #getContext()}.
         *
         * @param view the view to use as the contents of the alert dialog
         * @return this Builder object to allow for chaining of calls to set
         * methods
         */
        //设置布局
        public Builer setContentView(View view) {
            P.mView = view;
            P.mViewLayoutResId = 0;
            return this;
        }

        /**
         * Set a custom view resource to be the contents of the Dialog. The
         * resource will be inflated, adding all top-level views to the screen.
         *
         * @param layoutId Resource ID to be inflated.
         * @return this Builder object to allow for chaining of calls to set methods
         */
        //设置布局layout id
        public Builer setContentView(int layoutId) {
            P.mView = null;
            P.mViewLayoutResId = layoutId;
            return this;
        }

        /**
         * Sets whether the dialog is cancelable or not.  Default is true.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builer setCancelable(boolean cancelable) {
            P.mCancelable = cancelable;
            return this;
        }

        /**
         * Sets the callback that will be called if the dialog is canceled.
         * <p>
         * <p>Even in a cancelable dialog, the dialog may be dismissed for reasons other than
         * being canceled or one of the supplied choices being selected.
         * If you are interested in listening for all cases where the dialog is dismissed
         * and not just when it is canceled, see
         * {@link #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         * setOnDismissListener}.</p>
         *
         * @return This Builder object to allow for chaining of calls to set methods
         * @see #setCancelable(boolean)
         * @see #setOnDismissListener(android.content.DialogInterface.OnDismissListener)
         */
        public Builer setOnCancelListener(OnCancelListener onCancelListener) {
            P.mOnCancelListener = onCancelListener;
            return this;
        }

        /**
         * Sets the callback that will be called when the dialog is dismissed for any reason.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builer setOnDismissListener(OnDismissListener onDismissListener) {
            P.mOnDismissListener = onDismissListener;
            return this;
        }

        /**
         * Sets the callback that will be called if a key is dispatched to the dialog.
         *
         * @return This Builder object to allow for chaining of calls to set methods
         */
        public Builer setOnKeyListener(OnKeyListener onKeyListener) {
            P.mOnKeyListener = onKeyListener;
            return this;
        }

        //设置文本
        public Builer setText(int viewId, CharSequence text) {
            P.mTextArray.put(viewId, text);
            return this;
        }

        //设置点击事件
        public Builer setOnClickListener(int view, View.OnClickListener listener) {
            P.mClickArray.put(view, listener);
            return this;
        }

        //配置一些万能的参数
        public Builer fullWidth() {
            P.mWidth = ViewGroup.LayoutParams.MATCH_PARENT;
            return this;
        }

        /**
         * 从底部弹出，是否有动画
         *
         * @param isAnimation
         * @return
         */
        public Builer fromBottom(boolean isAnimation) {
            if (isAnimation) {
                P.mAnimations = R.style.dialog_from_bottom_anim;
            }
            P.mGravity = Gravity.BOTTOM;
            return this;
        }

        /**
         * 设置dialog的宽高
         * @param width
         * @return
         */
        public Builer setWidthAndHeight(int width, int height) {
            P.mWidth = width;
            P.mHeight = height;
            return this;
        }

        /**
         * 设置默认动画
         * @return
         */
        public Builer setDefaultAnimation() {
            P.mAnimations = R.style.dialog_scan_anim;
            return this;
        }

        /**
         * 设置动画
         * @param styleAnimation
         * @return
         */
        public Builer setAnimations(int styleAnimation) {
            P.mAnimations = styleAnimation;
            return this;
        }

        /**
         * 设置X/Y坐标
         * @param X
         * @param Y
         * @return
         */
        public Builer setXAndY(int X, int Y) {
            P.MX = X;
            P.MY = Y;
            return this;
        }
    }


}
