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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.FlowLayout;
import com.telit.zhkt_three.CustomView.interactive.QWFL.InterestingClassificationLayout;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/7/30 14:20
 *
 QWFLFragment qwflFragment = new QWFLFragment();

 List<String> leftList = new ArrayList<>();
 leftList.add("7");
 leftList.add("9");
 leftList.add("11");
 leftList.add("13");

 List<String> rightList = new ArrayList<>();
 rightList.add("6");
 rightList.add("8");
 rightList.add("10");

 qwflFragment.fillData("奇数偶数学习", "奇数", "偶数", leftList, rightList, 2);

 FragmentManager fragmentManager = getSupportFragmentManager();
 FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
 fragmentTransaction.replace(R.id.frame_layout, qwflFragment);
 fragmentTransaction.commit();
 */
public class QWFLFragment extends Fragment implements View.OnClickListener, InterestingClassificationLayout.QWFLResultInterface {

    private Unbinder unbinder;
    @BindView(R.id.qwfl_layout)
    FrameLayout qwfl_layout;
    @BindView(R.id.qwfl_title)
    TextView qwfl_title;
    @BindView(R.id.qwfl_time)
    TextView qwfl_time;
    @BindView(R.id.qwfl_view)
    InterestingClassificationLayout qwfl_view;
    @BindView(R.id.qwfl_reset)
    ImageView qwfl_reset;
    @BindView(R.id.qwfl_result_layout)
    LinearLayout qwfl_result_layout;
    @BindView(R.id.qwfl_result_flowlayout_left)
    FlowLayout left_flow;
    @BindView(R.id.qwfl_result_flowlayout_right)
    FlowLayout right_flow;
    @BindView(R.id.qwfl_result_tv_left)
    TextView tv_left;
    @BindView(R.id.qwfl_result_tv_right)
    TextView tv_right;

    /**
     * 趣味分类大标题
     */
    private String bigTitle;
    /**
     * 左边的分类标题
     */
    private String leftTitle;
    /**
     * 右边的分类标题
     */
    private String rightTitle;
    /**
     * 左边的分类选项
     */
    private List<String> leftItems;
    /**
     * 右边的分类选项
     */
    private List<String> rightItems;

    /**
     * 样式下标 0:星际迷航 1:儿童乐园 2:快乐青蛙 3:海底水母
     */
    private int bgIndex;

    private ScheduledExecutorService timeExecutor;
    private long timerCount;
    private boolean isTimeOver = false;

    /**
     * 填充数据：
     * 趣味分类大标题、分类项标题、分类项、分类样式下标
     */
    public void fillData(String bigTitle, String leftTitle, String rightTitle, List<String> leftItems, List<String> rightItems, int bgIndex) {
        this.bigTitle = bigTitle;
        this.leftTitle = leftTitle;
        this.rightTitle = rightTitle;
        this.leftItems = leftItems;
        this.rightItems = rightItems;
        this.bgIndex = bgIndex;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qwfl_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        qwfl_view.setQwflResultInterface(this);
        qwfl_reset.setOnClickListener(this);

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
                            qwfl_time.setText("用时：".concat(QZXTools.getTransmitTime(timerCount)));
                        }
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        initQWFLData(true);

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

    /**
     * 初始化趣味分类的数据
     */
    private void initQWFLData(boolean init) {
        if (init) {
            //依据bgIndex设置对应的背景图片
            switch (bgIndex) {
                case 0:
                    qwfl_layout.setBackground(getResources().getDrawable(R.mipmap.qwfl_star_bg));
                    break;
                case 1:
                    qwfl_layout.setBackground(getResources().getDrawable(R.mipmap.qwfl_plane_bg));
                    break;
                case 2:
                    qwfl_layout.setBackground(getResources().getDrawable(R.mipmap.qwfl_frog_bg));
                    break;
                case 3:
                    qwfl_layout.setBackground(getResources().getDrawable(R.mipmap.qwfl_jellyfish_bg));
                    break;
            }

            qwfl_title.setText(bigTitle);

            qwfl_view.addContainerImages(bgIndex, leftTitle, rightTitle);
        }

        /**
         * 打乱顺序分布
         * */
        int totalSize = leftItems.size() + rightItems.size();
        int randomStart = (int) (Math.random() * totalSize);//起始种子[0.0,1.0)
        //自己设定规则
        for (int i = 0; i < totalSize; i++) {
            if (randomStart == 7 || randomStart == 8 || randomStart == 1) {
                //按照先左到右顺序依次排列
                if (i < leftItems.size()) {
                    qwfl_view.addItemView(leftItems.get(i), i, totalSize, true);
                } else {
                    qwfl_view.addItemView(rightItems.get(i - leftItems.size()), i, totalSize, false);
                }
            } else if (randomStart % 2 == 0) {
                //一个间隔一个右优先向显示
                if (i % 2 == 0) {
                    //0 2 4 6 ---> 0 1 2 3
                    if ((i / 2) < rightItems.size()) {
                        qwfl_view.addItemView(rightItems.get(i / 2), i, totalSize, false);
                    } else {
                        qwfl_view.addItemView(leftItems.get(i / 2), i, totalSize, true);
                    }
                } else {
                    // 1 3 5 7 ---> 0 1 2 3
                    if (((i + 1) / 2 - 1) < leftItems.size()) {
                        qwfl_view.addItemView(leftItems.get((i + 1) / 2 - 1), i, totalSize, true);
                    } else {
                        qwfl_view.addItemView(rightItems.get((i + 1) / 2 - 1), i, totalSize, false);
                    }
                }
            } else {
                //一个间隔一个左优先显示
                if (i % 2 == 0) {
                    if ((i / 2) < leftItems.size()) {
                        qwfl_view.addItemView(leftItems.get(i / 2), i, totalSize, true);
                    } else {
                        qwfl_view.addItemView(rightItems.get(i / 2), i, totalSize, false);
                    }
                } else {
                    if ((i + 1) / 2 - 1 < rightItems.size()) {
                        qwfl_view.addItemView(rightItems.get((i + 1) / 2 - 1), i, totalSize, false);
                    } else {
                        qwfl_view.addItemView(leftItems.get((i + 1) / 2 - 1), i, totalSize, true);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.qwfl_reset:
                //重置数据
                qwfl_result_layout.setVisibility(View.GONE);
                removeView();

                qwfl_view.resetView();

                initQWFLData(false);

                break;
        }
    }

    /**
     * 结果展示流布局视图
     */
    private void addView(String str, boolean isLeft) {
        ViewGroup.MarginLayoutParams lp = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.rightMargin = getResources().getDimensionPixelSize(R.dimen.x30);
        TextView mTvKey = new TextView(getContext());
        mTvKey.setTextColor(0xFF986533);
        mTvKey.setText(str);
        if (isLeft) {
            left_flow.addView(mTvKey, lp);
        } else {
            right_flow.addView(mTvKey, lp);
        }
    }

    private void removeView() {
        left_flow.removeAllViews();
        right_flow.removeAllViews();
    }

    @Override
    public void popQWFLResultDialog() {
        qwfl_result_layout.setVisibility(View.VISIBLE);

        tv_left.setText(leftTitle);
        tv_right.setText(rightTitle);

        for (String left : leftItems) {
            addView(left, true);
        }

        for (String right : rightItems) {
            addView(right, false);
        }
    }
}
