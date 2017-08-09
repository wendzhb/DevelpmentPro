package com.zhbstudy.baselibrary.fixBug;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.BaseDexClassLoader;

import static android.R.attr.path;

/**
 * Created by alex on 2017/6/3.
 * <p>
 * Descripte:热修复
 */
public class FixDexManager {
    private static final String TAG = "FixDexManager";
    private Context mContext;
    private File mDexDir;

    public FixDexManager(Context context) {
        this.mContext = context;
        //获取系统能够访问的dex目录
        this.mDexDir = context.getDir("odex",Context.MODE_PRIVATE);
    }

    /**
     * 修复Dex包
     *
     * @param fixDexPath
     */
    public void fixDex(String fixDexPath) throws Exception {
        //1.先获取已经运行的dexElement;
        ClassLoader applicationClassLoader = mContext.getClassLoader();

        Object applicationDexElements = getDexElementsByClassLoader(applicationClassLoader);

        //2.获取下载好的补丁的dexElement;
        //2.1.移动到系统能够访问的  dex目录下  ClassLoader
        File srcFile = new File(fixDexPath);

        if (!srcFile.exists()) {
            throw  new FileNotFoundException(fixDexPath);
        }

        File destFile = new File(mDexDir,srcFile.getName());

        if (destFile.exists()) {
            Log.d(TAG, "patch [" + fixDexPath + "] has be loaded.");
            return;
        }

        copyFile(srcFile,destFile);

        //2.2.ClassLoader读取fixDex路径  为什么加入到集合 因为一启动可能就要修复 BaseApplication
        List<File> fixDexFiles = new ArrayList<>();
        fixDexFiles.add(destFile);

        fixDexFiles(fixDexFiles);
    }



    /**
     * 从classloader中获取dexElements
     *
     * @param classLoader
     * @return
     */
    private Object getDexElementsByClassLoader(ClassLoader classLoader) throws Exception {
        //1.先获取 pathList
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        //IOC 熟悉反射
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(classLoader);

        //2.pathList里面的dexElements
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);
        Object dexElements = dexElementsField.get(pathList);
        return dexElements;
    }

    /**
     *
     * copy file
     *
     * @param src
     *            source file
     * @param dest
     *            target file
     * @throws IOException
     */
    public static void copyFile(File src, File dest) throws IOException {
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            if (!dest.exists()) {
                dest.createNewFile();
            }
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dest).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            if (inChannel != null) {
                inChannel.close();
            }
            if (outChannel != null) {
                outChannel.close();
            }
        }
    }

    /**
     * 合并两个dexElements数组
     *
     * @param arrayLhs
     * @param arrayRhs
     * @return
     */
    private static Object combineArray(Object arrayLhs, Object arrayRhs) {
        Class<?> localClass = arrayLhs.getClass().getComponentType();
        int i = Array.getLength(arrayLhs);
        int j = i + Array.getLength(arrayRhs);
        Object result = Array.newInstance(localClass, j);
        for (int k = 0; k < j; ++k) {
            if (k < i) {
                Array.set(result, k, Array.get(arrayLhs, k));
            } else {
                Array.set(result, k, Array.get(arrayRhs, k - i));
            }
        }
        return result;
    }

    /**
     * 把dexElements注入到classLoader中
     * @param classLoader
     * @param dexElements
     */
    private void injectDexElements(ClassLoader classLoader, Object dexElements) throws Exception {
        //1.先获取 pathList
        Field pathListField = BaseDexClassLoader.class.getDeclaredField("pathList");
        //IOC 熟悉反射
        pathListField.setAccessible(true);
        Object pathList = pathListField.get(classLoader);

        //2.pathList里面的dexElements
        Field dexElementsField = pathList.getClass().getDeclaredField("dexElements");
        dexElementsField.setAccessible(true);

        dexElementsField.set(pathList, dexElements);
    }

    /**
     * 加载全部的修复包
     */
    public void loadFixDex() throws Exception {
        File[] dexFiles = mDexDir.listFiles();
        List<File> fixDexFiles = new ArrayList<>();
        for (File dexFile : dexFiles) {
            if (dexFile.getName().endsWith(".dex")){
                fixDexFiles.add(dexFile);
            }
        }
        fixDexFiles(fixDexFiles);
    }

    /**
     * 修复dex
     * @param fixDexFiles
     * @throws Exception
     */
    private void fixDexFiles(List<File> fixDexFiles) throws Exception {
        //1.先获取已经运行的dexElement;
        ClassLoader applicationClassLoader = mContext.getClassLoader();

        Object applicationDexElements = getDexElementsByClassLoader(applicationClassLoader);

        File optimizedDirectory = new File(mDexDir,"odex");
        if (!optimizedDirectory.exists()) {
            optimizedDirectory.mkdirs();
        }

        //修复
        for (File fixDexFile : fixDexFiles) {
            /**
             * dexPath: dex路径
             * optimizedDirectory:解压路径
             * librarySearchPath:.so文件位置
             * parent:父ClassLoader
             */
            ClassLoader fixDexClassLoader = new BaseDexClassLoader(
                    fixDexFile.getAbsolutePath(),//dex路径  必须要在应用目录下的odex文件中
                    optimizedDirectory,//解压路径
                    null,//.so文件位置
                    applicationClassLoader//父ClassLoader
            );

            Object fixDexElements = getDexElementsByClassLoader(fixDexClassLoader);

            //3.把补丁的dexElement 插入到已经运行的dexElement 的最前面 合并

            //3.1.dexElements 数组 合并 fixDexElements 数组
            //合并完成
            applicationDexElements = combineArray(fixDexElements, applicationDexElements);
        }

        //3.2.把合并的数组注入到原来的类中applicationClassLoader
        injectDexElements(applicationClassLoader,applicationDexElements);
    }
}
