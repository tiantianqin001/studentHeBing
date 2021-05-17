package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.VPLearningAdapter;
import com.telit.zhkt_three.CusomPater;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.CustomView.LazyViewPager;
import com.telit.zhkt_three.Fragment.AfterHomeWork.CompletedHomeWorkFragment;
import com.telit.zhkt_three.Fragment.AfterHomeWork.ToDoHomeWorkFragment;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AfterHomeWorkActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;

    @BindView(R.id.after_work_head_layout)
    CustomHeadLayout customHeadLayout;
    @BindView(R.id.after_work_calendar)
    ImageView img_calendar;

    @BindView(R.id.after_work_todo)
    TextView tv_todo;
    @BindView(R.id.after_work_completed)
    TextView tv_completed;
    @BindView(R.id.after_work_indicator)
    View indicator;

    @BindView(R.id.after_work_viewPager)
    CusomPater viewPager;

    private List<Fragment> fgList;

    private int distance_dots;
    private int curPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_homework);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        //设置头像信息等

        customHeadLayout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());

        tv_todo.setOnClickListener(this);
        tv_completed.setOnClickListener(this);
        indicator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                distance_dots = tv_completed.getLeft() - tv_todo.getLeft();
                QZXTools.logE("distance_dots=" + distance_dots, null);
                //移除布局改变监听
                indicator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //添加Fragment
        fgList = new ArrayList<>();

        ToDoHomeWorkFragment toDoHomeWorkFragment = new ToDoHomeWorkFragment();
        CompletedHomeWorkFragment completedHomeWorkFragment = new CompletedHomeWorkFragment();

        fgList.add(toDoHomeWorkFragment);
        fgList.add(completedHomeWorkFragment);

        //处理ViewPager
        VPLearningAdapter vpAdapter = new VPLearningAdapter(getSupportFragmentManager());
        vpAdapter.setFragmentList(fgList);
        viewPager.setAdapter(vpAdapter);

        viewPager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) indicator.getLayoutParams();
                layoutParams.leftMargin = (int) (distance_dots * (positionOffset + position)
                        + getResources().getDimensionPixelSize(R.dimen.y339));
                indicator.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_todo.setTextColor(0xFF4562CF);
                    tv_completed.setTextColor(0xFFA2A2A2);
                } else {
                    tv_todo.setTextColor(0xFFA2A2A2);
                    tv_completed.setTextColor(0xFF4562CF);
                }
                curPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        //防止内存泄露
        QZXTools.setmToastNull();
        super.onDestroy();

        //退出作业埋点
        BuriedPointUtils.buriedPoint("2018","","","","");
    }

    private int preValue;

    @Override
    public void onClick(View v) {
        preValue = 0;
        switch (v.getId()) {
            case R.id.after_work_todo:

                if (curPosition == 1) {
                    curPosition = 0;
                    viewPager.setCurrentItem(curPosition, true);
                }
                break;
            case R.id.after_work_completed:

                if (curPosition == 0) {
                    curPosition = 1;
                    viewPager.setCurrentItem(curPosition, true);
                }
                break;
        }
    }

    @OnClick(R.id.iv_collect_question)
    void collectQuestion(){
        startActivity(new Intent(this, CollectQuestionActivity.class));
    }
}
