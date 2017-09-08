package com.example.kaifa.essayjoke.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * 自定义的ListView 用于ScrollView嵌套ListView时 ListView展示不全
 */
public class ScrollListView extends ListView
{

	public ScrollListView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}

	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int mExpandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, mExpandSpec);
	}

}