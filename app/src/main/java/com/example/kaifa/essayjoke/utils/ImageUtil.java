package com.example.kaifa.essayjoke.utils;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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

    public native static void cryptFile(String filePath,String cryptPath);
    public static Bitmap decodeFile(String path) {
        int finalWidth = 320;
        //先获取宽带
        BitmapFactory.Options options = new BitmapFactory.Options();
        //不加载图片到内存只拿宽高
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path,options);

        int bitmapWidth = options.outWidth;
        int inSampleSize = 1;
        if (bitmapWidth>finalWidth){
            inSampleSize = bitmapWidth/finalWidth;
        }
        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(path,options);
    }
}
