package com.telit.zhkt_three.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.telit.zhkt_three.CustomView.MultiColorPBView;
import com.telit.zhkt_three.R;

/**
 * 自定义多色圆形进度条
 */
public class CircleProgressDialogFragment extends DialogFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //必须设置在onCreateView之前
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.CircleProgressDialog);
    }

    private MultiColorPBView multiColorPBView;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        //设置背景为透明
        window.setBackgroundDrawable(ContextCompat.getDrawable(getContext(), android.R.color.transparent));
        //减去状态栏高度
        int screenHeight = getScreenHeight(getActivity());
        int statusBarHeight = getStatusBarHeight(getContext());
        int dialogHeight = screenHeight - statusBarHeight - 30;
        //设置弹窗大小为会屏
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
        //去除阴影
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.dimAmount = 0.0f;
        window.setAttributes(layoutParams);
    }

    //获取屏幕高度
    private static int getScreenHeight(Activity activity) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    //获取状态栏高度
    private static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //取消标题
//        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getDialog().setCanceledOnTouchOutside(false);

        View view = inflater.inflate(R.layout.circle_progress_layout, container, false);
        multiColorPBView = view.findViewById(R.id.multiColorPBView);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //这里就应该释放掉multiColorPBView
        releaseAll();
    }

    public void releaseAll() {
        if (multiColorPBView != null) {
            multiColorPBView.releaseAll();
            multiColorPBView = null;
        }
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            if (!isAdded()) {
                manager.beginTransaction().remove(this).commitAllowingStateLoss();
                FragmentTransaction ft = manager.beginTransaction();
                ft.add(this, tag);
                ft.commitAllowingStateLoss();
            }

        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }


}
