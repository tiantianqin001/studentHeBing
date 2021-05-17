package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

public class NoScrollXRecyclerView extends XRecyclerView {


    public NoScrollXRecyclerView(Context context) {
        super(context);
    }

    public NoScrollXRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoScrollXRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, height);
    }
}
