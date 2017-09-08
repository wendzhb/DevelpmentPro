package com.example.kaifa.essayjoke.view;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by zhb on 2017/7/15.
 */

public class NormalTextView extends TextView {
    public NormalTextView(Context context) {
        super(context);
    }

    public NormalTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NormalTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        this.setTypeface(Typeface.DEFAULT); //设置字体为默认字体
    }
}
