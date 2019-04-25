package com.example.kaifa.essayjoke.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.base.BaseActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


/**
 * 相机界面
 * Created by zhb on 2015/11/11.
 */
public class ActivityCamera extends BaseActivity implements SurfaceHolder.Callback, Camera.PictureCallback, View.OnClickListener, Camera.PreviewCallback {

    public static final String TAG = "ActivityCamera";

    TextView cameratakephpto;
    TextView cameracancle;
    TextView cameratitle;


    private SurfaceView surfaceView;
    private SurfaceHolder holder;
    private Camera camera;
    private int cameraPosition = 0;//0代表前置摄像头,1代表后置摄像头,默认打开前置摄像头
    private int cameraCount = 0;//获得相机的摄像头数量

    public static File cacheDir;//照片路径

    private Display display;


    @Override
    protected void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void initView() {
        cameratitle = (TextView) findViewById(R.id.camera_title);
        cameratakephpto = (TextView) findViewById(R.id.camera_take_phpto);
        cameracancle = (TextView) findViewById(R.id.camera_cancle);

        //初始化surfaceview预览效果,照相机预览的空间
        surfaceView = (SurfaceView) findViewById(R.id.camera_view);
        holder = surfaceView.getHolder();//获得句柄
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//surfaceview不维护自己的缓冲区，等待屏幕渲染引擎将内容推送到用户面前
        holder.setKeepScreenOn(true);// 屏幕常亮
        holder.addCallback(this);// 为SurfaceView的句柄添加一个回调函数

        cameratakephpto.setOnClickListener(this);
        cameracancle.setOnClickListener(this);

        cameratitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //交换摄像头
                changeCamera();
            }
        });
    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_camera);
        cacheDir = new File(this.getExternalCacheDir().getAbsolutePath());
        cameraCount = Camera.getNumberOfCameras();//得到摄像头的个数
        // 获取屏幕信息
        WindowManager mManger = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        display = mManger.getDefaultDisplay();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera_take_phpto://拍照
                //注：调用takePicture()方法进行拍照是传入了一个PictureCallback对象——当程序获取了拍照所得的图片数据之后,PictureCallback对象将会被回调，该对象可以负责对相片进行保存或传入网络
                if (camera != null) {
                    camera.takePicture(null, null, this);
                }
                break;
            case R.id.camera_cancle://取消
                finish();
                break;
        }
    }


    /**
     * 开始拍照时调用该方法
     * 2.3以后支持多摄像头，所以开启前可以通过getNumberOfCameras先获取摄像头数目，再通过 getCameraInfo得到需要开启的摄像头id，然后传入Open函数开启摄像头
     *
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            int CammeraIndex = findFrontCamera();
            if (CammeraIndex == -1) {
                CammeraIndex = findBackCamera();
            }
            camera = Camera.open(CammeraIndex);// 打开摄像头
            camera.setPreviewDisplay(holder); // 设置用于显示拍照影像的SurfaceHolder对象
            camera.setPreviewCallback(this);
            camera.setDisplayOrientation(90);
            camera.startPreview(); // 开始预览
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 拍照状态变化时调用该方法
     *
     * @param holder
     * @param format
     * @param width
     * @param height
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        setParameter();
        camera.startPreview();
    }

    /**
     * 停止拍照时调用该方法
     *
     * @param holder
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.release(); // 释放照相机
            camera = null;
        }
    }

    /**
     * 将bitmap转换成btye[]
     *
     * @param bitmap
     * @return
     */
    public static byte[] bitmapToByte(Bitmap bitmap) {

        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                return bitmapBytes;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 拍照完成的回调
     *
     * @param data
     * @param camera
     */
    @Override
    public void onPictureTaken(final byte[] data, Camera camera) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //将照片改为竖直方向
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    Log.e("tag", "bitmap == null?" + (bitmap == null ? true : false));

                    Matrix matrix = new Matrix();
                    switch (cameraPosition) {
                        case 0://前
                            matrix.preRotate(270);
                            break;
                        case 1:
                            matrix.preRotate(90);
                            break;
                    }

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    String path = saveToSDCard(bitmapToByte(bitmap)); // 保存图片到sd卡中
                    //数据是使用Intent返回
                    Intent intent = new Intent();
                    //把返回数据存入Intent
                    intent.putExtra("path", path);
                    //设置返回数据
                    ActivityCamera.this.setResult(RESULT_OK, intent);
                    //关闭Activity
                    ActivityCamera.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 设置照片格式
     */
    private void setParameter() {
        Camera.Parameters parameters = camera.getParameters(); // 获取各项参数

        parameters.setPictureFormat(PixelFormat.JPEG); // 设置图片格式
        parameters.setJpegQuality(100); // 设置照片质量
        //获得相机支持的照片尺寸,选择合适的尺寸
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
        int maxSize = Math.max(display.getWidth(), display.getHeight());
        int length = sizes.size();
        if (maxSize > 0) {
            for (int i = 0; i < length; i++) {
                if (maxSize <= Math.max(sizes.get(i).width, sizes.get(i).height)) {
                    Log.e("tag", "11" + sizes.get(i).width + sizes.get(i).height + "");

                    parameters.setPictureSize(sizes.get(i).width, sizes.get(i).height);
                    break;
                }
            }
        }
        List<Camera.Size> ShowSizes = parameters.getSupportedPreviewSizes();
        int showLength = ShowSizes.size();
        if (maxSize > 0) {
            for (int i = 0; i < showLength; i++) {
                if (maxSize <= Math.max(ShowSizes.get(i).width, ShowSizes.get(i).height)) {
                    Log.e("tag", ShowSizes.get(i).width + ShowSizes.get(i).height + "");
                    parameters.setPreviewSize(ShowSizes.get(i).width, ShowSizes.get(i).height);
                    break;
                }
            }
        }
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        camera.setParameters(parameters);
    }

    /**
     * 改变摄像头
     */
    private void changeCamera() {
        //切换前后摄像头
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        for (int i = 0; i < cameraCount; i++) {
            Camera.getCameraInfo(i, cameraInfo);//得到每一个摄像头的信息
            if (cameraPosition == 1) {
                //现在是后置，变更为前置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                        camera.setDisplayOrientation(getPreviewDegree(ActivityCamera.this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setParameter();
                    camera.startPreview();//开始预览
                    cameraPosition = 0;
                    break;
                }
            } else {
                //现在是前置， 变更为后置
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {//代表摄像头的方位，CAMERA_FACING_FRONT前置      CAMERA_FACING_BACK后置
                    camera.stopPreview();//停掉原来摄像头的预览
                    camera.release();//释放资源
                    camera = null;//取消原来摄像头
                    camera = Camera.open(i);//打开当前选中的摄像头
                    try {
                        camera.setPreviewDisplay(holder);//通过surfaceview显示取景画面
                        camera.setDisplayOrientation(getPreviewDegree(ActivityCamera.this));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    setParameter();
                    camera.startPreview();//开始预览
                    cameraPosition = 1;
                    break;
                }
            }
        }
    }

    /**
     * 查找前置摄像头
     *
     * @return
     */
    private int findFrontCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    /**
     * 查找后置摄像头
     *
     * @return
     */
    private int findBackCamera() {
        int cameraCount = 0;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras(); // get cameras number

        for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
            Camera.getCameraInfo(camIdx, cameraInfo); // get camerainfo
            if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                // 代表摄像头的方位，目前有定义值两个分别为CAMERA_FACING_FRONT前置和CAMERA_FACING_BACK后置
                return camIdx;
            }
        }
        return -1;
    }

    /**
     * 将拍下来的照片存放到指定缓存文件中
     *
     * @param data
     * @throws IOException
     */
    public static String saveToSDCard(byte[] data) throws IOException {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("'ORACLEEN_IMG'_yyyy-MM-dd HH:mm:ss"); // 格式化时间
        String filename = format.format(date) + ".jpg";
        File fileFolder = new File(cacheDir.getAbsolutePath() + "/oracleen/");
        if (!fileFolder.exists()) { // 如果目录不存在，则创建一个名为"oracleen"的目录
            fileFolder.mkdir();
        }
        File jpgFile = new File(fileFolder, filename);
        FileOutputStream outputStream = new FileOutputStream(jpgFile); // 文件输出流
        outputStream.write(data); // 写入sd卡中
        outputStream.close(); // 关闭输出流
        return jpgFile.getPath();
    }

    /**
     * 用于根据手机方向获得相机预览画面旋转的角度
     *
     * @param activity
     * @return
     */
    public int getPreviewDegree(Activity activity) {
        // 获得手机的方向
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degree = 0;
        // 根据手机的方向计算相机预览画面应该选择的角度
        switch (rotation) {
            case Surface.ROTATION_0:
                degree = 90;
                break;
            case Surface.ROTATION_90:
                degree = 0;
                break;
            case Surface.ROTATION_180:
                degree = 270;
                break;
            case Surface.ROTATION_270:
                degree = 180;
                break;
        }
        return degree;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

    }
}