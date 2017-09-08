package com.example.kaifa.essayjoke.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.example.alex.framelibrary.skin.BaseSkinActivity;
import com.example.kaifa.essayjoke.R;
import com.example.kaifa.zlgxcmake.PatchUtils;
import com.zhbstudy.baselibrary.base.BaseActivity;

import org.reactivestreams.Subscriber;

import java.io.File;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ZLGXActivity extends BaseSkinActivity {

    private String patch_path = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "version_1.0_2.0.patch";
    private String mNewApkPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "version_2.0.apk";

    @Override
    protected void initData() {
        //1.访问后台接口，需不需要更新版本

        //2.需要更新版本，那么提示用户需要下载（腾讯视屏）
        //直接下载，然后提示用户更新

        if (!new File(patch_path).exists()) {
            return;
        }

        File newApk = new File(mNewApkPath);
        if (!newApk.exists()) {
            Toast.makeText(ZLGXActivity.this, "新的apk生成失败", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + mNewApkPath), "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

//        Observable observable = Observable.create(new ObservableOnSubscribe() {
//            @Override
//            public void subscribe(ObservableEmitter emitter) throws Exception {
//
//                //3.下载完差分包之后，调用我们的方法去合并生成新版本的apk
//                //是一个耗时操作---开线程+handler，asynctask,rxjava
//                //本地apk路径怎么来，已经被安装了1.0
//                //当前手机运行的app的apk路径
////                String apkpath = getPackageResourcePath();
////                PatchUtils.combine(apkpath, mNewApkPath, patch_path);
//                emitter.onNext(1);
//                emitter.onComplete();
//            }
//        });
//
//        Observer<Object> observer = new Observer<Object>() {
//            private Disposable mDisposable;
//
//            @Override
//            public void onSubscribe(Disposable d) {
//                mDisposable = d;
//            }
//
//            @Override
//            public void onNext(Object value) {
//                //4.需要校验签名  获取本地apk的签名，与新版本的apk作对比
//                //怎么获取2.0版本的签名  百度  灵感  系统安装apk时会进行校验，可以找系统安装apk的那个应用
//
//                //5.安装最新版本（安装apk）application/vnd.android.package-archive
//                File newApk = new File(mNewApkPath);
//                if (!newApk.exists()) {
//                    Toast.makeText(ZLGXActivity.this, "新的apk生成失败", Toast.LENGTH_SHORT).show();
//                    return;
//                }
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(Uri.parse("file://" + mNewApkPath), "application/vnd.android.package-archive");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            }
//
//            @Override
//            public void onError(Throwable e) {
//
//            }
//
//            @Override
//            public void onComplete() {
//
//            }
//        };
//        observable.subscribe(observer);
//        observable.subscribeOn(Schedulers.newThread()).observeOn(AndroidSchedulers.mainThread());


    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_zlgx);

    }

    public void startActivity(View view) {
    }
}
