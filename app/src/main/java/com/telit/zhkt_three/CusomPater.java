package com.telit.zhkt_three;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class CusomPater extends ViewPager {
    public CusomPater(@NonNull Context context) {
        super(context);
    }

    public CusomPater(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        try {
            super.onTouchEvent(event);
        } catch (IllegalArgumentException  e) {
            Log.e( "ImageOriginPager-error" , "IllegalArgumentException 错误被活捉了！");
            e.printStackTrace();
        }
        return true;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean b=false;
        try {
             b = super.onInterceptTouchEvent(event);

        } catch (IllegalArgumentException  e) {
            Log.e( "ImageOriginPager-error" , "IllegalArgumentException 错误被活捉了！");
            e.printStackTrace();
            b=true;
        }
      return b;
    }
}
