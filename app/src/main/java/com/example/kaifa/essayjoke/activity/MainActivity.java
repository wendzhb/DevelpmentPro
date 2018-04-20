package com.example.kaifa.essayjoke.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.alex.framelibrary.navigationbar.DefaultNavigationBar;
import com.example.alex.framelibrary.skin.BaseSkinActivity;
import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.crash.ExceptionCrashHandler;
import com.zhbstudy.baselibrary.fixBug.FixDexManager;
import com.zhbstudy.baselibrary.ioc.CheckNet;
import com.zhbstudy.baselibrary.ioc.OnClick;
import com.zhbstudy.baselibrary.ioc.ViewById;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class MainActivity extends BaseSkinActivity {

    @ViewById(R.id.tv)
    private TextView tv;
    @ViewById(R.id.iv)
    private ImageView mIv;
    @ViewById(R.id.edt)
    private EditText mEdt;
    private String locationProvider;
    private double lat;
    private double lng;


    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initTitle() {
        DefaultNavigationBar navigationBar = new DefaultNavigationBar
                .Builder(this)
                .setTitle("title")
                .setRightText("dsafdsfds")
                .hideLeftIcon()
                .setRightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MainActivity.this, "sddfsfd", Toast.LENGTH_SHORT).show();
                    }
                })
                .builder();
    }

    @Override
    protected void initView() {
        //LayoutInflater主要用来实例化view，inflater自己的layout布局
//        View.inflate(this, R.layout.activity_main, null);
//        LayoutInflater.from(this).inflate(R.layout.activity_main, null);
//        LayoutInflater.from(this).inflate(R.layout.activity_main, null, false);

        mEdt.setOnEditorActionListener(new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Log.e("tag", "完成");

                    return true;
                }
                return false;
            }

        });
    }

    @Override
    protected void initData() {
        //获取上次的崩溃信息上传到服务器
        updateCrashFile();
        //每次启动的时候 去后台获取差分包 fix.patch 然后修复本地bug
//        aliFixBug();
        fixDexBug();

        //获取地理位置管理器
        LocationManager mlocationmanager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //获取所有可用的位置提供器
        List<String> prividerLists = mlocationmanager.getProviders(true);
        if (prividerLists.contains(LocationManager.GPS_PROVIDER)) {
            locationProvider = LocationManager.GPS_PROVIDER;
        } else if (prividerLists.contains(LocationManager.NETWORK_PROVIDER)) {
            locationProvider = LocationManager.NETWORK_PROVIDER;
        } else {
            Log.i("location", "没有可用的位置提供器");
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        String provider = mlocationmanager.getBestProvider(criteria,
                true);
        Location location = mlocationmanager.getLastKnownLocation(locationProvider);
        if (location != null) {
            updateWithNewLocation(location);
            //showLocation(location);
        } else {
            Log.i("location", "无法获取当前位置");


        }
        //监视地理位置变化
        mlocationmanager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

    }

    private void updateWithNewLocation(Location location) {
        String coordinate;
        String addressStr = "no address \n";
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
            //double lat = 39.25631486;
            //double lng = 115.63478961;
            coordinate = "Latitude：" + lat + "\nLongitude：" + lng;
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(lat,
                        lng, 1);
                StringBuilder sb = new StringBuilder();
                if (addresses.size() > 0) {
                    Address address = addresses.get(0);
                    for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                        sb.append(address.getAddressLine(i)).append(" ");
                    }
                /*sb.append(address.getCountryName());
                Log.i("location", "address.getCountryName()==" + address.getCountryName());//国家名*/
                    sb.append(address.getLocality()).append(" ");
                    Log.i("location", "address.getLocality()==" + address.getLocality());//城市名
                    sb.append(address.getSubLocality());
                    Log.i("location", "address.getSubLocality()=2=" + address.getSubLocality());//---区名
                    addressStr = sb.toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //如果用户没有允许app访问位置信息 则默认取上海松江经纬度的数据
            lat = 39.25631486;
            lng = 115.63478961;
            coordinate = "no coordinate!\n";
        }
        Log.i("location", "经纬度为===" + coordinate);
        Log.i("location", "地址为====" + addressStr);
    }

    /**
     * LocationListern监听器
     * 参数：地理位置提供器、监听位置变化的时间间隔、位置变化的距离间隔、LocationListener监听器
     */
    LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle arg2) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            //如果位置发生变化,重新显示
            Log.i("location", location.toString());
        }
    };

    @OnClick({R.id.tv})
    @CheckNet//没网就不执行该方法,而是直接打印没网的Toast
    private void longin(View view) {
        startActivity(TestActivity.class);

    }

    /**
     * 崩溃信息上传到服务器
     */
    private void updateCrashFile() {
        File crashFile = ExceptionCrashHandler.getInstance().getCrashFile();
        if (crashFile.exists()) {
            //上传到服务器
            try {
                InputStreamReader fileReader = new InputStreamReader(new FileInputStream(crashFile));
                char[] buffer = new char[1024];
                int len = 0;
                while ((len = fileReader.read(buffer)) != -1) {
                    String str = new String(buffer, 0, len);
                    Log.e("TAG", str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 阿里热修复
     */
    private void aliFixBug() {
        //测试 ， 直接获取本地内存卡里面的fix.pathc
        File fixFile = new File(Environment.getExternalStorageDirectory(), "fix.apatch");
        if (fixFile.exists()) {
            //修复Bug
            try {
                BaseApplication.mPatchManager.addPatch(fixFile.getAbsolutePath());
                Toast.makeText(this, "修复成功", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "修复失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 自己的修复方式
     */
    private void fixDexBug() {
        File fixFile = new File(Environment.getExternalStorageDirectory(), "fix.dex");
        if (fixFile.exists()) {
            //修复Bug
            FixDexManager fixDexManager = new FixDexManager(this);
            try {
                fixDexManager.fixDex(fixFile.getAbsolutePath());
                Toast.makeText(this, "修复成功", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "修复失败", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @OnClick(R.id.btn_start_skin)
    private void btnStartSkinClick(Button btnStartSkin) {
        startActivity(SkinActivity.class);
    }


    /**
     * 得到本地图片旋转压缩
     *
     * @param path
     * @param size
     * @return
     */
    public static Bitmap getLocalThumbImg(String path, int size) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, newOpts); // 此时返回bm为空
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 1; // 设置缩放比例1表示不缩放
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        bitmap = BitmapFactory.decodeFile(path, newOpts);
        bitmap = compressImage(bitmap, size, "jpg"); // 压缩好比例大小后再进行质量压缩
        int degree = readPictureDegree(path);
        bitmap = rotaingImageView(degree, bitmap);
        return bitmap;
    }

    /**
     * 旋转图片
     *
     * @param angle
     * @param bitmap
     * @return Bitmap
     */
    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        if (bitmap == null)
            return null;
        // 旋转图片 动作
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    /**
     * 图片质量压缩
     *
     * @param image
     * @return
     * @size 图片大小（kb）
     */
    public static Bitmap compressImage(Bitmap image, int size, String imageType) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (imageType.equalsIgnoreCase("png")) {
                image.compress(Bitmap.CompressFormat.PNG, 100, baos);
            } else {
                image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
            }
            int options = 100;
            while (baos.toByteArray().length / 1024 > size) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                if (imageType.equalsIgnoreCase("png")) {
                    image.compress(Bitmap.CompressFormat.PNG, options, baos);
                } else {
                    image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                }
                options -= 10;// 每次都减少10
            }
            ByteArrayInputStream isBm = new ByteArrayInputStream(
                    baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
            Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 读取图片属性：旋转的角度
     *
     * @param path 图片绝对路径
     * @return degree旋转的角度
     */
    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("tag", "requestCode:");

        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Log.e("tag", "requestCode:");

                String path = data.getExtras().getString("path");
                Log.e("tag", "path:" + path);
                Bitmap localThumbImg = getLocalThumbImg(path, 300);
                mIv.setImageBitmap(localThumbImg);

            }
        }
    }

    @OnClick(R.id.btn_camera)
    private void btnCameraClick(Button btnCamera) {
        startActivityForResult(new Intent(this, ActivityCamera.class), 1);
    }

    @OnClick(R.id.btn_image_select)
    private void btnImageSelectClick(Button btnImageSelect) {
        startActivity(DemoSelectImageActivity.class);
    }

    public void plugin(View view) {

        startActivity(PluginActivity.class);
    }

    public void updata(View view) {
        startActivity(ZLGXActivity.class);
    }

    public void finger(View view) {
        startActivity(FingerprintActivity.class);
    }
}
