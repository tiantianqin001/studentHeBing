package com.telit.zhkt_three.Activity.AutonomousLearning;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.gyf.immersionbar.ImmersionBar;
import com.lzy.okserver.OkDownload;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.Fragment.AutonomousLearning.QuestionBankFragment;
import com.telit.zhkt_three.Fragment.AutonomousLearning.ReadFragment;
import com.telit.zhkt_three.Fragment.AutonomousLearning.ResourceFragment;
import com.telit.zhkt_three.Fragment.AutonomousLearning.TeachingMaterialFragment;
import com.telit.zhkt_three.Fragment.AutonomousLearning.VideoAudioPictureFragment;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.zbv.meeting.util.SharedPreferenceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/4/6 13:50
 * name;
 * overview:
 * usage: 自主学校
 * ******************************************************************
 */
public class AutonomousLearningActivity extends BaseActivity {
    private Unbinder unbinder;

    @BindView(R.id.learning_headLayout)
    CustomHeadLayout customHeadLayout;

    @BindView(R.id.learning_micro)
    LinearLayout layout_micro;
    @BindView(R.id.learning_audio)
    LinearLayout layout_audio;
    @BindView(R.id.learning_picture)
    LinearLayout layout_picture;
    @BindView(R.id.learning_resource)
    LinearLayout layout_resource;
    @BindView(R.id.learning_read)
    LinearLayout layout_read;
    @BindView(R.id.learning_book)
    LinearLayout layout_book;
    @BindView(R.id.learning_item_bank)
    LinearLayout layout_item_bank;

    private LinearLayout[] mTabs;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;

    /**
     * 埋点的计时：进入的开始时间
     */
    private long enterLearningTime;
    private static final String TAG="AutoLearenActivity";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous_learning);
        unbinder = ButterKnife.bind(this);
        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();

        initView();
        initData();

    }

    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initData() {
        //主要用于埋点的数据统计
        EventBus.getDefault().register(this);

        OkDownload.getInstance().getThreadPool().setCorePoolSize(5);

        //埋点的时间，首次进入时间
        enterLearningTime = System.currentTimeMillis();
        //埋点参与人数
        MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_ONE, -1, -1, "");
        //埋点学习次数
        MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_TWO, -1, -1, "");

        //设置头像信息等
        customHeadLayout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());

        fragments = new Fragment[]{VideoAudioPictureFragment.newInstance("3"), ResourceFragment.newInstance(""), ReadFragment.newInstance(), TeachingMaterialFragment.newInstance(), QuestionBankFragment.newInstance()};

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, fragments[0])
                .add(R.id.fragment_container, fragments[1]).hide(fragments[1]).show(fragments[0])
                .commit();
    }

    private void initView(){
        mTabs = new LinearLayout[5];
        mTabs[0] = layout_micro;
        mTabs[1] = layout_resource;
        mTabs[2] = layout_read;
        mTabs[3] = layout_book;
        mTabs[4] = layout_item_bank;
        mTabs[0].setSelected(true);//当前显示首页
    }

    /**
     * on tab clicked
     *
     * @param view
     */
    public void onTabClicked(View view) {
        switch (view.getId()) {
            case R.id.learning_micro:
                index = 0;
                break;
            case R.id.learning_resource:
                index = 1;
                break;
            case R.id.learning_read:
                index = 2;
                break;
            case R.id.learning_book:
                index = 3;
                break;
            case R.id.learning_item_bank:
                index = 4;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commit();
        }
        mTabs[currentTabIndex].setSelected(false);
        // set current tab selected
        mTabs[index].setSelected(true);
        currentTabIndex = index;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //埋点学习时长
        String selfLearning = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("SelfLearning");
        BuriedPointUtils.buriedPoint("2034","","","",selfLearning);

        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        QZXTools.logE("onRestart",null);
    }
}
