package com.telit.zhkt_three;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;


/**
 * ***************************************************************************
 * author: Administrator
 * time: 2016/12/28 8:29
 * name:
 * overview:
 * usage:
 * ***************************************************************************
 */
public class MaxRecyclerView extends RecyclerView {
    public MaxRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxRecyclerView(Context context) {
        super(context);
    }

   /* @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = View.MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, View.MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }*/
}
