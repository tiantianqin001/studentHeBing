package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.interactive.FFK.FlipCardBean;
import com.telit.zhkt_three.CustomView.interactive.FFK.FlipCardLayout;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/8/1 10:10
 *
 FlipCardFragment flipCardFragment = new FlipCardFragment();

 List<FlipCardBean> flipCardBeans = new ArrayList<>();
 for (int i = 0; i < 2; i++) {
 FlipCardBean flipCardBean = new FlipCardBean();
 flipCardBean.setSign_font(1);
 flipCardBean.setResId_font(R.mipmap.cjfl_moon_bg);
 flipCardBean.setSign_behind(0);
 flipCardBean.setText_behind("万圣节-" + i);
 flipCardBeans.add(flipCardBean);
 }

 flipCardFragment.setFlipCardBeans(flipCardBeans);

 FragmentManager fragmentManager = getSupportFragmentManager();
 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 fragmentTransaction.replace(R.id.frame_layout, flipCardFragment);
 fragmentTransaction.commit();
 */
public class FlipCardFragment extends Fragment {
    private Unbinder unbinder;
    @BindView(R.id.ffk_layout)
    FrameLayout ffk_layout;
    //    @BindView(R.id.cjfl_title)
//    TextView cjfl_title;
    @BindView(R.id.ffk_time)
    TextView ffk_time;
    @BindView(R.id.flipCardLayout)
    FlipCardLayout flipCardLayout;

    private ScheduledExecutorService timeExecutor;
    private long timerCount;
    private boolean isTimeOver = false;

    /**
     * 要展示的翻翻卡个数
     */
    private int ffk_count;

    /**
     * 数据
     */
    private List<FlipCardBean> flipCardBeans;

    public void setFlipCardBeans(List<FlipCardBean> flipCardBeans) {
        this.flipCardBeans = flipCardBeans;
        ffk_count = flipCardBeans.size();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ffk_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

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
                            ffk_time.setText("用时：".concat(QZXTools.getTransmitTime(timerCount)));
                        }
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        initFlipCardData();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (timeExecutor != null) {
            isTimeOver = true;
            timeExecutor.shutdown();
            timeExecutor = null;
        }
        super.onDestroyView();
    }

    private void initFlipCardData() {
        for (int i = 0; i < flipCardBeans.size(); i++) {
            FlipCardBean flipCardBean = flipCardBeans.get(i);
            flipCardLayout.addFlipCard(flipCardBean.getSign_font(), flipCardBean.getText_font(), flipCardBean.getResId_font(),
                    flipCardBean.getSign_behind(), flipCardBean.getText_behind(), flipCardBean.getResId_behind(), flipCardBeans.size(), i);
        }
    }
}
