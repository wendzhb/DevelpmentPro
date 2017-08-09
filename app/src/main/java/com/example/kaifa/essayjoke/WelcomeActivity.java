package com.example.kaifa.essayjoke;

import android.content.Intent;
import android.os.Handler;

import com.example.alex.framelibrary.skin.BaseSkinActivity;

public class WelcomeActivity extends BaseSkinActivity {

    private static final long WAIT_TIME = 3000;

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_welcome);

        // wait for a moment start activity
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // start activity
                Intent intent = new Intent(WelcomeActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, WAIT_TIME);
    }
}
