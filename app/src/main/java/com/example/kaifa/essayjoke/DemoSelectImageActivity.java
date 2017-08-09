package com.example.kaifa.essayjoke;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.example.alex.framelibrary.navigationbar.DefaultNavigationBar;
import com.example.kaifa.essayjoke.imageselect.ImageSelector;
import com.example.kaifa.essayjoke.imageselect.SelectImageActivity;
import com.zhbstudy.baselibrary.base.BaseActivity;
import com.zhbstudy.baselibrary.ioc.OnClick;
import com.zhbstudy.baselibrary.ioc.ViewById;

import java.io.File;
import java.util.ArrayList;

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
        mSure.setText(stringFromJNI());
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
        for (String s : mImageList) {
            ImageUtil.compressBitmap(BitmapFactory.decodeFile(mImageList.get(0)), 30,
                    Environment.getExternalStorageDirectory().getAbsolutePath() +
                            File.separator + new File(s).getName());

        }

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}
