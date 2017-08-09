package com.example.alex.framelibrary.skin;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.alex.framelibrary.skin.attr.SkinView;
import com.example.alex.framelibrary.skin.callback.ISkinChangeListener;
import com.example.alex.framelibrary.skin.config.SkinConfig;
import com.example.alex.framelibrary.skin.config.SkinPreUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhb on 2017/7/6.
 * <p>
 * Descripte:皮肤的管理类
 */
public class SkinManager {
    private static SkinManager mInstance;
    private Context mContext;
    private SkinResource mSkinResource;

    private Map<ISkinChangeListener, List<SkinView>> mSkinViews = new HashMap<>();

    static {
        mInstance = new SkinManager();
    }


    public static SkinManager getInstance() {
        return mInstance;
    }

    public void init(Context context) {
        this.mContext = context.getApplicationContext();
        //每一次打开应用都会执行，防止皮肤被任意删除，做一些措施
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();

        File file = new File(currentSkinPath);
        if (!file.exists()) {
            //如果文件不存在，清空皮肤
            SkinPreUtils.getInstance(mContext).clearSkinInfo();
            return;
        }

        //最好做一下，获取到包名
        //获取skinpath包名
        String packageName = context.getPackageManager().getPackageArchiveInfo(currentSkinPath, PackageManager.GET_ACTIVITIES).packageName;
        if (TextUtils.isEmpty(packageName)) {
            SkinPreUtils.getInstance(mContext).clearSkinInfo();
            return;
        }

        //做好校验签名
        //做一些初始化的工作
        mSkinResource = new SkinResource(mContext, currentSkinPath);

    }

    /**
     * 加载皮肤
     *
     * @param skinPath
     * @return
     */
    public int loadSkin(String skinPath) {

        File file = new File(skinPath);
        if (!file.exists()) {
            return SkinConfig.SKIN_FILE_NOEXSIST;
        }

        //获取skinpath包名
        String packageName = mContext.getPackageManager().getPackageArchiveInfo(skinPath,
                PackageManager.GET_ACTIVITIES).packageName;
        if (TextUtils.isEmpty(packageName)) {
            return SkinConfig.SKIN_FILE_ERROR;
        }

        //判断当前的皮肤是否一样
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if (skinPath.equals(currentSkinPath)) {
            return SkinConfig.SKIN_CHANGE_SUCCESS;
        }
        //校验签名  增量更新再说
        //保存皮肤的状态
        Log.e("tag", "skinPath1" + skinPath);
        //增连更新的时候最好把它复制走，用户不能轻易删除的地方 cache目录下面
        //初始化资源管理
        mSkinResource = new SkinResource(mContext, skinPath);
        //改变皮肤
        changeSkin();

        saveSkinStatus(skinPath);
        return SkinConfig.SKIN_CHANGE_SUCCESS;
    }

    /**
     * 改变皮肤
     */
    private void changeSkin() {
        Set<ISkinChangeListener> keys = mSkinViews.keySet();
        for (ISkinChangeListener key : keys) {
            List<SkinView> skinViews = mSkinViews.get(key);
            for (SkinView skinView : skinViews) {
                skinView.skin();
            }
            //通知activity
            key.changeSkin(mSkinResource);
        }
    }

    private void saveSkinStatus(String skinPath) {
        SkinPreUtils.getInstance(mContext).saveSkinPath(skinPath);
    }

    /**
     * 恢复默认
     *
     * @return
     */
    public int restoreDefault() {
        //判断当前有没有皮肤，没有皮肤就不要执行
        String currentSkinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if (TextUtils.isEmpty(currentSkinPath)) {
            return SkinConfig.SKIN_CHANGE_NOTHING;
        }
        //当前手机运行的app的apk路径
        String skinPath = mContext.getPackageResourcePath();
        //初始化资源管理
        mSkinResource = new SkinResource(mContext, skinPath);
        //改变皮肤
        changeSkin();
        //把皮肤信息清空
        SkinPreUtils.getInstance(mContext).clearSkinInfo();

        return SkinConfig.SKIN_CHANGE_SUCCESS;
    }

    /**
     * 通过activity获取SkinView
     *
     * @param activity
     * @return
     */
    public List<SkinView> getSkinViews(Activity activity) {
        return mSkinViews.get(activity);
    }

    /**
     * 注册
     *
     * @param skinChangeListener
     * @param skinViews
     */
    public void register(ISkinChangeListener skinChangeListener, List<SkinView> skinViews) {
        mSkinViews.put(skinChangeListener, skinViews);
    }

    /**
     * 获取当前皮肤的管理
     *
     * @return
     */
    public SkinResource getSkinResource() {
        return mSkinResource;
    }

    /**
     * 检测要不要换肤
     *
     * @param skinView
     */
    public void checkChangeSkin(SkinView skinView) {
        //如果当前有皮肤，也就是保存了皮肤路径，就换肤
        String skinPath = SkinPreUtils.getInstance(mContext).getSkinPath();
        if (!TextUtils.isEmpty(skinPath)) {
            skinView.skin();
        }
    }

    /**
     * 防止内存泄漏
     * @param skinChangeListener
     */
    public void unregister(ISkinChangeListener skinChangeListener) {
        mSkinViews.remove(skinChangeListener);
    }
}
