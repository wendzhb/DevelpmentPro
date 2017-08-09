package com.example.alex.framelibrary.skin.attr;

import android.view.View;

import java.util.List;

/**
 * Created by zhb on 2017/7/6.
 * <p>
 * Descripte:
 */
public class SkinView {
    private View mView;

    private List<SkinAttr> mAttrs;

    public SkinView(View view, List<SkinAttr> skinAttrs) {
        this.mView = view;
        this.mAttrs = skinAttrs;
    }

    public void skin() {
        for (SkinAttr mAttr : mAttrs) {
            mAttr.skin(mView);
        }
    }

}
