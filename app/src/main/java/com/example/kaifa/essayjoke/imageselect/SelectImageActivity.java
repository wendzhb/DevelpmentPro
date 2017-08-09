package com.example.kaifa.essayjoke.imageselect;


import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.alex.framelibrary.skin.BaseSkinActivity;
import com.example.alex.framelibrary.util.StatusBarUtil;
import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.ioc.OnClick;
import com.zhbstudy.baselibrary.ioc.ViewById;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhb on 2017/7/10.
 * <p>
 * Descripte:图片选择activity
 */
public class SelectImageActivity extends BaseSkinActivity implements View.OnClickListener, SelectImageListener {
    //传过来的Key
    //选择图片的模式-多选
    public static final int MODE_MULTI = 0x0011;
    //选择图片的模式-单选
    public static final int MODE_SINGLE = 0x0012;
    //是否显示相机的EXTRA_KEY
    public static final String EXTRA_SHOW_CAMERA = "EXTRA_SHOW_CAMERA";
    //总共可以选择多少照片的EXTRA_KEY
    public static final String EXTRA_SELECT_COUNT = "EXTRA_SELECT_COUNT";
    //原始图片的路径的EXTRA_KEY
    public static final String EXTRA_DEFAULT_SELECT_LIST = "EXTRA_DEFAULT_SELECT_LIST";
    //选择模式的EXTRA_KEY
    public static final String EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE";
    //返回选择图片列表的EXTRA_KEY
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    //加载所有的数据
    public static final int LOADER_TYPE = 0x0021;
    /*****************
     * 获取传递过来的参数
     *****************/
    //单选或者多选，int类型的type
    private int mMode = MODE_MULTI;
    //boolean类型是否显示拍照按钮
    private boolean mShowCamera = true;
    //int 类型的图片张数
    private int mMaxCount = 8;
    //ArrayList<String>已经选择好的图片
    private ArrayList<String> mResultList;
    @ViewById(R.id.rcy_image)
    private RecyclerView mRcyImage;
    /****预览****/
    @ViewById(R.id.tv_image)
    private TextView mTvImage;
    /****0/9****/
    @ViewById(R.id.tv_num)
    private TextView mTvNum;
    /****确定****/
    @ViewById(R.id.btn_image)
    private TextView mBtnImage;

    File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
            + File.separator + "temp.jpg");


    @Override
    protected void initData() {
        //1.获取传递过来的参数
        Intent intent = getIntent();
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, mMode);
        mMaxCount = intent.getIntExtra(EXTRA_SELECT_COUNT, mMaxCount);
        mShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, mShowCamera);
        mResultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECT_LIST);

        if (mResultList == null) {
            mResultList = new ArrayList<>();
        }

        //2.初始化本地图片数据
        initImageList();
        //3.改变数据显示
        exchangeViewShow();
    }

    //改变布局显示 需要及时更新
    private void exchangeViewShow() {
        //预览是否可以点击，显示是什么颜色
        if (mResultList.size() > 0) {
            //至少选择了一张
            mTvImage.setEnabled(true);
        } else {
            //一张都没选
            mTvImage.setEnabled(false);

        }
        //中间图片的张数
        mTvNum.setText(mResultList.size() + "/" + mMaxCount);
    }

    /**
     * 2.ContentProvide获取内存中所有图片
     */
    private void initImageList() {
        //耗时操作，开线程，asynctask
        //int id 查询全部---用来区分的
        getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallback);

    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //查询数据
            CursorLoader cursorLoader = new CursorLoader(SelectImageActivity.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR " + IMAGE_PROJECTION[3] + "=? ",
                    new String[]{"image/jpeg", "image/png"},
                    IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //解析封装到集合   这里暂时先只保存路径

            if (data != null && data.getCount() > 0) {
                ArrayList<ImageEntity> imageEntities = new ArrayList<>();
                ArrayList<String> images = new ArrayList<>();
                //如果需要显示拍照，就在第一个位置加一个空String
                if (mShowCamera) {
                    images.add("");
                }
                //遍历循环
                while (data.moveToNext()) {
                    String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                    String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                    long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                    Log.e("tag", "path = " + path + ",name = " + name + ",dateTime = " + dateTime);
//                    //判断文件是不是存在
//                    if (!pathExist(path)) {
//                        continue;
//                    }
                    Log.e("tag", "path = " + path + ",name = " + name + ",dateTime = " + dateTime);
                    //封装数据对象
                    ImageEntity image = new ImageEntity(path, name, dateTime);
                    imageEntities.add(image);
                    //只保存路径
                    images.add(path);
                }
                //显示列表数据
//                showListData(imageEntities);
                showImageList(images);
            }

        }


        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    /**
     * 显示图片列表
     *
     * @param images
     */
    private void showImageList(ArrayList<String> images) {
        SelectImageListAdapter adapter = new SelectImageListAdapter(images, this, mResultList, mMaxCount);
        adapter.setOnSelectImageListener(this);
        mRcyImage.setLayoutManager(new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false));
        mRcyImage.setAdapter(adapter);
    }

    /**
     * 判断文件是否存在
     *
     * @param path
     * @return
     */
    private boolean pathExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    @Override
    protected void initView() {
        mTvImage.setOnClickListener(this);
    }

    @Override
    protected void initTitle() {
//        DefaultNavigationBar navigationBar = new DefaultNavigationBar.Builder(this)
//                .setTitle("所有图片").builder();
        StatusBarUtil.statusBarTinColor(this, Color.BLACK);
        //改变状态栏颜色
//        StatusBarUtil.statusBarTinColor(this, Color.parseColor("#261f1f"));
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_image_select);
    }

    @OnClick(R.id.tv_image)
    private void tvImageClick(TextView tvImage) {
    }

    //确定
    @OnClick(R.id.btn_image)
    private void btnImageClick() {
        //选择好的图片传过去

        Intent intent = new Intent();
        intent.putStringArrayListExtra(EXTRA_RESULT, mResultList);
        setResult(RESULT_OK, intent);

        finish();
    }

    @Override
    public void onClick(View v) {
        //图片预览

    }

    @Override
    public void select() {
        exchangeViewShow();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //1.第一个把图片加入集合
        //2.调用确定方法
        //3.通知系统本地有图片改变了，下次进来可以找到这张图片
        if (resultCode == RESULT_OK) {
            if (requestCode == 3) {
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                        Uri.fromFile(tempFile)));
                mResultList.add(tempFile.getAbsolutePath());
                btnImageClick();
            }
        }

    }
}
