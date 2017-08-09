package com.example.alex.framelibrary.util;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * Created by zhb on 2017/7/13.
 * <p>
 * Descripte:
 */
public class StatusBarUtil {

    /**
     * 设置状态栏颜色
     *
     * @param activity
     * @param color
     */
    public static void statusBarTinColor(Activity activity, int color) {
        //代表5.0及以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.e("tag","tag1");
            activity.getWindow().setStatusBarColor(color);
            Log.e("tag","tag2");
            return;
        }
        //versionCode > 4.4 and versionCode < 5.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup androidContainer = (ViewGroup) activity.findViewById(android.R.id.content);
            //留出高度 setFitsSystemWindows true代表回调整布局，会把状态栏的高度留出来
            View contentView = androidContainer.getChildAt(0);
            if (contentView != null) {
                contentView.setFitsSystemWindows(true);
            }
            //在原来的位置上添加一个状态栏
            View statusBarView = createStatusBarView(activity);
            androidContainer.addView(statusBarView, 0);
            statusBarView.setBackgroundColor(color);
        }
    }

    private static View createStatusBarView(Activity activity) {
        View statusBarView = new View(activity);
        ViewGroup.LayoutParams statusBarParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                getStatusBarHeight(activity));
        statusBarView.setLayoutParams(statusBarParams);
        return statusBarView;
    }

    private static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
