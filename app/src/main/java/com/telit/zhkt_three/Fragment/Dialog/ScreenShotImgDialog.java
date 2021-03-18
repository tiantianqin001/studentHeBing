package com.telit.zhkt_three.Fragment.Dialog;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.telit.zhkt_three.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: qzx
 * Date: 2019/7/9 8:54
 * <p>
 * 截屏的图片展示,结果展现后三秒内自动消失
 */
public class ScreenShotImgDialog extends DialogFragment {

    private String imgFilePath;

    private ScheduledExecutorService scheduledExecutorService;

    public void setImgFilePath(String imgFilePath) {
        this.imgFilePath = imgFilePath;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_shot_img, container, false);

        getDialog().setCanceledOnTouchOutside(true);

        ImageView shot_img = view.findViewById(R.id.shot_img);
        shot_img.setImageBitmap(BitmapFactory.decodeFile(imgFilePath));

        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.schedule(new Runnable() {
            @Override
            public void run() {
                if (getDialog().isShowing()) {
                    getDialog().dismiss();
                }
            }
        }, 3000, TimeUnit.MILLISECONDS);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
        super.onDestroyView();
    }
}
