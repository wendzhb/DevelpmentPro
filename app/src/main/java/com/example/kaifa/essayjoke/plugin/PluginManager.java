package com.example.kaifa.essayjoke.plugin;

import android.content.Context;

import com.zhbstudy.baselibrary.fixBug.*;

/**
 * Created by zhb on 2017/8/22.
 */

public class PluginManager {

    public static final void install(Context context, String apkPath) {
        //解决类加载的问题
        try {
            FixDexManager fixDexManager = new FixDexManager(context);
            //把apk的class加载到applicationClassLoader
            fixDexManager.fixDex(apkPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
