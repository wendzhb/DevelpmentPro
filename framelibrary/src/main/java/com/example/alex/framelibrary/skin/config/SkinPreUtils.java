package com.example.alex.framelibrary.skin.config;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by zhb on 2017/7/7.
 * <p>
 * Descripte:
 */
public class SkinPreUtils {
    private static SkinPreUtils mInstance;
    private Context mContext;

    private SkinPreUtils(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public static SkinPreUtils getInstance(Context context) {
        if (mInstance == null) {
            synchronized (SkinPreUtils.class) {
                if (mInstance == null) {
                    mInstance = new SkinPreUtils(context);
                }
            }
        }
        return mInstance;
    }

    /**
     * 保存当前皮肤路径
     *
     * @param skinPath
     */
    public void saveSkinPath(String skinPath) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(SkinConfig.SKIN_PATH_NAME, skinPath);
        edit.commit();
    }

    /**
     * 获取皮肤路径
     *
     * @return
     */
    public String getSkinPath() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(SkinConfig.SKIN_PATH_NAME, "");
    }

    public void clearSkinInfo() {
        saveSkinPath("");
    }
}
