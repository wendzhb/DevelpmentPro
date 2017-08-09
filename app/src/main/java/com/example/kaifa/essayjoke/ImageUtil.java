package com.example.kaifa.essayjoke;


import android.graphics.Bitmap;

/**
 * Created by zhb on 2017/8/4.
 * <p>
 * Descripte:
 */
public class ImageUtil {

    static {
        System.loadLibrary("native-lib");
    }
    public native static int compressBitmap(Bitmap bitmap, int quality, String fileName);
}
