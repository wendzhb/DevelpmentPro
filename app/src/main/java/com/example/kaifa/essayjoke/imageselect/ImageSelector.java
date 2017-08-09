package com.example.kaifa.essayjoke.imageselect;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.ArrayList;

/**
 * Created by zhb on 2017/7/13.
 * <p>
 * Descripte:图片选择的链式调用  为了保障框架的封装性，不要暴露太多
 */
public class ImageSelector {
    //最多可以选择多少张图片 默认八张
    private int mMaxCount = 9;
    //选择图片的模式
    private int mMode = SelectImageActivity.MODE_MULTI;
    //是否显示相机
    private boolean mShowCamera = true;
    //原始的图片
    private ArrayList<String> mOriginData;

    private ImageSelector() {
    }

    public static ImageSelector create() {
        return new ImageSelector();
    }

    /**
     * 单选
     *
     * @return
     */
    public ImageSelector single() {
        mMode = SelectImageActivity.MODE_SINGLE;
        return this;
    }

    /**
     * 多选
     *
     * @return
     */
    public ImageSelector multi() {
        mMode = SelectImageActivity.MODE_MULTI;
        return this;
    }

    /**
     * 最大数量
     *
     * @param count
     * @return
     */
    public ImageSelector count(int count) {
        mMaxCount = count;
        return this;
    }

    /**
     * 是否拍照
     *
     * @param showCamera
     * @return
     */
    public ImageSelector showCamera(boolean showCamera) {
        mShowCamera = showCamera;
        return this;
    }

    /**
     * 选中的图片集合
     *
     * @param origin
     * @return
     */
    public ImageSelector origin(ArrayList<String> origin) {
        mOriginData = origin;
        return this;
    }

    public void start(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, SelectImageActivity.class);
        addParamsByIntent(intent);
        activity.startActivityForResult(intent, requestCode);
    }

    public void start(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getActivity(), SelectImageActivity.class);
        addParamsByIntent(intent);
        fragment.startActivityForResult(intent, requestCode);
    }

    private void addParamsByIntent(Intent intent) {
        intent.putExtra(SelectImageActivity.EXTRA_SELECT_COUNT, mMaxCount);
        intent.putExtra(SelectImageActivity.EXTRA_SELECT_MODE, SelectImageActivity.MODE_MULTI);
        if (mOriginData != null && mMode == SelectImageActivity.MODE_MULTI) {
            intent.putStringArrayListExtra(SelectImageActivity.EXTRA_DEFAULT_SELECT_LIST, mOriginData);
        }
        intent.putExtra(SelectImageActivity.EXTRA_SHOW_CAMERA, mShowCamera);
    }

}
