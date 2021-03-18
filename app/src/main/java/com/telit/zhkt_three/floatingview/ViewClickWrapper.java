package com.telit.zhkt_three.floatingview;

import android.view.View;

/**
 *    author : Android 轮子哥
 *    github : https://github.com/getActivity/XToast
 *    time   : 2019/01/04
 *    desc   : {@link View.OnClickListener} 包装类
 */
final class ViewClickWrapper implements View.OnClickListener {

    private final PopWindows popWindow;
    private final OnClickListener mListener;

    ViewClickWrapper(PopWindows popWindows,View view, OnClickListener listener) {

        mListener = listener;
        popWindow=popWindows;
        view.setOnClickListener(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void onClick(View v) {
        mListener.onClick(popWindow, v);
    }
}