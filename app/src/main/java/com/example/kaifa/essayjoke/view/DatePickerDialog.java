package com.example.kaifa.essayjoke.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources.NotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.LinearLayout;
import android.widget.NumberPicker;


import com.example.kaifa.essayjoke.R;

import java.lang.reflect.Field;

/**
 * Created by ZHB on 2017/5/22.
 *
 * Des:自定义布局的DatePickerDialog,可设置分割线的高度及颜色
 */
public class DatePickerDialog extends AlertDialog implements OnClickListener,
        OnDateChangedListener, android.view.View.OnClickListener {

	private static final String START_YEAR = "start_year";
	private static final String START_MONTH = "start_month";
	private static final String START_DAY = "start_day";

	private final DatePicker mDatePickerStart;
	private final OnDateSetListener mCallBack;
	private Button mSureTextView, mChanceTextView;

	public interface OnDateSetListener {
		void onDateSet(DatePicker startDatePicker, int startYear,
                       int startMonthOfYear, int startDayOfMonth);
	}

	public DatePickerDialog(Context context, OnDateSetListener callBack,
                            int year, int monthOfYear, int dayOfMonth) {
		this(context, 0, callBack, year, monthOfYear, dayOfMonth);
	}

	public DatePickerDialog(Context context, int theme,
                            OnDateSetListener callBack, int year, int monthOfYear,
                            int dayOfMonth) {
		super(context, theme);

		mCallBack = callBack;

		Context themeContext = getContext();

		// setButton(BUTTON_POSITIVE, "确 定", this);
		// setButton(BUTTON_NEGATIVE, "取 消", this);
		// setIcon(0);

		LayoutInflater inflater = (LayoutInflater) themeContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_date_picker, null);
		setView(view);
		mDatePickerStart = (DatePicker) view.findViewById(R.id.datePickerStart);
		mSureTextView = (Button) view.findViewById(R.id.bt_positive);
		mChanceTextView = (Button) view.findViewById(R.id.bt_nagvite);
		mDatePickerStart.init(year, monthOfYear, dayOfMonth, this);
		mSureTextView.setOnClickListener(this);
		mChanceTextView.setOnClickListener(this);
		setDatePickerDividerColor(mDatePickerStart);

	}

	public void onClick(DialogInterface dialog, int which) {
		if (which == BUTTON_POSITIVE)
			tryNotifyDateSet();
	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		if (view.getId() == R.id.datePickerStart)
			mDatePickerStart.init(year, month, day, this);
	}

	public DatePicker getDatePickerStart() {
		return mDatePickerStart;
	}

	public void updateStartDate(int year, int monthOfYear, int dayOfMonth) {
		mDatePickerStart.updateDate(year, monthOfYear, dayOfMonth);
	}

	private void tryNotifyDateSet() {
		if (mCallBack != null) {
			mDatePickerStart.clearFocus();
			mCallBack.onDateSet(mDatePickerStart, mDatePickerStart.getYear(),
					mDatePickerStart.getMonth(),
					mDatePickerStart.getDayOfMonth());
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public Bundle onSaveInstanceState() {
		Bundle state = super.onSaveInstanceState();
		state.putInt(START_YEAR, mDatePickerStart.getYear());
		state.putInt(START_MONTH, mDatePickerStart.getMonth());
		state.putInt(START_DAY, mDatePickerStart.getDayOfMonth());
		return state;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		int start_year = savedInstanceState.getInt(START_YEAR);
		int start_month = savedInstanceState.getInt(START_MONTH);
		int start_day = savedInstanceState.getInt(START_DAY);
		mDatePickerStart.init(start_year, start_month, start_day, this);

	}

	/**
	 * 
	 * 设置时间选择器的分割线颜色
	 * 
	 * @param datePicker
	 */
	private void setDatePickerDividerColor(DatePicker datePicker) {
		// Divider changing:

		// 获取 mSpinners
		LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

		// 获取 NumberPicker
		LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
		for (int i = 0; i < mSpinners.getChildCount(); i++) {
			NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

			Field[] pickerFields = NumberPicker.class.getDeclaredFields();
			for (Field pf : pickerFields) {
				if (pf.getName().equals("mSelectionDivider")) {// 颜色
					pf.setAccessible(true);
					try {
						// pf.set(picker.getHeight(), 20);
						pf.set(picker, new ColorDrawable(getContext().getResources()
										.getColor(R.color.background_tab_pressed)));
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	/**
	 * 
	 * 设置时间选择器的分割线高度
	 * 
	 * @param datePicker
	 */
	private void setDatePickerDividerHight(DatePicker datePicker) {
		// Divider changing:

		// 获取 mSpinners
		LinearLayout llFirst = (LinearLayout) datePicker.getChildAt(0);

		// 获取 NumberPicker
		LinearLayout mSpinners = (LinearLayout) llFirst.getChildAt(0);
		for (int i = 0; i < mSpinners.getChildCount(); i++) {
			NumberPicker picker = (NumberPicker) mSpinners.getChildAt(i);

			Field[] pickerFields = NumberPicker.class.getDeclaredFields();
			for (Field pf : pickerFields) {
				if (pf.getName().equals("mSelectionDividersDistance")) {
					pf.setAccessible(true);
					try {
						// pf.set(picker.getHeight(), 20);
						pf.set(picker, 40);// 按照需求在此处修改
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (NotFoundException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}

			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.bt_positive:
			tryNotifyDateSet();
			dismiss();
			break;
		case R.id.bt_nagvite:
			dismiss();
			break;

		default:
			break;
		}
	}
}
