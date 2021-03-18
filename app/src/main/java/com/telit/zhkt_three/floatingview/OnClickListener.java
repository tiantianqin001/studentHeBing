package com.telit.zhkt_three.floatingview;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : View 的点击事件封装
 */
public interface OnClickListener<V extends View> {

    /**
     * 点击回调
     */
    void onClick(PopWindows toast, V view);
}