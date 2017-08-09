package com.example.alex.framelibrary.skin.attr;

import android.view.View;

import java.util.List;

/**
 * Created by zhb on 2017/7/6.
 * <p>
 * Descripte:
 */
public class SkinAttr {
    private String mResName;
    private SkinType mType;

    public SkinAttr(String resName, SkinType skinType) {
        this.mResName = resName;
        this.mType = skinType;
    }

    public void skin(View mView) {
        mType.skin(mView, mResName);
    }

}
