package com.example.alex.framelibrary.skin.attr;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.alex.framelibrary.skin.SkinManager;
import com.example.alex.framelibrary.skin.SkinResource;

/**
 * Created by zhb on 2017/7/6.
 * <p>
 * Descripte:
 */
public enum SkinType {

    TEXT_COLOR("textColor") {
        @Override
        public void skin(View view, String resName) {
            if (TextUtils.isEmpty(resName) || view == null) {
                return;
            }
            //获取资源设置
            SkinResource skinResource = getSkinResource();
            ColorStateList color = skinResource.getColorByName(resName);
            if (color == null || !(view instanceof TextView)) {
                return;
            }
            TextView textView = (TextView) view;
            textView.setTextColor(color);
        }
    }, BACKGROUND("background") {
        @Override
        public void skin(View view, String resName) {
            if (TextUtils.isEmpty(resName) || view == null) {
                return;
            }
            //背景可能是颜色也可能是图片
            SkinResource skinResource = getSkinResource();
            Log.e("SkinType", resName);

            Drawable drawable = skinResource.getDrawableByName(resName);

            if (drawable != null) {
                view.setBackground(drawable);
                return;

            }
            //可能是颜色
            ColorStateList color = skinResource.getColorByName(resName);
            if (color != null) {
                view.setBackgroundColor(color.getDefaultColor());
            }
        }
    }, SRC("src") {
        @Override
        public void skin(View view, String resName) {
            if (TextUtils.isEmpty(resName) || view == null) {
                return;
            }
            //背景可能是颜色也可能是图片
            SkinResource skinResource = getSkinResource();
            Drawable drawable = skinResource.getDrawableByName(resName);
            if (drawable != null || !(view instanceof ImageView)) {
                ImageView imageView = (ImageView) view;
                imageView.setImageDrawable(drawable);
                return;
            }
        }
    };

    public SkinResource getSkinResource() {
        return SkinManager.getInstance().getSkinResource();
    }

    //会根据名字调用对应的方法
    private String mResName;


    SkinType(String resName) {
        this.mResName = resName;
    }

    //抽象方法
    public abstract void skin(View view, String resName);

    public String getResName() {
        return mResName;
    }

    @Override
    public String toString() {
        return mResName;
    }
}
