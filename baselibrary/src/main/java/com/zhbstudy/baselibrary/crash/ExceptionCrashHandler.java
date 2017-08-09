package com.zhbstudy.baselibrary.crash;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 2017/6/1.
 * <p>
 * Descripte:单例的设计模式的异常捕捉类
 */
public class ExceptionCrashHandler implements Thread.UncaughtExceptionHandler {
    private Context mContext;
    private static ExceptionCrashHandler mInstance;
    private final static String TAG = "ExceptionCrashHandler";
    private Thread.UncaughtExceptionHandler mDefaultExceptionHandler;

    public static ExceptionCrashHandler getInstance() {
        if (mInstance == null) {
            //用来解决多并发的问题
            synchronized (ExceptionCrashHandler.class) {
                if (mInstance == null) {
                    mInstance = new ExceptionCrashHandler();
                }
            }
        }
        return mInstance;
    }

    //用来获取应用的一些信息
    public void init(Context context) {
        this.mContext = context;
        //设置全局的异常为本类
        Thread.currentThread().setUncaughtExceptionHandler(this);
        mDefaultExceptionHandler = Thread.currentThread().getDefaultUncaughtExceptionHandler();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //全局异常
        Log.e(TAG, "报异常了");

        //写入到本地文件   ex  当前的版本  手机信息
        String crashFileName = saveInfoToSD(e);
        Log.e(TAG, "fileName-->" + crashFileName);

        cacheCrashFile(crashFileName);

        //系统默认处理
        mDefaultExceptionHandler.uncaughtException(t, e);
    }


    /**
     * 保存获取的软件信息 设备信息 和 错误信息到SD卡中
     *
     * @param throwable
     * @return
     */
    private String saveInfoToSD(Throwable throwable) {
        String fileName = null;
        StringBuffer sb = new StringBuffer();

        //1.手机信息 应用信息 包名 版本号
        for (Map.Entry<String, String> entry : obtainSimpleInfo(mContext).entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        Log.e(TAG, "obtainSimpleInfo");


        //2.崩溃的详细信息
        sb.append(obtainExceptionInfo(throwable));
        Log.e(TAG, "obtainExceptionInfo");


        //3.保存当前文件，等应用再次启动再上传（上传文件不在这里处理）

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //获取手机的目录，并没有拿手机sd卡目录 6.0以上动态申请权限
            File dir = new File(mContext.getFilesDir() + File.separator + "crash" + File.separator);


            //先删除之前的异常信息
            //删除该目录下的所有子文件
            if (dir.exists()) {
                deleteDir(dir);
            }
            //再重新创建文件夹
            if (!dir.exists()) {
                dir.mkdir();
            }

            try {
                fileName = dir.toString() + File.separator + getAssignTime("yyyy_MM_dd_HH_mm") + ".txt";
                Log.e(TAG, "fileName :" + fileName);

                FileOutputStream fos = new FileOutputStream(fileName);
                fos.write(sb.toString().getBytes());
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return fileName;
    }

    /**
     * 缓存崩溃日志文件
     *
     * @param crashFileName
     */
    private void cacheCrashFile(String crashFileName) {
        SharedPreferences sp = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE);
        sp.edit().putString("CRASH_FILE_NAME", crashFileName).commit();
    }

    /**
     * 获取一些简单的信息，软件版本，手机版本，型号等信息放在hashmap中
     *
     * @param mContext
     * @return
     */
    private HashMap<String, String> obtainSimpleInfo(Context mContext) {
        HashMap<String, String> map = new HashMap<>();
        PackageManager mPackageManager = mContext.getPackageManager();
        PackageInfo mPackageInfo = null;
        try {
            mPackageInfo = mPackageManager.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        map.put("versionName", mPackageInfo.versionName);
        map.put("versionCode", mPackageInfo.versionCode + "");
        map.put("MODEL", Build.MODEL);
        map.put("SDK_INT", Build.VERSION.SDK_INT + "");
        map.put("PRODUCT", Build.PRODUCT);
        map.put("MOBLE_INFO", getMobileInfo());
        return map;
    }

    /**
     * 获取系统未捕捉的错误信息
     *
     * @param throwable
     * @return
     */
    private String obtainExceptionInfo(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        throwable.printStackTrace(printWriter);
        printWriter.close();
        return stringWriter.toString();
    }

    /**
     * 获取手机信息
     *
     * @return
     */
    private String getMobileInfo() {
        StringBuffer sb = new StringBuffer();
        try {
            //利用反射获取这个类的所有属性
            Field[] fields = Build.class.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                String name = field.getName();
                String value = field.get(null).toString();
                sb.append(name + "=" + value);
                sb.append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param dir
     * @return
     */
    private boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            //递归删除目录下的子目录
            for (File child : children) {
                child.delete();
            }
        }
        //目录此时为空，可以删除
        return true;
    }

    private String getAssignTime(String dateFormatStr) {
        DateFormat dateFormat = new SimpleDateFormat(dateFormatStr);
        long currentTime = System.currentTimeMillis();
        return dateFormat.format(currentTime);
    }

    /**
     * 获取崩溃文件名称
     * @return
     */
    public File getCrashFile() {
        String crashFileName = mContext.getSharedPreferences("crash", Context.MODE_PRIVATE).getString("CRASH_FILE_NAME", "");

        return new File(crashFileName);
    }
}
