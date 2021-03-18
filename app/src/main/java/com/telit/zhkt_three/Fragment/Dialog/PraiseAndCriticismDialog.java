package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * author: qzx
 * Date: 2019/5/13 14:38
 */
public class PraiseAndCriticismDialog extends DialogFragment {

    private FrameLayout praiseFrame;
    private FrameLayout criticismFrame;
    private TextView tvName;

    private boolean isPraised = false;
    private String name;

    public void setDialogType(boolean isPraised, String name) {
        this.isPraised = isPraised;
        this.name = name;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_praise_criticism_layout, container, false);
        praiseFrame = view.findViewById(R.id.fl_praise_area);
        criticismFrame = view.findViewById(R.id.fl_criticism_area);
        tvName = view.findViewById(R.id.tv_student_name);
        if (isPraised) {
            criticismFrame.setVisibility(View.GONE);
            praiseFrame.setVisibility(View.VISIBLE);
            tvName.setText(name);
        } else {
            praiseFrame.setVisibility(View.GONE);
            criticismFrame.setVisibility(View.VISIBLE);
        }

        ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);

        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                dismiss();

                scheduledExecutorService.shutdown();
            }
        }, 3, TimeUnit.SECONDS);

        return view;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }
}
