package com.telit.zhkt_three.Activity.AutonomousLearning;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.AutoLearning.PullOperationAdapter;
import com.telit.zhkt_three.Adapter.AutoLearning.RVAutoLearningAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.AutonomousLearning.ReadFragment;
import com.telit.zhkt_three.Fragment.AutonomousLearning.ResourceFragment;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionGrade;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionKnowledge;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionParam;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionSection;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionSubject;
import com.telit.zhkt_three.JavaBean.Gson.KnowledgeParamBean;
import com.telit.zhkt_three.JavaBean.Gson.KnowledgeSectionBean;
import com.telit.zhkt_three.JavaBean.Gson.ResourceConditionBean;
import com.telit.zhkt_three.JavaBean.Gson.ResourceInfoBean;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.JavaBean.Resource.LocalResourceRecord;
import com.telit.zhkt_three.JavaBean.Resource.ResourceBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.LocalResourceRecordDao;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 自主学习模块：微课、音频、图片、教材以及题库
 * <p>
 * 之前是单个Fragment的，点击切换
 * <p>
 * 资源保存在本地的位置：/storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/AutoLearningResources/
 * <p>
 * todo 一、修改成ViewPager; 二、删除列表的加载更多和刷新
 * <p>
 */
public class AutoLearningActivity extends BaseActivity implements View.OnClickListener, ToUsePullView.SpinnerClickInterface {
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
    LinearLayout learning_resource;
    @BindView(R.id.learning_read)
    LinearLayout learning_read;
    @BindView(R.id.learning_book)
    LinearLayout layout_book;
    @BindView(R.id.learning_item_bank)
    LinearLayout layout_item_bank;
    @BindView(R.id.learning_xRecycler)
    XRecyclerView xRecyclerView;

    @BindView(R.id.fl_list)
    FrameLayout fl_list;
    @BindView(R.id.fl_resource)
    FrameLayout fl_resource;

    //条件选择
    @BindView(R.id.learning_pull_all)
    LinearLayout layout_pull_all;
    @BindView(R.id.learning_pull_layout)
    LinearLayout layout_pull;
    @BindView(R.id.learning_pull_subject_layout)
    RelativeLayout subject_layout;
    @BindView(R.id.learning_pull_section_layout)
    RelativeLayout section_layout;
    @BindView(R.id.learning_pull_select_layout)
    RelativeLayout select_layout;
    @BindView(R.id.learning_pull_grade_layout)
    RelativeLayout grade_layout;
    @BindView(R.id.learning_pull_press_layout)
    RelativeLayout press_layout;
    @BindView(R.id.learning_pull_subject)
    ToUsePullView subject_view;
    @BindView(R.id.learning_pull_section)
    ToUsePullView section_view;
    @BindView(R.id.learning_pull_select)
    ToUsePullView select_view;
    @BindView(R.id.learning_pull_grade)
    ToUsePullView grade_view;
    @BindView(R.id.learning_pull_press)
    ToUsePullView press_view;

    private List<Fragment> fragments = new ArrayList<>();

    //缺少网络和资源视图
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    //-------------------------------------------侧拉下载文件管理,题库下不可见
    @BindView(R.id.auto_learning_pull_tag)
    FrameLayout auto_learning_pull_tag;
    @BindView(R.id.auto_learning_pull_icon)
    ImageView auto_learning_pull_icon;

    //方式一：checkbox to delete
    @BindView(R.id.pull_linear_red)
    LinearLayout pull_linear_red;
    @BindView(R.id.pull_tv_edit)
    TextView pull_tv_edit;
    @BindView(R.id.pull_cb_all)
    CheckBox pull_cb_all;
    @BindView(R.id.pull_recycler)
    RecyclerView pull_recycler;
    @BindView(R.id.pull_tv_del)
    TextView pull_tv_del;

    //todo 方式二：侧拉删除加全部删除

    //动画
    private Animation FromRightToLeftAnimation;
    private Animation FromLeftToRightAnimation;

    private List<LocalResourceRecord> pullOperationBeans;

    private PullOperationAdapter pullOperationAdapter;

    //-------------------------------------------侧拉下载文件管理,题库下不可见

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private RVAutoLearningAdapter rvAutoLearningAdapter;
    private List<FillResource> fillResourceList;

    //汉字为key，下标为值
    //学段
    private Map<String, String> sectionMap;
    //学科
    private Map<String, String> subjectMap;
    //年级
    private Map<String, String> gradeMap;
    //出版社
    private Map<String, String> pressMap;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operate_Section_Success = 2;
    private static final int Operate_Subject_Grade_Success = 3;
    private static final int Operate_Resource_Condition_Success = 4;
    private static final int Operate_Resource_Success = 5;

    private static boolean isShow=false;

    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(AutoLearningActivity.this, "当前网络不佳....", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(AutoLearningActivity.this, "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }

                    break;
                case Operate_Section_Success:
                    if (isShow){
                        List<String> sectionList = new ArrayList<String>(sectionMap.keySet());
                        section_view.setDataList(sectionList);
                    }

                    break;
                case Operate_Subject_Grade_Success:
                    if (isShow){
                        countAdd++;

                        List<String> subjectList = new ArrayList<String>(subjectMap.keySet());
                        subject_view.setDataList(subjectList);

                        List<String> gradeList = new ArrayList<String>(gradeMap.keySet());
                        grade_view.setDataList(gradeList);

                        List<String> pressList = new ArrayList<String>(pressMap.keySet());
                        press_view.setDataList(pressList);
                        if (pressList != null && pressList.size() > 0) {
                            press_view.setPullContent(pressList.get(0));
                        }

                        if (countAdd == countRequest) {
                            if (circleProgressDialogFragment != null) {
                                circleProgressDialogFragment.dismissAllowingStateLoss();
                                circleProgressDialogFragment = null;
                            }
                        }

                        //题库的主体内容数据
                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }

                        if (fillResourceList.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }
                        rvAutoLearningAdapter.notifyDataSetChanged();

                        //没有分页
                        xRecyclerView.setNoMore(true);
                    }

                    break;
                case Operate_Resource_Condition_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        List<String> xuekeList = new ArrayList<String>(subjectMap.keySet());
                        subject_view.setDataList(xuekeList);

                        List<String> nianjiList = new ArrayList<String>(gradeMap.keySet());
                        grade_view.setDataList(nianjiList);

                        List<String> chubansheList = new ArrayList<String>(pressMap.keySet());
                        press_view.setDataList(chubansheList);

                        List<String> xueqiList = new ArrayList<String>(sectionMap.keySet());
                        section_view.setDataList(xueqiList);

                        //设置默认值，并且自动加载资源内容
                        subject_view.setPullContent(xuekeList.get(0));
                        grade_view.setPullContent(nianjiList.get(0));
                        press_view.setPullContent(chubansheList.get(0));
                        section_view.setPullContent(xueqiList.get(0));

                        curPageNo = 1;

                        fetchNetworkForResourceContent(false, subjectMap.get(subject_view.getPullContent()),
                                gradeMap.get(grade_view.getPullContent()),
                                sectionMap.get(section_view.getPullContent()),
                                pressMap.get(press_view.getPullContent()));

                    }

                    break;
                case Operate_Resource_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }

                        if (fillResourceList.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }
                        rvAutoLearningAdapter.notifyDataSetChanged();
                    }

                    break;
            }
        }
    };

    /**
     * 埋点的计时：进入的开始时间
     */
    private long enterLearningTime;
    private static final String TAG="AutoLearenActivity";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_learning);
        unbinder = ButterKnife.bind(this);
        isShow=true;
        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        //主要用于埋点的数据统计
        EventBus.getDefault().register(this);

        //埋点的时间，首次进入时间
        enterLearningTime = System.currentTimeMillis();
        //埋点参与人数
        MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_ONE, -1, -1, "");
        //埋点学习次数
        MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_TWO, -1, -1, "");

        //设置头像信息等
        customHeadLayout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());

        //网格布局，一行四列
        fillResourceList = new ArrayList<>();
        //显示时间线
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        //设置没有更多数据的显示
        xRecyclerView.getDefaultFootView().setNoMoreHint("");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        xRecyclerView.setLayoutManager(gridLayoutManager);
        xRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvAutoLearningAdapter = new RVAutoLearningAdapter(this, fillResourceList,"1");
        xRecyclerView.setAdapter(rvAutoLearningAdapter);

        xRecyclerView.setItemViewCacheSize(20);

        //加载更多回调
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    curPageNo = 1;
                    if (isItemBank) {
                        fetchNetworkForSubjectGrade(sectionMap.get(section_view.getPullContent()), "1",
                                subjectMap.get(subject_view.getPullContent()), pressMap.get(press_view.getPullContent()));
                    } else {
                        fetchNetworkForResourceContent(false, subjectMap.get(subject_view.getPullContent()),
                                gradeMap.get(grade_view.getPullContent()), sectionMap.get(section_view.getPullContent()),
                                pressMap.get(press_view.getPullContent()));
                    }
                }catch (Exception e){
                    e.fillInStackTrace();
                    QZXTools.popToast(MyApplication.getInstance(),"网络不可用",true);
                }

            }

            @Override
            public void onLoadMore() {
                try {
                    curPageNo++;
                    if (isItemBank) {
                    } else {
                        fetchNetworkForResourceContent(true, subjectMap.get(subject_view.getPullContent()),
                                gradeMap.get(grade_view.getPullContent()), sectionMap.get(section_view.getPullContent()),
                                pressMap.get(press_view.getPullContent()));
                    }
                }catch (Exception e){
                    e.fillInStackTrace();
                    QZXTools.popToast(MyApplication.getInstance(),"网络不可用",true);
                }
            }
        });

        link_network.setOnClickListener(this);
        //首先微课被选中
        layout_micro.setOnClickListener(this);
        layout_audio.setOnClickListener(this);
        layout_picture.setOnClickListener(this);
        learning_resource.setOnClickListener(this);
        learning_read.setOnClickListener(this);
        layout_book.setOnClickListener(this);
        layout_item_bank.setOnClickListener(this);

        layout_pull_all.setOnClickListener(this);
        //这个点击其实没什么作用：仅仅防止非遮罩层才消失
        layout_pull.setOnClickListener(this);

        //使能第一个
        layout_micro.setSelected(true);
        auto_learning_pull_tag.setVisibility(View.VISIBLE);

        //一开始统一显示无资源，用户条件选择查询后得到正式数据
        leak_resource.setVisibility(View.GONE);

        subject_view.setSpinnerClick(this);
        section_view.setSpinnerClick(this);
        select_view.setSpinnerClick(this);
        grade_view.setSpinnerClick(this);
        press_view.setSpinnerClick(this);

        subjectMap = new LinkedHashMap<>();
        sectionMap = new LinkedHashMap<>();
        gradeMap = new LinkedHashMap<>();
        pressMap = new LinkedHashMap<>();


        //一开始出版社不可见
        press_layout.setVisibility(View.GONE);

        //只有手动组卷是写死的
        List<String> papers = new ArrayList<>();
        papers.add("知识点");
        papers.add("教材");
        select_view.setDataList(papers);

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        //请求资源，一开始请求微课的资源条件
        fetchNetworkForResourceCondition();

        //-----------------------------------------处理侧拉

        //侧拉点击
        auto_learning_pull_tag.setOnClickListener(this);

        //侧拉动画
        FromRightToLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_show);
        FromLeftToRightAnimation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_hide);

        LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();
        pullOperationBeans = localResourceRecordDao.queryBuilder().list();

        pull_tv_edit.setOnClickListener(this);
        pull_tv_del.setOnClickListener(this);

        pull_cb_all.setOnClickListener(this);

        pull_recycler.setLayoutManager(new LinearLayoutManager(this));

        pullOperationAdapter = new PullOperationAdapter(this, pullOperationBeans);

        pullOperationAdapter.setCheckedInterface(new PullOperationAdapter.CheckedInterface() {
            @Override
            public void checkedStatus(boolean hasChecked, int position) {
                boolean allChoosed = true;

                pullOperationBeans.get(position).setIsChoosed(hasChecked);
                pull_tv_del.setSelected(hasChecked);

                for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                    if (!localResourceRecord.getIsChoosed()) {
                        allChoosed = false;
                    }
                }

                //联动
                pull_cb_all.setChecked(allChoosed);
            }
        });

        pull_recycler.setAdapter(pullOperationAdapter);

        if (pullOperationBeans == null || pullOperationBeans.size() <= 0) {
            auto_learning_pull_tag.setVisibility(View.GONE);
        }

        initFragment();
    }

    private void initFragment(){
        fragments.add(ResourceFragment.newInstance(""));
        fragments.add(ReadFragment.newInstance());
    }

    /**
     * todo 下载中退出Activity怎么办？
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        isShow=false;

        EventBus.getDefault().unregister(this);

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }

        //防止内存泄露
        mHandler.removeCallbacksAndMessages(null);
        OkHttp3_0Utils.getInstance().cleanHandler();
        QZXTools.setmToastNull();
        super.onDestroy();

        //埋点学习时长
        long exitLearningTime = System.currentTimeMillis();
 /*       MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FIVE
                , (exitLearningTime - enterLearningTime), -1, "");*/

        String selfLearning = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("SelfLearning");
        BuriedPointUtils.buriedPoint("2034","","","",selfLearning);
    }

    /**
     * 处理下载的文件的侧拉状态更新：下载后通知更新
     */
    @Subscriber(tag = Constant.Auto_Learning_Update, mode = ThreadMode.MAIN)
    public void updateResources(String type) {
        pullOperationBeans = null;
        LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();
        pullOperationBeans = localResourceRecordDao.queryBuilder().list();
        if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
            switch (type) {
                case "item_bank":
                    auto_learning_pull_tag.setVisibility(View.GONE);
                    break;
                default:
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                    break;
            }
            //添加设置这一句，不然有问题？
            pullOperationAdapter.setPullOperationBeans(pullOperationBeans);
            pullOperationAdapter.notifyDataSetChanged();
        } else {
            auto_learning_pull_tag.setVisibility(View.GONE);
        }
    }

    /**
     * 最后一次进度消失
     */
    private int countRequest;
    private int countAdd;

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "【基础信息】查询成功！",
     * "result": [
     * {
     * "id": 1,
     * "xd": 1,
     * "xdName": "小学"
     * }
     * ],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 查询学段
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForSection() {
        subjectMap.clear();
        sectionMap.clear();
        gradeMap.clear();
        pressMap.clear();

        subject_view.setHintText("选择学科");
        section_view.setHintText("选择学段");
        select_view.setHintText("手动组卷");
        grade_view.setHintText("选择年级");
        press_view.setHintText("出版社");

        select_layout.setVisibility(View.VISIBLE);
        press_layout.setVisibility(View.GONE);

        countAdd = 0;

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        String url = UrlUtils.BaseUrl + UrlUtils.QueryKnowledgeSection;

        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE( "onFailure: "+e.getMessage(),e);
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
//                            QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        KnowledgeSectionBean knowledgeSectionBean = gson.fromJson(resultJson, KnowledgeSectionBean.class);
//                    QZXTools.logE("knowledgeSectionBean=" + knowledgeSectionBean, null);

                        countRequest = knowledgeSectionBean.getResult().size();

                        for (int i = 0; i < knowledgeSectionBean.getResult().size(); i++) {
                            QuestionSection questionSection = knowledgeSectionBean.getResult().get(i);
                            sectionMap.put(questionSection.getXdName(), questionSection.getXd() + "");
                            //请求对应的学科和年级
                            fetchNetworkForSubjectGrade(questionSection.getXd() + "", "0", null, null);
                        }
                        mHandler.sendEmptyMessage(Operate_Section_Success);
                    }catch (Exception e){
                        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Error404);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    private void fetchNetworkForSubjectGrade(String xd, String isSearchChapter, String subjectId, String pressId) {
        String url = UrlUtils.BaseUrl + UrlUtils.QueryKnowledgeSubjectGrade;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("xd", xd);
        // 0:查询知识点  1查询教材
        mapParams.put("isSearchChapter", isSearchChapter);
        if (!TextUtils.isEmpty(subjectId)) {
            mapParams.put("chid", subjectId);
        }

        if (!TextUtils.isEmpty(pressId)) {
            mapParams.put("press", pressId);
        }

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        //查询章节数据
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    try {
                        String resultJson = response.body().string();
//                            QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        KnowledgeParamBean knowledgeSubjectGradeBean = gson.fromJson(resultJson, KnowledgeParamBean.class);
//                    QZXTools.logE("knowledgeSubjectGradeBean=" + knowledgeSubjectGradeBean, null);

                        QuestionParam questionParam = knowledgeSubjectGradeBean.getResult();

                        //科目
                        if (questionParam.getQuestionSubjects() != null && questionParam.getQuestionSubjects().size() > 0) {
                            for (int i = 0; i < questionParam.getQuestionSubjects().size(); i++) {
                                QuestionSubject questionSubject = knowledgeSubjectGradeBean.getResult().getQuestionSubjects().get(i);
                                subjectMap.put(questionSubject.getChname(), questionSubject.getChid() + "");
                            }
                        }

                        //年级
                        if (questionParam.getQuestionGrades() != null && questionParam.getQuestionGrades().size() > 0) {
                            for (int i = 0; i < questionParam.getQuestionGrades().size(); i++) {
                                QuestionGrade questionGrade = knowledgeSubjectGradeBean.getResult().getQuestionGrades().get(i);
                                gradeMap.put(questionGrade.getGradeName(), questionGrade.getGradeId() + "");
                            }
                        }

                        //出版社
                        if (questionParam.getQuestionEdition() != null && questionParam.getQuestionEdition().size() > 0) {
                            for (int i = 0; i < questionParam.getQuestionEdition().size(); i++) {
                                QuestionKnowledge questionKnowledge = questionParam.getQuestionEdition().get(i);
                                //传递的是知识点id
                                pressMap.put(questionKnowledge.getName(), questionKnowledge.getKnowledgeId() + "");
                            }
                        }

                        //主体内容QuestionGradeVolum
                        if (questionParam.getQuestionGradeVolum() != null && questionParam.getQuestionGradeVolum().size() > 0) {

                            //清空之前的数据
                            fillResourceList.clear();

                            for (int i = 0; i < questionParam.getQuestionGradeVolum().size(); i++) {
                                //将QuestionKnowledge转化为FillResource
                                QuestionKnowledge questionKnowledge = questionParam.getQuestionGradeVolum().get(i);

                                FillResource fillResource = new FillResource();
                                fillResource.setCover("");
                                fillResource.setGradename(questionKnowledge.getName());
                                fillResource.setTermname("");

                                displayItemBankText(questionKnowledge.getParentId(), questionKnowledge.getXd(), questionKnowledge.getChid());

                                fillResource.setPressname(pressName);
                                fillResource.setSubjectName(subjectName);

                                if (type.equals("1010")) {
                                    fillResource.setTeachingMaterial(true);
                                } else {
                                    fillResource.setTeachingMaterial(false);
                                }
                                fillResource.setTitle(TitleText);
                                fillResource.setType(type);
                                fillResource.setItemBank(true);


                                fillResource.setChid(questionKnowledge.getChid());
                                fillResource.setKnowledgeId(questionKnowledge.getKnowledgeId() + "");
                                fillResource.setXd(questionKnowledge.getXd());
                                fillResource.setId(questionKnowledge.getId()+"");

                                fillResourceList.add(fillResource);
                            }
                        }

                        mHandler.sendEmptyMessage(Operate_Subject_Grade_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Error404);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "操作成功",
     * "result": [
     * [
     * {
     * "id": "1010",
     * "name": "电子教材",
     * "code": null
     * },
     * {
     * "id": "0",
     * "name": "文档",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "图片",
     * "code": null
     * },
     * {
     * "id": "2",
     * "name": "音频",
     * "code": null
     * },
     * {
     * "id": "3",
     * "name": "视频",
     * "code": null
     * },
     * {
     * "id": "4",
     * "name": "压缩包",
     * "code": null
     * }
     * ],
     * [
     * {
     * "id": null,
     * "name": "一年级",
     * "code": "1"
     * },
     * {
     * "id": null,
     * "name": "二年级",
     * "code": "2"
     * },
     * {
     * "id": null,
     * "name": "三年级",
     * "code": "3"
     * },
     * {
     * "id": null,
     * "name": "四年级",
     * "code": "4"
     * },
     * {
     * "id": null,
     * "name": "五年级",
     * "code": "5"
     * },
     * {
     * "id": null,
     * "name": "六年级",
     * "code": "6"
     * },
     * {
     * "id": null,
     * "name": "七年级",
     * "code": "7"
     * },
     * {
     * "id": null,
     * "name": "八年级",
     * "code": "8"
     * },
     * {
     * "id": null,
     * "name": "九年级",
     * "code": "9"
     * },
     * {
     * "id": null,
     * "name": "高一",
     * "code": "10"
     * },
     * {
     * "id": null,
     * "name": "高二",
     * "code": "11"
     * },
     * {
     * "id": null,
     * "name": "高三",
     * "code": "12"
     * }
     * ],
     * [
     * {
     * "id": "0",
     * "name": "语文",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "数学",
     * "code": null
     * },
     * {
     * "id": "10",
     * "name": "思想品德",
     * "code": null
     * },
     * {
     * "id": "11",
     * "name": "历史",
     * "code": null
     * },
     * {
     * "id": "12",
     * "name": "地理",
     * "code": null
     * },
     * {
     * "id": "13",
     * "name": "物理",
     * "code": null
     * },
     * {
     * "id": "14",
     * "name": "化学",
     * "code": null
     * },
     * {
     * "id": "15",
     * "name": "生物",
     * "code": null
     * },
     * {
     * "id": "16",
     * "name": "体育",
     * "code": null
     * },
     * {
     * "id": "17",
     * "name": "思维训练",
     * "code": null
     * },
     * {
     * "id": "18",
     * "name": "阅读",
     * "code": null
     * },
     * {
     * "id": "19",
     * "name": "健康",
     * "code": null
     * },
     * {
     * "id": "2",
     * "name": "英语",
     * "code": null
     * },
     * {
     * "id": "20",
     * "name": "口语",
     * "code": null
     * },
     * {
     * "id": "21",
     * "name": "班会",
     * "code": null
     * },
     * {
     * "id": "22",
     * "name": "自习",
     * "code": null
     * },
     * {
     * "id": "28",
     * "name": "活动",
     * "code": null
     * },
     * {
     * "id": "29",
     * "name": "政治",
     * "code": null
     * },
     * {
     * "id": "3",
     * "name": "品德与生活",
     * "code": null
     * },
     * {
     * "id": "30",
     * "name": "书法",
     * "code": null
     * },
     * {
     * "id": "31",
     * "name": "道德与法治",
     * "code": null
     * },
     * {
     * "id": "32",
     * "name": "数活",
     * "code": null
     * },
     * {
     * "id": "33",
     * "name": "手工",
     * "code": null
     * },
     * {
     * "id": "34",
     * "name": "诵读",
     * "code": null
     * },
     * {
     * "id": "35",
     * "name": "周会",
     * "code": null
     * },
     * {
     * "id": "36",
     * "name": "体活",
     * "code": null
     * },
     * {
     * "id": "37",
     * "name": "语活",
     * "code": null
     * },
     * {
     * "id": "38",
     * "name": "班会/国防",
     * "code": null
     * },
     * {
     * "id": "39",
     * "name": "阅读/写作",
     * "code": null
     * },
     * {
     * "id": "4",
     * "name": "品德与社会",
     * "code": null
     * },
     * {
     * "id": "40",
     * "name": "英活",
     * "code": null
     * },
     * {
     * "id": "41",
     * "name": "国防",
     * "code": null
     * },
     * {
     * "id": "42",
     * "name": "2+1",
     * "code": null
     * },
     * {
     * "id": "5",
     * "name": "音乐",
     * "code": null
     * },
     * {
     * "id": "6",
     * "name": "美术",
     * "code": null
     * },
     * {
     * "id": "7",
     * "name": "信息技术",
     * "code": null
     * },
     * {
     * "id": "8",
     * "name": "科学",
     * "code": null
     * },
     * {
     * "id": "9",
     * "name": "综合实践",
     * "code": null
     * }
     * ],
     * [
     * {
     * "id": "0",
     * "name": "上学期",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "下学期",
     * "code": null
     * },
     * {
     * "id": "9",
     * "name": "全册",
     * "code": null
     * }
     * ],
     * [
     * {
     * "id": "0",
     * "name": "人教版",
     * "code": null
     * },
     * {
     * "id": "11",
     * "name": "人教版2016",
     * "code": null
     * },
     * {
     * "id": "1",
     * "name": "苏教版",
     * "code": null
     * },
     * {
     * "id": "2",
     * "name": "外研版",
     * "code": null
     * },
     * {
     * "id": "40",
     * "name": "清华大学出版社",
     * "code": null
     * },
     * {
     * "id": "42",
     * "name": "部编版",
     * "code": null
     * },
     * {
     * "id": "30",
     * "name": "沪科版",
     * "code": null
     * },
     * {
     * "id": "87",
     * "name": "人民美术出版社",
     * "code": null
     * },
     * {
     * "id": "88",
     * "name": "人民音乐出版社",
     * "code": null
     * },
     * {
     * "id": "90",
     * "name": "安徽大学出版社",
     * "code": null
     * },
     * {
     * "id": "91",
     * "name": "合肥工业大学出版社",
     * "code": null
     * },
     * {
     * "id": "92",
     * "name": "电子工业出版社",
     * "code": null
     * },
     * {
     * "id": "93",
     * "name": "教育科学出版社",
     * "code": null
     * },
     * {
     * "id": "98",
     * "name": "西冷印社出版社",
     * "code": null
     * },
     * {
     * "id": "99",
     * "name": "其它",
     * "code": null
     * }
     * ]
     * ],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 请求资源条件
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForResourceCondition() {
        //清空资源条件
        subjectMap.clear();
        sectionMap.clear();
        gradeMap.clear();
        pressMap.clear();

        subject_view.setHintText("选择学科");
        section_view.setHintText("选择学期");
        grade_view.setHintText("选择年级");
        press_view.setHintText("出版社");

        select_layout.setVisibility(View.GONE);
        press_layout.setVisibility(View.VISIBLE);

        String url = UrlUtils.BaseUrl + UrlUtils.ConditionResource;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("schoolid", UserUtils.getStudentId());

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("onFailure: "+e.getMessage(),e);
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
//                    QZXTools.logE("resultJson=" + resultJson, null);

                    try {
                        Gson gson = new Gson();
                        ResourceConditionBean resourceConditionBean = gson.fromJson(resultJson, ResourceConditionBean.class);
                        ResourceConditionBean.ResultBean result = resourceConditionBean.getResult();

                        //获取年级
                        List<ResourceConditionBean.ResultBean.PeriodBean> period = result.getPeriod();
                        for (ResourceConditionBean.ResultBean.PeriodBean periodBean : period) {
                           // gradeMap.put(periodBean.getName(),periodBean.getId());
                        }
                        //获取学科
                        List<ResourceConditionBean.ResultBean.SubjectBean> subject = result.getSubject();
                        for (ResourceConditionBean.ResultBean.SubjectBean subjectBean : subject) {
                            subjectMap.put(subjectBean.getName(),subjectBean.getId());
                        }
                        //获取年级
                        List<ResourceConditionBean.ResultBean.GradeBean> grade = result.getGrade();
                        for (ResourceConditionBean.ResultBean.GradeBean gradeBean : grade) {
                            gradeMap.put(gradeBean.getName(), gradeBean.getCode());
                        }

                        QZXTools.logE("onResponse: "+gradeMap,null);
                        //出版社
                        List<ResourceConditionBean.ResultBean.PressBean> press = result.getPress();
                        for (ResourceConditionBean.ResultBean.PressBean pressBean : press) {
                            pressMap.put(pressBean.getName(), pressBean.getId());
                        }
                        //获得学期
                        List<ResourceConditionBean.ResultBean.TermBean> term = result.getTerm();
                        for (ResourceConditionBean.ResultBean.TermBean termBean : term) {
                            sectionMap.put(termBean.getName(), termBean.getId());
                        }


                        QZXTools.logE("sectionMap: "+new Gson().toJson(sectionMap),null);


                        mHandler.sendEmptyMessage(Operate_Resource_Condition_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    private int curPageNo = 1;
    private String type = "3";

    /**
     * 查询具体的数据信息
     *
     * @param isLoadingMore 是否是加载更多，如果是的话不需要清空list集合
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForResourceContent(boolean isLoadingMore, String subjectId, String gradeId, String termId, String pressId) {

        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        QZXTools.logE("isLoadingMore=" + isLoadingMore + ";subjectId=" + subjectId
                + ";gradeId=" + gradeId + ";termId=" + termId + ";pressId=" + pressId + ";curPageNo="
                + curPageNo + ";type=" + type+";url="+UrlUtils.BaseUrl + UrlUtils.OldResource, null);

        if (!isLoadingMore) {
            fillResourceList.clear();
            //如果这里不添加刷新的话可能有问题：Scrapped or attached views may not be recycled
            rvAutoLearningAdapter.notifyDataSetChanged();
        }

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        String url = UrlUtils.BaseUrl + UrlUtils.OldResource;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("pageNo", curPageNo + "");
        paraMap.put("pageSize", "30");
        paraMap.put("suffix", type);
        if (!TextUtils.isEmpty(subjectId)) {
            paraMap.put("subjectid", subjectId);
        }

        if (!TextUtils.isEmpty(gradeId)) {
            paraMap.put("gradeid", gradeId);
        }

        if (!TextUtils.isEmpty(termId)) {
            paraMap.put("term", termId);
        }

        if (!TextUtils.isEmpty(pressId)) {
            paraMap.put("press", pressId);
        }

        QZXTools.logE("paraMap:"+new Gson().toJson(paraMap),null);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("onFailure: "+e.getMessage(),e);
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        ResourceInfoBean resourceInfoBean = gson.fromJson(resultJson, ResourceInfoBean.class);
                        List<ResourceBean> resourceBeanList = resourceInfoBean.getResult();
                        for (int i = 0; i < resourceBeanList.size(); i++) {
                            FillResource fillResource = new FillResource();
                            fillResource.setId(resourceBeanList.get(i).getId());
                            fillResource.setCover(resourceBeanList.get(i).getCover());
                            fillResource.setGradename(resourceBeanList.get(i).getGradename());
                            fillResource.setPressname(resourceBeanList.get(i).getPressname());
                            if (type.equals("1010")) {
                                fillResource.setTeachingMaterial(true);
                            } else {
                                fillResource.setTeachingMaterial(false);
                            }
                            fillResource.setTermname(resourceBeanList.get(i).getTermname());
                            fillResource.setTitle(resourceBeanList.get(i).getTitle());
                            fillResource.setType(type);
                            fillResource.setItemBank(false);
                            fillResource.setSubjectName(subject_view.getPullContent());
                            fillResource.setSubjectId(subjectMap.get(subject_view.getPullContent()));

                            fillResourceList.add(fillResource);
                        }
                        mHandler.sendEmptyMessage(Operate_Resource_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * 是否是题库
     */
    private boolean isItemBank = false;

    /**
     * 侧拉动画参数s
     */
    private boolean isShown = false;
    private int preValue;

    /**
     * 微课：3
     * 音频：2
     * 图片：1
     * 电子课本：1010
     * <p>
     * 点击切换目录首先查询资源条件，然后手动选择条件后展示服务资源
     * <p>
     * 现修改成自动获取默认条件，如果用户选择再筛选服务资源
     * 微课、音频、图片和教材： fetchNetworkForResourceCondition()
     * 题库：fetchNetworkForSection() 先查学段，然后依据学段查学科等信息，再查资源，这个太麻烦了，操作不方便，需要用户手动选择服务资源
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.learning_micro:
                fl_list.setVisibility(View.VISIBLE);
                fl_resource.setVisibility(View.GONE);

                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "3";

                layout_micro.setSelected(true);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                learning_resource.setSelected(false);
                learning_read.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_audio:
                fl_list.setVisibility(View.VISIBLE);
                fl_resource.setVisibility(View.GONE);

                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "2";

                layout_micro.setSelected(false);
                layout_audio.setSelected(true);
                layout_picture.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_picture:
                fl_list.setVisibility(View.VISIBLE);
                fl_resource.setVisibility(View.GONE);

                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "1";

                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(true);
                learning_resource.setSelected(false);
                learning_read.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_book:
                fl_list.setVisibility(View.VISIBLE);
                fl_resource.setVisibility(View.GONE);

                resetViewInterface(false);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.VISIBLE);
                }

                type = "1010";

                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                learning_resource.setSelected(false);
                learning_read.setSelected(false);
                layout_book.setSelected(true);
                layout_item_bank.setSelected(false);

                layout_pull_all.setVisibility(View.VISIBLE);

                fetchNetworkForResourceCondition();
                break;
            case R.id.learning_item_bank:
                fl_list.setVisibility(View.VISIBLE);
                fl_resource.setVisibility(View.GONE);

                if (pullOperationBeans != null && pullOperationBeans.size() > 0) {
                    auto_learning_pull_tag.setVisibility(View.GONE);
                }

                type = "item_bank";

                resetViewInterface(true);

                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                learning_resource.setSelected(false);
                learning_read.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(true);

                layout_pull_all.setVisibility(View.VISIBLE);

                /**
                 * 这里是点击题库开始请求学段信息，然后依据学段信息请求学科和年级
                 * 请求学段信息
                 * */
                fetchNetworkForSection();
                break;
            case R.id.learning_resource:
                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                learning_resource.setSelected(true);
                learning_read.setSelected(false);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                fl_list.setVisibility(View.GONE);
                fl_resource.setVisibility(View.VISIBLE);

                showResourceOrReadFragment(0,1);
                break;
            case R.id.learning_read:
                layout_micro.setSelected(false);
                layout_audio.setSelected(false);
                layout_picture.setSelected(false);
                learning_resource.setSelected(false);
                learning_read.setSelected(true);
                layout_book.setSelected(false);
                layout_item_bank.setSelected(false);

                fl_list.setVisibility(View.GONE);
                fl_resource.setVisibility(View.VISIBLE);

                showResourceOrReadFragment(1,0);
                break;
            case R.id.link_network:
                QZXTools.enterWifiSetting(this);
                break;
            case R.id.learning_pull_all:
                //如果下拉显示中则不取消一级背景色
                if (subject_view.pullViewPopShown() || section_view.pullViewPopShown()
                        || select_view.pullViewPopShown() || grade_view.pullViewPopShown()
                        || press_view.pullViewPopShown()) {
                    return;
                }
                layout_pull_all.setVisibility(View.GONE);
                break;
            case R.id.auto_learning_pull_tag:
                preValue = 0;
                if (isShown) {
                    //设置gone是无法测量elecRes_pull_content_layout.getMeasuredWidth()
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pull_linear_red.getMeasuredWidth());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                                    auto_learning_pull_tag.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.rightMargin -= offset;
                            auto_learning_pull_tag.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(auto_learning_pull_icon, 180.0f, 0.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            pull_linear_red.setVisibility(View.INVISIBLE);
                            isShown = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    pull_linear_red.startAnimation(FromLeftToRightAnimation);
                    valueAnimator.start();
                } else {
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pull_linear_red.getMeasuredWidth());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)
                                    auto_learning_pull_tag.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.rightMargin += offset;
                            auto_learning_pull_tag.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(auto_learning_pull_icon, 0f, 180.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isShown = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    pull_linear_red.setVisibility(View.VISIBLE);
                    pull_linear_red.startAnimation(FromRightToLeftAnimation);
                    valueAnimator.start();
                }
                break;
            case R.id.pull_tv_edit:
                if (pull_tv_edit.getText().toString().trim().equals("编辑")) {
                    pull_cb_all.setVisibility(View.VISIBLE);
                    //但是出于未selected状态
                    pull_tv_del.setVisibility(View.VISIBLE);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setCanChecked(true);
                    }

                    pullOperationAdapter.notifyDataSetChanged();

                    pull_tv_edit.setText("取消");
                } else if (pull_tv_edit.getText().toString().trim().equals("取消")) {
                    pull_cb_all.setVisibility(View.GONE);
                    pull_tv_del.setVisibility(View.GONE);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setCanChecked(false);
                    }

                    pullOperationAdapter.notifyDataSetChanged();

                    pull_tv_edit.setText("编辑");
                }
                break;
            case R.id.pull_tv_del:
                if (pull_tv_del.isSelected()) {
                    LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession()
                            .getLocalResourceRecordDao();
                    List<LocalResourceRecord> delResources = new ArrayList<>();
                    //这个才能删除，说明有选中项
                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        if (localResourceRecord.getIsChoosed()) {
                            delResources.add(localResourceRecord);
                        }
                    }
                    pullOperationBeans.removeAll(delResources);
                    localResourceRecordDao.deleteInTx(delResources);
                    pullOperationAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.pull_cb_all:
                //后起之秀:在onCheckedChanged变化之后触发，所以实际上没有选中时点击isChecked=true,所以反着来就行
                if (pull_cb_all.isChecked()) {
                    pull_cb_all.setChecked(true);

                    //全部取消
                    pull_tv_del.setSelected(true);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setIsChoosed(true);
                    }

                    pullOperationAdapter.notifyDataSetChanged();
                } else {
                    pull_cb_all.setChecked(false);

                    //全选
                    pull_tv_del.setSelected(false);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setIsChoosed(false);
                    }

                    pullOperationAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    /**
     * 展示省平台资源,课外读物
     */
    private void showResourceOrReadFragment(int showIndex,int hideIndex){
        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
        trx.hide(fragments.get(hideIndex));
        if (!fragments.get(showIndex).isAdded()) {
            trx.add(R.id.fl_resource, fragments.get(showIndex));
        }
        trx.show(fragments.get(showIndex)).commit();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void spinnerClick(View parent, String text) {
        switch (parent.getId()) {
            case R.id.learning_pull_subject:
                subject_view.setPullContent(text);
                break;
            case R.id.learning_pull_section:
                section_view.setPullContent(text);
                break;
            case R.id.learning_pull_select:
                //知识点数据单独直接进入知识点页面
                if (text.equals(getResources().getString(R.string.knowledge_point))) {
                    if (subject_view.getPullContent().equals("") || section_view.getPullContent().equals("")) {
                        QZXTools.popToast(this, "请先选择学段和学科", false);
                        return;
                    }
                    select_view.setPullContent(text);
                    press_layout.setVisibility(View.GONE);
                    //是知识点跳转到知识点详情页面,传入学科和学段
                    Intent intent = new Intent(this, ItemBankKnowledgeActivity.class);
                    //传入学段和学科
                    String learning_section = sectionMap.get(section_view.getPullContent());
                    String subject = subjectMap.get(subject_view.getPullContent());
                    QZXTools.logE("learning_section=" + learning_section + ";subject=" + subject, null);
                    intent.putExtra("learning_section", learning_section);
                    intent.putExtra("subject", subject);
                    startActivity(intent);
                    return;
                } else if (text.equals(getResources().getString(R.string.teaching_material))) {
                    if (section_view.getPullContent().equals("")) {
                        QZXTools.popToast(this, "请先选择学段", false);
                        return;
                    }
                    select_view.setPullContent(text);
                    press_layout.setVisibility(View.VISIBLE);
                    fetchNetworkForSubjectGrade(sectionMap.get(section_view.getPullContent()), "1",
                            subjectMap.get(subject_view.getPullContent()), pressMap.get(press_view.getPullContent()));
                    return;
                }
                break;
            case R.id.learning_pull_grade:
                grade_view.setPullContent(text);
                break;
            case R.id.learning_pull_press:
                press_view.setPullContent(text);
                break;
        }
        //查询数据---操作比较麻烦
        if (isItemBank) {
            if (subject_view.getPullContent().equals("") || section_view.getPullContent().equals("")
                    || press_view.getPullContent().equals("")) {
                return;
            }
            fetchNetworkForSubjectGrade(sectionMap.get(section_view.getPullContent()), "1",
                    subjectMap.get(subject_view.getPullContent()), pressMap.get(press_view.getPullContent()));
        } else {
            fetchNetworkForResourceContent(false, subjectMap.get(subject_view.getPullContent()),
                    gradeMap.get(grade_view.getPullContent()), sectionMap.get(section_view.getPullContent()),
                    pressMap.get(press_view.getPullContent()));
        }
    }

    /**
     * 重置界面
     */
    private void resetViewInterface(Boolean isItemBank) {
        //重置页码
        curPageNo = 1;
        //是题库还是其他
        this.isItemBank = isItemBank;
        //重置列表布局数据
        fillResourceList.clear();
        rvAutoLearningAdapter.notifyDataSetChanged();
        //重置下拉选项
        if (isItemBank) {
            subjectMap.clear();
            sectionMap.clear();
            gradeMap.clear();
            pressMap.clear();

            subject_view.setHintText("选择学科");
            section_view.setHintText("选择学段");
            select_view.setHintText("手动组卷");
            grade_view.setHintText("选择年级");
            press_view.setHintText("出版社");

            subject_view.setPullContent("");
            section_view.setPullContent("");
            select_view.setPullContent("");
            grade_view.setPullContent("");
            press_view.setPullContent("");

            select_layout.setVisibility(View.VISIBLE);
            press_layout.setVisibility(View.GONE);
        } else {
            subjectMap.clear();
            sectionMap.clear();
            gradeMap.clear();
            pressMap.clear();

            subject_view.setHintText("选择学科");
            section_view.setHintText("选择学期");
            grade_view.setHintText("选择年级");
            press_view.setHintText("出版社");

            subject_view.setPullContent("");
            section_view.setPullContent("");
            grade_view.setPullContent("");
            press_view.setPullContent("");

            select_layout.setVisibility(View.GONE);
            press_layout.setVisibility(View.VISIBLE);
        }

        //首先显示无资源，查找后显示资源
        leak_resource.setVisibility(View.VISIBLE);
    }

    private String pressName;
    private String subjectName;
    private String sectionName;
    private String TitleText;

    /**
     * 题库中的文本显示
     */
    private void displayItemBankText(String parentId, String xd, String chid) {
        //通过parentId得到文本出版社
        Iterator<Map.Entry<String, String>> iterator = pressMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            if (entry.getValue().equals(parentId)) {
                pressName = entry.getKey();
                break;
            }
        }
        //通过xd得到文本学段
        Iterator<Map.Entry<String, String>> iterator_xd = sectionMap.entrySet().iterator();
        while (iterator_xd.hasNext()) {
            Map.Entry<String, String> entry = iterator_xd.next();
            if (entry.getValue().equals(xd)) {
                sectionName = entry.getKey();
                break;
            }
        }
        //通过chid得到学科
        Iterator<Map.Entry<String, String>> iterator_xk = subjectMap.entrySet().iterator();
        while (iterator_xk.hasNext()) {
            Map.Entry<String, String> entry = iterator_xk.next();
            if (entry.getValue().equals(chid)) {
                subjectName = entry.getKey();
                break;
            }
        }

        //学段加学科作为标题
        if (!TextUtils.isEmpty(sectionName) && !TextUtils.isEmpty(subjectName)) {
            TitleText = sectionName.concat(subjectName);
        }
    }

    /**
     * 图标的旋转
     */
    private void iconRotate(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
    }
}
