package com.telit.zhkt_three.Activity.PreView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;

public class HackyProblematicViewGroup extends LinearLayout {
    public HackyProblematicViewGroup(@NonNull Context context) {
        super(context);
    }

    public HackyProblematicViewGroup(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public HackyProblematicViewGroup(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //uncomment if you really want to see these errors
            //e.printStackTrace();
            return false;
        }
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            super.onTouchEvent(event);
        } catch (IllegalArgumentException  e) {
            Log.e( "ImageOriginPager-error" , "IllegalArgumentException 错误被活捉了！");
            e.printStackTrace();
        }
        return false;
    }
}
