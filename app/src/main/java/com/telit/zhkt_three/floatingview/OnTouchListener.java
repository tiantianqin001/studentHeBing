package com.telit.zhkt_three.floatingview;

import android.view.MotionEvent;
import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : View 的触摸事件封装
 */
public interface OnTouchListener<V extends View> {
    /**
     * 触摸回调
     */
    boolean onTouch(PopWindows toast, V view, MotionEvent event);
}