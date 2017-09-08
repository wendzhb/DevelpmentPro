package com.example.kaifa.essayjoke.view;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

public class MarqueeTextView extends TextView implements Runnable {
    private int currentScrollX;// 当前滚动的位置
    private boolean isStop = false;
    private int textWidth = 100;
    private boolean isMeasure = false;

    public MarqueeTextView(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
            super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
           
    }

    /**
     * 获取文字宽度
     */
    private void getTextWidth() {
            Paint paint = this.getPaint();
            String str = this.getText().toString();
            textWidth = (int) paint.measureText(str);
    }

    @Override
    public void run() {
//            currentScrollX -= 1;// 滚动速度
            currentScrollX += 1;// 滚动速度
            scrollTo(currentScrollX, 0);
            if (isStop) {
                    return;
            }
//            if (getScrollX() <= -(this.getWidth())) {
//                    scrollTo(textWidth, 0);
//                    currentScrollX = textWidth;
//            }
            
            if (getScrollX() >= textWidth) {
                scrollTo(-(this.getWidth()), 0);
                currentScrollX = -(this.getWidth());
            }
//            int a = getScrollX();
//            int b = -(this.getWidth());
//            LogHelper.logE("marqueeTextView", "x：" + a + ",width:" + b + ",currentScrollX:" 
//            		+ currentScrollX
//            		+ "textWid" + textWidth);
            postDelayed(this, 10);
    }

    // 开始滚动
    public void startScroll() {
            isStop = false;
            this.removeCallbacks(this);
            post(this);
    }

    // 停止滚动
    public void stopScroll() {
            isStop = true;
    }

    // 从头开始滚动
    public void startFor0() {
        currentScrollX = 0;
        startScroll();
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        // TODO Auto-generated method stub
        super.setText(text, type);
        if (!isMeasure) {// 文字宽度只需获取一次就可以了
            getTextWidth();
            isMeasure = true;
        }
        startScroll();
    }

    @Override
    public void destroyDrawingCache() {
        // TODO Auto-generated method stub
        super.destroyDrawingCache();

    }
}