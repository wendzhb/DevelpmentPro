package com.example.kaifa.essayjoke.imageselect;

import android.text.TextUtils;

/**
 * Created by zhb on 2017/7/11.
 * <p>
 * Descripte:
 */
public class ImageEntity {
    public String path;
    public String name;
    public long time;

    public ImageEntity(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ImageEntity) {
            ImageEntity compare = (ImageEntity) obj;
            return TextUtils.equals(this.path, compare.path);
        }
        return false;
    }
}
