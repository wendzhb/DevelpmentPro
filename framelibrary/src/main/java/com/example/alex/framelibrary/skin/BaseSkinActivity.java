package com.example.alex.framelibrary.skin;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.LayoutInflaterCompat;
import android.support.v4.view.LayoutInflaterFactory;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.VectorEnabledTintResources;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;

import com.example.alex.framelibrary.skin.attr.SkinAttr;
import com.example.alex.framelibrary.skin.attr.SkinView;
import com.example.alex.framelibrary.skin.callback.ISkinChangeListener;
import com.example.alex.framelibrary.skin.support.SkinAppCompatViewInflater;
import com.example.alex.framelibrary.skin.support.SkinAttrSupport;
import com.zhbstudy.baselibrary.base.BaseActivity;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alex on 2017/5/12.
 * <p>
 * Description:
 */
public abstract class BaseSkinActivity extends BaseActivity implements LayoutInflaterFactory, ISkinChangeListener {
    //后面会写插件换肤 预留的东西
    private SkinAppCompatViewInflater mAppCompatViewInflater;
    private String TAG = "BaseSkinActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        LayoutInflaterCompat.setFactory(layoutInflater, this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        //拦截到view的创建 获取view之后要去解析
        //1.创建view
        View view = createAppCompatView(parent, name, context, attrs);
        //2.解析属性 src textcolor backgroud 自定义属性
//        Log.e(TAG, view + "");
        //2.1一个activity的布局肯定对应多个这样的skinview
        if (view != null) {
            List<SkinAttr> skinAttrs = SkinAttrSupport.getSkinAttrs(context, attrs);
            SkinView skinView = new SkinView(view, skinAttrs);

            //3.统一交给skinmanager管理
            managerSkinView(skinView);

            //4.判断一下要不要换肤
            SkinManager.getInstance().checkChangeSkin(skinView);
        }

        return view;
    }


    /**
     * 统一管理skinview
     * @param skinView
     */
    protected void managerSkinView(SkinView skinView) {
        List<SkinView> skinViews = SkinManager.getInstance().getSkinViews(this);
        if (skinViews == null) {
            skinViews = new ArrayList<>();
            SkinManager.getInstance().register(this,skinViews);
        }
        skinViews.add(skinView);
    }

    private View createAppCompatView(View parent, final String name, @NonNull Context context,
                           @NonNull AttributeSet attrs) {
        final boolean isPre21 = Build.VERSION.SDK_INT < 21;

        if (mAppCompatViewInflater == null) {
            mAppCompatViewInflater = new SkinAppCompatViewInflater();
        }

        // We only want the View to inherit it's context if we're running pre-v21
        final boolean inheritContext = isPre21 && true
                && shouldInheritContext((ViewParent) parent);

        return mAppCompatViewInflater.createView(parent, name, context, attrs, inheritContext,
                isPre21, /* Only read android:theme pre-L (L+ handles this anyway) */
                true /* Read read app:theme as a fallback at all times for legacy reasons */
        );
    }

    private boolean shouldInheritContext(ViewParent parent) {
        if (parent == null) {
            // The initial parent is null so just return false
            return false;
        }
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true;
            } else if (parent == getWindow().getDecorView() || !(parent instanceof View)
                    || ViewCompat.isAttachedToWindow((View) parent)) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false;
            }
            parent = parent.getParent();
        }
    }

    @Override
    protected void onDestroy() {
        SkinManager.getInstance().unregister(this);
        super.onDestroy();
    }

    /**
     * 处理第三方，自定义view
     * @param skinResource
     */
    @Override
    public void changeSkin(SkinResource skinResource) {

    }
}
