package com.example.alex.framelibrary.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by zhb on 2017/7/6.
 * <p>
 * Descripte:皮肤的资源管理
 */
public class SkinResource {
    //资源通过这个对象获取
    private Resources mSkinResources;
    private String mPackageName;

    public SkinResource(Context context, String skinPath) {
        try {
            //读取本地的一个.skin里面的资源
            Resources superRes = context.getResources();
            //创建AssetManager
            AssetManager assets = AssetManager.class.newInstance();

            //添加本地下载好的资源皮肤  native层c和c++
            Method method = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            //method.setAccessible(true);如果是私有的
            //反射执行方法
            method.invoke(assets, skinPath);

            mSkinResources = new Resources(assets, superRes.getDisplayMetrics(),
                    superRes.getConfiguration());

            //获取skinpath包名
            mPackageName = context.getPackageManager().getPackageArchiveInfo(skinPath, PackageManager.GET_ACTIVITIES).packageName;


        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过名字获取Drawable
     *
     * @param resName
     * @return
     */
    public Drawable getDrawableByName(String resName) {
        Drawable drawable = null;
        try {
            int resId = mSkinResources.getIdentifier(resName, "drawable", mPackageName);
            drawable = mSkinResources.getDrawable(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 通过名字获取颜色
     *
     * @param resName
     * @return
     */
    public ColorStateList getColorByName(String resName) {
        ColorStateList colorStateList = null;
        try {
            int resId = mSkinResources.getIdentifier(resName, "color", mPackageName);
            colorStateList = mSkinResources.getColorStateList(resId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return colorStateList;
    }
}
