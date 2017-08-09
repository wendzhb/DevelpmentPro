package com.example.kaifa.essayjoke;

import com.alipay.euler.andfix.patch.PatchManager;
import com.example.alex.framelibrary.http.OkHttpEngine;
import com.example.alex.framelibrary.skin.SkinManager;
import com.zhbstudy.baselibrary.crash.ExceptionCrashHandler;
import com.zhbstudy.baselibrary.fixBug.FixDexManager;
import com.zhbstudy.baselibrary.http.HttpUtils;

import org.litepal.LitePal;
import org.litepal.LitePalApplication;

/**
 * Created by alex on 2017/6/1.
 * <p>
 * Descripte:
 */
public class BaseApplication extends LitePalApplication {

    public static PatchManager mPatchManager;

    @Override
    public void onCreate() {
        super.onCreate();
        //设置全局异常捕捉类
        ExceptionCrashHandler.getInstance().init(this);

        HttpUtils.init(new OkHttpEngine());

        SkinManager.getInstance().init(this);

        /*//初始化阿里热修复
        mPatchManager = new PatchManager(getApplicationContext());
        //初始化版本，获取当前应用的版本
        try {
            String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
            mPatchManager.init(versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        //加载之前的patch包
        mPatchManager.loadPatch();*/

        LitePal.initialize(this);

        try {
            FixDexManager fixDexManager = new FixDexManager(this);
            //加载所有修复的dex包
            fixDexManager.loadFixDex();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
