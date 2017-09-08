package com.example.kaifa.essayjoke.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaifa.essayjoke.R;
import com.example.kaifa.essayjoke.utils.DisplayUtils;

import java.lang.ref.WeakReference;

public class SplashActivity extends AppCompatActivity implements View.OnClickListener {

    private static final long WAIT_TIME = 5000;
    private SplashActivity self = this;

    private ImageView sun;
    private ImageView cloud_1;
    private ImageView cloud_2;
    private ImageView cloud_3;
    private TextView joinNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initView();
        initData();
        setListener();

        // wait for a moment start activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // start activity
                jumpToMainActivity();
            }
        }, WAIT_TIME);

    }

    private void setListener() {
        playAnimOnLayoutFinish();
        joinNow.setOnClickListener(this);
    }

    private void initData() {
    }

    private void initView() {
        joinNow = (TextView) findViewById(R.id.join_now);
        sun = (ImageView) findViewById(R.id.sun);
        cloud_1 = (ImageView) findViewById(R.id.cloud_1);
        cloud_2 = (ImageView) findViewById(R.id.cloud_2);
        cloud_3 = (ImageView) findViewById(R.id.cloud_3);
    }

    private void playAnimOnLayoutFinish() {
        // 需要在布局填充完成后才能获取到View的尺寸
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        playAnim();
                        // 需要移除监听，否则会重复触发
                        getWindow().getDecorView().getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                }
        );
    }

    private void playJoinNowAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(joinNow, "alpha", 0.1f, 1f);
        anim.setDuration(1500);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setRepeatMode(ObjectAnimator.REVERSE);
        anim.start();
    }

    private void jumpToMainActivity() {
        WeakReference<SplashActivity> weakReference = new WeakReference<SplashActivity>(self);
        SplashActivity splashActivity = weakReference.get();
        if (splashActivity != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.right_in, R.anim.right_out);
            self = null;
            finish();
        }
    }

    private void playSunAnim() {
        ObjectAnimator anim = ObjectAnimator.ofFloat(sun, "rotation", 0f, 360f);
        anim.setRepeatMode(ObjectAnimator.RESTART); //因为是0-360，所以重复模式为从头开始
        anim.setRepeatCount(ObjectAnimator.INFINITE); //设置为无限重复
        anim.setInterpolator(new LinearInterpolator()); //设置为匀速动画，不然会出现明显的停顿效果。
        anim.setDuration(15 * 1000);
        anim.start();
    }

    private void playCloud_1Anim() {
        // 下面几步是在计算平移动画的起止点，结合xml来理解，最终实现的效果就是
        // 当完全移动出屏幕左边时，立即从屏幕右边重新开始向左平移。有的为了实现层次感，它右边的起点我设置的在屏幕右边缘稍远的地方，具体的到GitHub看。
        int left = cloud_1.getLeft();
        int right = DisplayUtils.getScreenWidth() - cloud_1.getRight();
        int width = cloud_1.getMeasuredWidth();
        ObjectAnimator anim = ObjectAnimator.ofFloat(cloud_1, "translationX", right + width, -(left + width));

        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(8 * 1000);
        anim.start();
    }

    private void playCloud_2Anim() {
        int left = cloud_2.getLeft();
        int right = DisplayUtils.getScreenWidth() - cloud_2.getRight();
        int width = cloud_2.getMeasuredWidth();
        int cloud_1Width = cloud_1.getMeasuredWidth();
        int leftMargin =
                Math.abs(((ViewGroup.MarginLayoutParams) cloud_2.getLayoutParams()).leftMargin);
        ObjectAnimator anim =
                ObjectAnimator.ofFloat(cloud_2, "translationX", right + width + leftMargin + ((float) cloud_1Width * 0.8f),
                        -(left + width + leftMargin));
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(10 * 1000);
        anim.start();
    }

    private void playCloud_3Anim() {
        int left = cloud_3.getLeft();
        int right = DisplayUtils.getScreenWidth() - cloud_3.getRight();
        int width = cloud_3.getMeasuredWidth();
        int cloud_1Width = cloud_1.getMeasuredWidth();
        int leftMargin = Math.abs(((ViewGroup.MarginLayoutParams) cloud_3.getLayoutParams()).leftMargin);
        ObjectAnimator anim = ObjectAnimator.ofFloat(cloud_3, "translationX",
                right + width + leftMargin + ((float) cloud_1Width * 1.5f),
                -(left + width + leftMargin));
        anim.setRepeatMode(ObjectAnimator.RESTART);
        anim.setRepeatCount(ObjectAnimator.INFINITE);
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration(12 * 1000);
        anim.start();
    }

    private void playAnim() {
        playSunAnim();
        playCloud_1Anim();
        playCloud_2Anim();
        playCloud_3Anim();
        playJoinNowAnim();
    }

    @Override
    public void onClick(View v) {
        jumpToMainActivity();
    }
}
