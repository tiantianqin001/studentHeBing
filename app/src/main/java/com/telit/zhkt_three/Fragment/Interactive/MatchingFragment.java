package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.interactive.LXT.LineBean;
import com.telit.zhkt_three.CustomView.interactive.LXT.MatchingView;
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
 MatchingFragment matchingFragment = new MatchingFragment();

 List<LineBean> left = new ArrayList<>();
 List<LineBean> right = new ArrayList<>();
 for (int i = 0; i < 4; i++) {
 LineBean lineBean = new LineBean();
 lineBean.setId(i);
 lineBean.setContent("七子笑" + i);
 if (i % 2 == 0) {
 left.add(lineBean);
 } else {
 right.add(lineBean);
 }
 }

 matchingFragment.fillMatchingData(0, left.size(), left, right);

 FragmentManager fragmentManager = getSupportFragmentManager();
 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 fragmentTransaction.replace(R.id.frame_layout, matchingFragment);
 fragmentTransaction.commit();
 */
public class MatchingFragment extends Fragment implements View.OnClickListener {
    private Unbinder unbinder;
    @BindView(R.id.matching_layout)
    FrameLayout matching_layout;
    @BindView(R.id.matching_title)
    TextView matching_title;
    @BindView(R.id.matching_time)
    TextView matching_time;
    @BindView(R.id.matching_answer)
    ImageView matching_answer;
    @BindView(R.id.matchingView)
    MatchingView matchingView;
    @BindView(R.id.matching_reset)
    ImageView matching_reset;

    private ScheduledExecutorService timeExecutor;
    private long timerCount;
    private boolean isTimeOver = false;

    /**
     * 左右连线数据
     */
    private List<LineBean> leftList;
    private List<LineBean> rightList;

    /**
     * 连线题对数
     */
    private int matchingGroup;

    /**
     * 背景下标：0 绿林翠竹
     */
    private int bgIndex;

    public void fillMatchingData(int bgIndex, int matchingGroup, List<LineBean> leftList, List<LineBean> rightList) {
        this.bgIndex = bgIndex;
        this.matchingGroup = matchingGroup;
        this.leftList = leftList;
        this.rightList = rightList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lxt_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        matching_reset.setOnClickListener(this);
        matching_answer.setOnClickListener(this);
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
                            matching_time.setText("用时：".concat(QZXTools.getTransmitTime(timerCount)));
                        }
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        initMatchingData(true);

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

    private void initMatchingData(boolean isInit) {
        switch (bgIndex) {
            case 0:
                matchingView.setInitData(matchingGroup, leftList, rightList, 0xFF6BAA1B, R.mipmap.line_img_bg);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.matching_reset:
                matchingView.resetView();
                break;
            case R.id.matching_answer:
                matchingView.showAnswer();
                break;
        }
    }
}
