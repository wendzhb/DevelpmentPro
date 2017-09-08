package com.example.kaifa.essayjoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;


public class ScrollGridView extends GridView {

	public ScrollGridView(Context context,
						  AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO 自动生成的方法存根
		int expandSpec = MeasureSpec.makeMeasureSpec(
				Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec); 
	}

}