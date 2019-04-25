package com.example.kaifa.essayjoke.activity;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;

import com.example.alex.framelibrary.skin.BaseSkinActivity;
import com.example.kaifa.essayjoke.R;
import com.zhbstudy.baselibrary.ioc.OnClick;
import com.zhbstudy.baselibrary.ioc.ViewById;

/**
 * Created by zhb on 2019/3/15.
 */
public class HandleThreadActivity extends BaseSkinActivity {

    private Handler mainHandle, workHandle;
    private HandlerThread handlerThread;
    @ViewById(R.id.tv)
    private TextView tv;
    @ViewById(R.id.bt)
    private Button bt;


    @Override
    protected void initData() {
        // 创建与主线程关联的Handler
        mainHandle = new Handler();

        /**
         * 步骤1：创建HandlerThread实例对象
         * 传入参数 = 线程名字，作用 = 标记该线程
         */
        handlerThread = new HandlerThread("handleThread");
        /**
         * 步骤2：启动线程
         */
        handlerThread.start();

        /**
         * 步骤3：创建工作线程Handler & 复写handleMessage（）
         * 作用：关联HandlerThread的Looper对象、实现消息处理操作 & 与其他线程进行通信
         * 注：消息处理操作（HandlerMessage（））的执行线程 = mHandlerThread所创建的工作线程中执行
         */
        workHandle = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
//                super.handleMessage(msg);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                mainHandle.post(new Runnable() {
                    @Override
                    public void run() {
                        tv.setText("点击");

                        // 步骤5：结束线程，即停止线程的消息循环
                        handlerThread.quit();
                    }
                });
            }
        };
    }


    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_handle_thread);
    }


    @OnClick(R.id.bt)
    private void btClick(Button bt) {
        // 通过sendMessage（）发送
        // a. 定义要发送的消息
        Message msg = Message.obtain();
        msg.what = 2; //消息的标识
        msg.obj = "B"; // 消息的存放
        // b. 通过Handler发送消息到其绑定的消息队列
        workHandle.sendMessage(msg);
    }
}
