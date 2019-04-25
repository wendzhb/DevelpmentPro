package com.example.kaifa.essayjoke.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kaifa.essayjoke.R;
import com.example.kaifa.essayjoke.datetimedialog.DateUtils;
import com.example.kaifa.essayjoke.datetimedialog.JudgeDate;
import com.example.kaifa.essayjoke.datetimedialog.ScreenInfo;
import com.example.kaifa.essayjoke.datetimedialog.WheelMain;
import com.example.kaifa.essayjoke.model.MessageEvent;
import com.example.kaifa.essayjoke.model.Person;
import com.example.kaifa.essayjoke.view.DatePickerDialog;
import com.zhbstudy.baselibrary.base.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeActivity extends BaseActivity {


    @Override
    protected void initData() {
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");//设置日期格式
        calendar = Calendar.getInstance(Locale.CHINA);
        fmtDate = new SimpleDateFormat("yyyy-MM-dd");

        showTiemDialog();

        startActivity(TestActivity.class);
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initTitle() {

    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_time);
        EventBus.getDefault().register(this);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(MessageEvent messageEvent) {
        Toast.makeText(this, messageEvent.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private SimpleDateFormat df;
    private WheelMain wheelMainDate;
    private String beginTime;

    private void showTiemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View menuView = LayoutInflater.from(this).inflate(R.layout.show_popup_window, null);

        builder.setView(menuView);
        ScreenInfo screenInfoDate = new ScreenInfo(this);
        wheelMainDate = new WheelMain(menuView, true);
        wheelMainDate.screenheight = screenInfoDate.getHeight();
        String time = DateUtils.currentMonth().toString();
        final Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-DD")) {
            try {
                calendar.setTime(new Date(time));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hours = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);
        wheelMainDate.initDateTimePicker(year, month, day, hours, minute);
        TextView tv_cancle = (TextView) menuView.findViewById(R.id.tv_cancle);
        TextView tv_ensure = (TextView) menuView.findViewById(R.id.tv_ensure);
        TextView tv_pop_title = (TextView) menuView.findViewById(R.id.tv_pop_title);
        final AlertDialog dialog = builder.create();

        tv_pop_title.setText("请选择面试时间");
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                dialog.dismiss();
            }
        });
        tv_ensure.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                beginTime = wheelMainDate.getTime().toString();
                try {
                    Date parse = df.parse(beginTime);
                    if (parse.getTime() < calendar.getTimeInMillis()) {
                        //"您选择的时间不正确，请重新选择"
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //设置时间
                //setText(DateUtils.formateStringH(beginTime, DateUtils.yyyyMMddHHmm));
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    // 获取一个日历对象
    private Calendar calendar;
    // 获取日期格式器对象
    private DateFormat fmtDate;
    private Date checkinDate;

    private void simpleTimeDialog() {
        DatePickerDialog checkindateDialog = new DatePickerDialog(
                this, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year,
                                  int monthOfYear, int dayOfMonth) {
                // 修改日历控件的年，月，日
                // 这里的year,monthOfYear,dayOfMonth的值与DatePickerDialog控件设置的最新值一致
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                checkinDate = calendar.getTime();
                // 将页面TextView的显示更新为最新时间
                String format = fmtDate.format(checkinDate);
                //setText(format);
            }
        }, calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        checkindateDialog.show();
    }
}
