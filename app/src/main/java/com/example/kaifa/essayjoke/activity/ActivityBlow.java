package com.example.kaifa.essayjoke.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.kaifa.essayjoke.R;
import com.example.kaifa.essayjoke.sensor.AudioSensor;
import com.example.kaifa.essayjoke.sensor.MySensor;


/**
 * Created by SiKang on 2016/1/14.
 */
public class ActivityBlow extends Activity implements View.OnTouchListener {
    private final String TAG = "ActivityBlowTest";
    private ImageView mWindmillImg;
    private Button mBlowBtn;
    private MySensor blowSenser;
    private boolean isBlowing;
    private boolean isBegin;
    private float blowAngle;
    private int blowTime;
    public static final int BLOW_START = 0;
    public static final int BLOWING = 1;
    public static final int BLOW_END = 2;
    private boolean isReset;
    private boolean isPause;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_blow);
        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        initLayout();
        initData();
        blowSenser = AudioSensor.getInstance(handler);
    }

    public void initData() {
        isPause = false;//是否进入onPause
        isBlowing = false;//转动线程是否已开启
        isBegin = false;//是否按住吹起按钮
        blowAngle = 0;//每一帧旋转的角度
        isReset = false;//是否补气
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case BLOW_START:
                    if (isBegin) {
                        //收到吹气通知，重置部分标识
                        if (!isReset)
                            isReset = true;
                        if (blowTime > 0)
                            blowTime = 0;
                        if (!isBlowing) {
                            //如果吹起线程没有开启，则开启新的线程
                            isBlowing = true;
                            new Thread(new blowRun()).start();
                        }
                    }
                    break;
                case BLOWING:
                    //更新UI
                    mWindmillImg.setRotation(blowAngle);
                    break;
                case BLOW_END:
                    //转动停止
                    isBlowing = false;
                    break;
            }
            return false;
        }
    });

    protected void initLayout() {
        //获得控件
        mWindmillImg = (ImageView) this.findViewById(R.id.activity_blow_windmill);
        mBlowBtn = (Button) this.findViewById(R.id.activity_blow_btn);
        mBlowBtn.setOnTouchListener(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int btnWidth = dm.heightPixels / 6;
        layoutParams.width = btnWidth;//设置宽高
        layoutParams.height = btnWidth;
        layoutParams.setMargins((dm.widthPixels - btnWidth) / 2, dm.heightPixels / 6 * 4, 0, 0);
        mBlowBtn.setLayoutParams(layoutParams);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isPause = false;
        blowSenser.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        blowSenser.shutDown();
        isPause = true;
    }

    @Override
    protected void onDestroy() {
        blowSenser.destory();
        super.onDestroy();

    }


    private float rotationAngle = 1f;

    //转动风车
    class blowRun implements Runnable {
        @Override
        public void run() {
            new Thread(new stopRun()).start();
            new Thread(new rotationRun()).start();
            while (isBlowing && !isPause) {
                blowAngle += rotationAngle;
                if (blowAngle > 360) {
                    blowAngle = 1;
                }
                handler.sendEmptyMessage(BLOWING);
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //控制加速
    class rotationRun implements Runnable {
        @Override
        public void run() {
            while (isReset && !isPause) {
                if (rotationAngle <= 10) {
                    rotationAngle += 0.4;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //控制减速
    class stopRun implements Runnable {
        @Override
        public void run() {
            while (blowTime <= 2 && !isPause) {
                blowTime++;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            isReset = false;
            while (rotationAngle > 0 && !isReset && !isPause) {
                rotationAngle -= 0.4;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (isReset) {
                new Thread(new stopRun()).start();
                new Thread(new rotationRun()).start();
            } else {
                handler.sendEmptyMessage(BLOW_END);
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            isBegin = false;
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isBegin = true;
        }
        return false;
    }
}
