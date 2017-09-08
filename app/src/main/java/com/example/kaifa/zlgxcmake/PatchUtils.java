package com.example.kaifa.zlgxcmake;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by zhb on 2017/8/23.
 */

public class PatchUtils {

    private final static String TAG = "PatchUtils.class";

    static {
        System.loadLibrary("bspatch");
    }


    /**
     * @param oldAPKPath 原来的apk  1.0
     * @param newAPKPath 合并后的新的apk路径 需要生成的2.0
     * @param patchPath  差分包路径，从服务器上下载下来的
     */
    public static native void combine(String oldAPKPath, String newAPKPath, String patchPath);

    /**
     * 获取当前apk的签名
     *
     * @param context
     * @return
     */
    public static String getSignature(Context context) {
        // 通过Context获取当前包名
        String currentApkPackageName = context.getApplicationInfo().packageName;

        Log.e(TAG, "TAG -> " + currentApkPackageName);

        // 通过PackageManager获取所有应用的PackageInfo信息
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);
        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(currentApkPackageName)) {
                // 获取签名
                return packageInfo.signatures[0].toCharsString();
            }
        }
        return null;
    }


    /**
     * 获取该目录下的apk签名
     *
     * @param path 当前apk路径
     * @return
     */
    public static String getSignature(String path) throws Exception {
        // 1.反射实例化PackageParser对象
        Object packageParser = getPackageParser(path);

        // 2.反射获取parsePackage方法
        Object packageObject = getPackageInfo(path, packageParser);

        // 3.调用collectCertificates方法
        Method collectCertificatesMethod = packageParser.getClass().
                getDeclaredMethod("collectCertificates", packageObject.getClass(), int.class);
        collectCertificatesMethod.invoke(packageParser, packageObject, 0);

        // 4.获取mSignatures属性
        Field signaturesField = packageObject.getClass().getDeclaredField("mSignatures");
        signaturesField.setAccessible(true);
        Signature[] mSignatures = (Signature[]) signaturesField.get(packageObject);
        return mSignatures[0].toCharsString();
    }

    /**
     * 创建PackageParser.Package类，兼容5.0
     *
     * @param path
     * @return
     * @throws Exception
     */
    private static Object getPackageInfo(String path, Object packageParser) throws Exception {
        if (Build.VERSION.SDK_INT >= 21) {
            Class<?>[] paramClass = new Class[2];
            paramClass[0] = File.class;
            paramClass[1] = int.class;
            Method parsePackageMethod = packageParser.getClass().getDeclaredMethod("parsePackage", paramClass);

            // 3.反射执行parsePackage方法
            Object[] paramObject = new Object[2];
            paramObject[0] = new File(path);
            paramObject[1] = 0;
            parsePackageMethod.setAccessible(true);
            return parsePackageMethod.invoke(packageParser, paramObject);
        } else {
            Class<?>[] paramClass = new Class[4];
            paramClass[0] = File.class;
            paramClass[1] = String.class;
            paramClass[2] = DisplayMetrics.class;
            paramClass[3] = int.class;
            Method parsePackageMethod = packageParser.getClass().getDeclaredMethod("parsePackage", paramClass);

            // 3.反射执行parsePackage方法
            Object[] paramObject = new Object[4];
            paramObject[0] = new File(path);
            paramObject[1] = path;
            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            paramObject[2] = metrics;
            paramObject[3] = 0;
            parsePackageMethod.setAccessible(true);
            return parsePackageMethod.invoke(packageParser, paramObject);
        }
    }

    /**
     * 创建PackageParser类
     *
     * @param path
     * @return
     * @throws Exception
     */
    private static Object getPackageParser(String path) throws Exception {
        Class<?> packageParserClazz = Class.forName("android.content.pm.PackageParser");
        // 版本兼容
        if (Build.VERSION.SDK_INT >= 21) {
            Constructor<?> packageParserConstructor = packageParserClazz.getDeclaredConstructor();
            return packageParserConstructor.newInstance();
        } else {
            Constructor<?> packageParserConstructor = packageParserClazz.getDeclaredConstructor(String.class);
            return packageParserConstructor.newInstance(path);
        }
    }

}
