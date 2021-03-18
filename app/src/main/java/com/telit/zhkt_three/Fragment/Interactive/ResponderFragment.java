package com.telit.zhkt_three.Fragment.Interactive;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * author: qzx
 * Date: 2019/5/13 20:12
 * <p>
 * 互动---抢答
 */
public class ResponderFragment extends Fragment {

    private TextView interactive_name;
    private TextView interactive_timer;

    private FrameLayout response_frame;

    private TextView response_tv_answer;

    private ImageView response_img_start;

    private ScheduledExecutorService timeExecutor;

    private long timerCount;

    private boolean isTimeOver = false;
    private View view;
    private static boolean isShow=false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_responder_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isShow=true;
        interactive_name = view.findViewById(R.id.interactive_name);
        interactive_timer = view.findViewById(R.id.interactive_timer);
        response_img_start = view.findViewById(R.id.response_img_start);
        response_frame = view.findViewById(R.id.response_frame);
        response_tv_answer = view.findViewById(R.id.response_tv_answer);

        //设置互动名称
        interactive_name.setText(getResources().getString(R.string.responder));

        response_img_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_FIRST_ANSWER, MsgUtils.createFirstAnswer());
                response_img_start.setEnabled(false);
                response_img_start.setImageResource(R.mipmap.response_end);
            }
        });

        //开启互动计时
        timeExecutor = Executors.newSingleThreadScheduledExecutor();
        timeExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                timerCount++;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isTimeOver) {
                            interactive_timer.setText(QZXTools.getTransmitTime(timerCount));
                        }
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }
    @Override
    public void onDestroy() {
        if (timeExecutor != null) {
            isTimeOver = true;
            timeExecutor.shutdown();
            timerCount = 0;
        }
        super.onDestroy();
    }
    /**
     * 显示抢答结果展示
     */
    public void showResponseResult(String name) {
        if (response_img_start!=null&& response_frame!=null){
            //双重判断fragment 是不是还活这
            if (isShow){
                response_img_start.setVisibility(View.GONE);
                response_frame.setVisibility(View.VISIBLE);
                Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "PingFang-SimpleBold.ttf");
                response_tv_answer.setTypeface(typeface);
                response_tv_answer.setText(name);

                isTimeOver = true;
            }

        }

    }

    @Override
    public void onPause() {
        super.onPause();
        //再这个这个界面失去焦点的时候就不要再给我显示布局了
        isShow=false;
    }
}
