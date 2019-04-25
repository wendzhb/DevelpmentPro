package com.example.kaifa.essayjoke.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.util.LruCache;


import com.example.kaifa.essayjoke.disklrucache.DiskLruCache;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by zhb on 2019/4/25.
 */
public class LruCacheUtils {

    private static LruCacheUtils lruCacheUtils;

    private DiskLruCache diskLruCache; //LRU磁盘缓存
    private LruCache<String, Bitmap> lruCache; //LRU内存缓存
    private Context context;

    private LruCacheUtils() {
    }

    public static LruCacheUtils getInstance() {
        if (lruCacheUtils == null) {
            lruCacheUtils = new LruCacheUtils();
        }
        return lruCacheUtils;
    }

    //获得采样比例
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //获取位图的圆宽高
        int width = options.outWidth;
        int height = options.outHeight;
        System.out.println("outWidth=" + width + "outHeight" + height);
        int inSampleSize = 1;
        if (width > reqWidth || height > reqHeight) {
            //判断原图的宽高,再进行匹配,按照小的来求采样比例
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        System.out.println("inSampleSize" + inSampleSize);
        return inSampleSize;
    }

    public Bitmap decodeSampleBitmapFromStream(byte[] bytes, int reqWidth, int reqHeight) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
    }

    //在清单文件中定义的versionCode
    private int getAppVersion() {

        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 1;
    }

    //获取缓存目录
    private File getCacheDir(String name) {
        //判断是不是SD卡  或者外部存储已经被删掉   前面一个是有SD卡(mnt/sdcard/Android/data/包名/cache)   后面一个没有SD卡(缓存在私有目录下data/data/包名/cache)
        String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED || !Environment.isExternalStorageRemovable()
                ? context.getExternalCacheDir().getPath() : context.getCacheDir().getPath();
        return new File(cachePath + File.separator + name);

    }

    //计算MD5的字符串摘要
    public String hashKeyForDisk(String key) {
        String cacheKey;
        try {
            final MessageDigest mDigest = MessageDigest.getInstance("MD5");
            mDigest.update(key.getBytes());
            cacheKey = bytesToHexString(mDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            cacheKey = String.valueOf(key.hashCode());
        }
        return cacheKey;
    }

    public String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(0xFF & bytes[i]);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    //添加缓存
    public void addBitmapToCache(String url, Bitmap bitmap) {
        String key = hashKeyForDisk(url);
        if (getBitmapFromCache(key) == null) {
            System.out.println("key=====" + key);
            System.out.println("bitmap====" + bitmap);
            lruCache.put(key, bitmap);
        }
    }

    //读取缓存
    public Bitmap getBitmapFromCache(String url) {
        String key = hashKeyForDisk(url);
        return lruCache.get(key);
    }

    public void open(Context context, String disk_cache_subdir, int disk_cache_size) {
        this.context = context;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        final int cacheSize = memoryClass / 8 * 1024 * 1024;   //单位大小为字节  八分之一的内存作为缓存大小
        lruCache = new LruCache<>(cacheSize);
        try {
            diskLruCache = DiskLruCache.open(getCacheDir(disk_cache_subdir), getAppVersion(), 1, disk_cache_size);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putCache(final String url, final CallBack callBack) {
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                String key = hashKeyForDisk(params[0]);
                System.out.println("key=" + key);
                DiskLruCache.Editor editor = null;
                Bitmap bitmap = null;
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(1000 * 30);
                    conn.setConnectTimeout(1000 * 30);
                    ByteArrayOutputStream baos = null;
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
                        baos = new ByteArrayOutputStream();
                        byte[] bytes = new byte[1024];
                        int len = -1;
                        while ((len = bis.read(bytes)) != -1) {
                            baos.write(bytes, 0, len);
                        }
                        bis.close();
                        baos.close();
                        conn.disconnect();
                    }
                    if (baos != null) {
                        bitmap = decodeSampleBitmapFromStream(baos.toByteArray(), 100, 100);
                        //加入缓存
                        addBitmapToCache(params[0], bitmap);
                        //加入磁盘缓存
                        editor = diskLruCache.edit(key);
                        System.out.println(url.getFile());
                        //位图压缩后输出(参数:压缩格式,质量(100表示不压缩,30表示压缩70%),输出流)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, editor.newOutputStream(0));
                        editor.commit();
                    }
                } catch (IOException e) {
                    try {
                        editor.abort();  //放弃写入
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                    e.printStackTrace();
                }
                return bitmap;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                callBack.response(bitmap);
            }
        }.execute(url);

    }

    //回调接口
    public interface CallBack<T> {
        public void response(T entity);
    }

    //获取磁盘缓存
    public InputStream getDiskCache(String url) {
        String key = hashKeyForDisk(url);
        System.out.println("getDiskCache=" + key);
        try {
            //快照
            DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
            System.out.println(snapshot);
            if (snapshot != null) {
                return snapshot.getInputStream(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //关闭磁盘缓存
    public void close() {
        if (diskLruCache != null && !diskLruCache.isClosed()) {
            try {
                diskLruCache.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //刷新磁盘缓存
    public void flush() {
        if (diskLruCache != null) {
            try {
                diskLruCache.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
