package com.encode;

import android.graphics.Bitmap;

/**
 * Created by zhb on 2019/5/16.
 */
public class AesMd5Encode {
    static {
        System.loadLibrary("native-lib");
    }

    public static native String getMd5(String origin);

    public static native String AesEncode(String src);
    public static native String AesDecode(String src);
    /**
     * 加密
     *
     * @param msg  原始字符串
     * @return 加密后的数据
     */
    public static native String Base64Encode(String msg );
    /**
     * 解密
     *
     * @param msg  解密前的 字符串
     * @return 解密后的数据
     */
    public static native String Base64Decode(String msg );


}
