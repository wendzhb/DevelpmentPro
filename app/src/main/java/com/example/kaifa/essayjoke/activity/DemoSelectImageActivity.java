package com.example.kaifa.essayjoke.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.alex.framelibrary.navigationbar.DefaultNavigationBar;
import com.example.kaifa.essayjoke.R;
import com.example.kaifa.essayjoke.adapter.RecycleAdapter;
import com.example.kaifa.essayjoke.imageselect.ImageSelector;
import com.example.kaifa.essayjoke.imageselect.SelectImageActivity;
import com.libjpeg.compress.ImageUtil;
import com.zhbstudy.baselibrary.base.BaseActivity;
import com.zhbstudy.baselibrary.ioc.OnClick;
import com.zhbstudy.baselibrary.ioc.ViewById;

import java.io.File;
import java.util.ArrayList;
import java.util.ListIterator;

public class DemoSelectImageActivity extends BaseActivity {

    private ArrayList<String> mImageList;

    @ViewById(R.id.rcy_image_list)
    private RecyclerView mRcyImageList;
    /****选择****/
    @ViewById(R.id.sure)
    private Button mSure;
    private RecycleAdapter adapter;

    @Override
    protected void initTitle() {
        DefaultNavigationBar navigationBar = new DefaultNavigationBar.Builder(this)
                .setTitle("图片选择器")
                .setRightText("测试")
                .builder();
    }


    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {
        mImageList = new ArrayList<>();
        adapter = new RecycleAdapter(mImageList, this);
        mRcyImageList.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        mRcyImageList.setAdapter(adapter);
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_demo_select_image);
    }

    @OnClick(R.id.sure)
    private void sureClick(Button sure) {
        ImageSelector.create().multi().count(9).showCamera(true).origin(mImageList).start(this, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (data != null) {
                    mImageList.clear();
                    mImageList.addAll(data.getStringArrayListExtra(SelectImageActivity.EXTRA_RESULT));
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public void comparess(View view) {
        // 采用Iterator的原因是for是线程不安全的，迭代器是线程安全的
//        for (String path : mImageList) {
        ListIterator<String> iterator = mImageList.listIterator();
        while(iterator.hasNext()){
            String path = iterator.next();
            //优化 bitmap decodeFile有可能会内存溢出
            //一般后台会规定尺寸     800；    微信  1280*960
            //Bitmap bitmap = BitmapFactory.decodeFile(s);
            //上传的时候可能会等待    for循环   最好用线程池（2-3）
            Bitmap bitmap = ImageUtil.decodeFile(path);
            ImageUtil.compressBitmap(bitmap, 50,
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + new File(path).getName());
        }
        Toast.makeText(this, "压缩完成", Toast.LENGTH_SHORT).show();
    }
}
