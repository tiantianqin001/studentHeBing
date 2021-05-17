package com.telit.zhkt_three.Fragment.AutonomousLearning;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Progress;
import com.lzy.okgo.request.PostRequest;
import com.lzy.okserver.OkDownload;
import com.lzy.okserver.download.DownloadTask;
import com.telit.zhkt_three.Adapter.AutoLearning.AutoLearningAdapter;
import com.telit.zhkt_three.Adapter.AutoLearning.PullOperationAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.ClearEditText;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.BaseFragment;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.ResourceConditionBean;
import com.telit.zhkt_three.JavaBean.Gson.ResourceInfoBean;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.JavaBean.Resource.LocalResourceRecord;
import com.telit.zhkt_three.JavaBean.Resource.ResourceBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.LogDownloadListener;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.LocalResourceRecordDao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/3/29 16:00
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class ReadFragment extends BaseFragment implements View.OnClickListener, ToUsePullView.SpinnerClickInterface {
    //条件选择
    private LinearLayout layout_pull_all;
    private LinearLayout layout_pull;
    private RelativeLayout grade_layout;
    private RelativeLayout press_layout;
    private ToUsePullView grade_view;
    private ToUsePullView press_view;
    private XRecyclerView xRecyclerView;

    private ClearEditText et_search;

    //缺少网络和资源视图
    private ImageView leak_resource;
    private LinearLayout leak_net_layout;
    private TextView link_network;

    //-------------------------------------------侧拉下载文件管理,题库下不可见
    private FrameLayout auto_learning_pull_tag;
    private ImageView auto_learning_pull_icon;

    //方式一：checkbox to delete
    private LinearLayout pull_linear_red;
    private TextView pull_tv_edit;
    private CheckBox pull_cb_all;
    private RecyclerView pull_recycler;
    private TextView pull_tv_del;

    //todo 方式二：侧拉删除加全部删除

    //动画
    private Animation FromRightToLeftAnimation;
    private Animation FromLeftToRightAnimation;

    private List<LocalResourceRecord> pullOperationBeans;

    private PullOperationAdapter pullOperationAdapter;

    //-------------------------------------------侧拉下载文件管理,题库下不可见

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private AutoLearningAdapter rvAutoLearningAdapter;
    private List<DownloadTask> fillResourceList;

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

    /**
     * 创建新实例
     *
     * @return
     */
    public static ReadFragment newInstance() {
        ReadFragment fragment = new ReadFragment();
        return fragment;
    }

    /**
     * 最后一次进度消失
     */
    private int countRequest;
    private int countAdd;


    private int curPageNo = 1;
    private String type = "1010";

    private final int INTERVAL = 300; //输入时间间隔为300毫秒

    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(getActivity(), "当前网络不佳....", false);
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
                        QZXTools.popToast(getActivity(), "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }

                    break;
                case Operate_Subject_Grade_Success:
                    if (isShow){
                        countAdd++;

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


                        List<String> nianjiList = new ArrayList<String>(gradeMap.keySet());
                        grade_view.setDataList(nianjiList);

                        List<String> chubansheList = new ArrayList<String>(pressMap.keySet());
                        press_view.setDataList(chubansheList);

                        //设置默认值，并且自动加载资源内容
                        grade_view.setPullContent(nianjiList.get(0));
                        press_view.setPullContent(chubansheList.get(0));

                        curPageNo = 1;

                        fetchNetworkForResourceContent(false, gradeMap.get(grade_view.getPullContent()), pressMap.get(press_view.getPullContent()));

                    }

                    break;
                case Operate_Resource_Success:
                    if (isShow){
                        /*if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }*/

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


    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        initView(view);

        isShow=true;
        EventBus.getDefault().register(this);

        //网格布局，一行四列
        fillResourceList = new ArrayList<>();
        //显示时间线
        xRecyclerView.getDefaultRefreshHeaderView().setRefreshTimeVisible(true);
        //设置没有更多数据的显示
        xRecyclerView.getDefaultFootView().setNoMoreHint("");
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 4);
        xRecyclerView.setLayoutManager(gridLayoutManager);
        xRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        rvAutoLearningAdapter = new AutoLearningAdapter(getActivity(), fillResourceList,"2");
        xRecyclerView.setAdapter(rvAutoLearningAdapter);

        //加载更多回调
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                try {
                    curPageNo = 1;
                    fetchNetworkForResourceContent(false, gradeMap.get(grade_view.getPullContent()), pressMap.get(press_view.getPullContent()));
                }catch (Exception e){
                    e.fillInStackTrace();
                    QZXTools.popToast(MyApplication.getInstance(),"网络不可用",true);
                }

            }

            @Override
            public void onLoadMore() {
                try {
                    curPageNo++;
                    fetchNetworkForResourceContent(true, gradeMap.get(grade_view.getPullContent()),pressMap.get(press_view.getPullContent()));
                }catch (Exception e){
                    e.fillInStackTrace();
                    QZXTools.popToast(MyApplication.getInstance(),"网络不可用",true);
                }

            }
        });

        link_network.setOnClickListener(this);
        layout_pull_all.setOnClickListener(this);
        layout_pull.setOnClickListener(this);

        //使能第一个
        auto_learning_pull_tag.setVisibility(View.VISIBLE);

        //一开始统一显示无资源，用户条件选择查询后得到正式数据
        leak_resource.setVisibility(View.GONE);

        grade_view.setSpinnerClick(this);
        press_view.setSpinnerClick(this);

        gradeMap = new LinkedHashMap<>();
        pressMap = new LinkedHashMap<>();

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        fetchNetworkForResourceCondition();

        //侧拉点击
        auto_learning_pull_tag.setOnClickListener(this);

        //侧拉动画
        FromRightToLeftAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.right_to_left_show);
        FromLeftToRightAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.left_to_right_hide);

        LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();
        pullOperationBeans = localResourceRecordDao.queryBuilder().list();

        pull_tv_edit.setOnClickListener(this);
        pull_tv_del.setOnClickListener(this);

        pull_cb_all.setOnClickListener(this);

        pull_recycler.setLayoutManager(new LinearLayoutManager(getActivity()));

        pullOperationAdapter = new PullOperationAdapter(getActivity(), pullOperationBeans);

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

        et_search.addTextChangedListener(textWatcher);

        return view;
    }

    private void initView(View view){
        layout_pull_all = view.findViewById(R.id.learning_pull_all);
        layout_pull = view.findViewById(R.id.learning_pull_layout);
        grade_layout = view.findViewById(R.id.learning_pull_grade_layout);
        press_layout = view.findViewById(R.id.learning_pull_press_layout);
        grade_view = view.findViewById(R.id.learning_pull_grade);
        press_view = view.findViewById(R.id.learning_pull_press);
        xRecyclerView = view.findViewById(R.id.read_xRecycler);

        leak_resource = view.findViewById(R.id.leak_resource);
        leak_net_layout = view.findViewById(R.id.leak_net_layout);
        link_network = view.findViewById(R.id.link_network);

        auto_learning_pull_tag = view.findViewById(R.id.auto_learning_pull_tag);
        auto_learning_pull_icon = view.findViewById(R.id.auto_learning_pull_icon);

        press_view = view.findViewById(R.id.learning_pull_press);
        xRecyclerView = view.findViewById(R.id.read_xRecycler);

        pull_linear_red = view.findViewById(R.id.pull_linear_red);
        pull_tv_edit = view.findViewById(R.id.pull_tv_edit);
        pull_cb_all = view.findViewById(R.id.pull_cb_all);
        pull_recycler = view.findViewById(R.id.pull_recycler);
        press_view = view.findViewById(R.id.learning_pull_press);
        pull_tv_del = view.findViewById(R.id.pull_tv_del);

        et_search = view.findViewById(R.id.et_search);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isShow=false;

        EventBus.getDefault().unregister(this);

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }

        rvAutoLearningAdapter.unRegister();

        super.onDestroy();
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForResourceCondition() {
        //清空资源条件
        gradeMap.clear();
        pressMap.clear();

        grade_view.setHintText("学段");
        press_view.setHintText("分类");

        String url = UrlUtils.BaseUrl + UrlUtils.ConditionRead;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("typeCodes", "read_grade,read_press");

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
                    QZXTools.logE("resultJson=" + resultJson, null);

                    try {
                        Gson gson = new Gson();
                        ResourceConditionBean resourceConditionBean = gson.fromJson(resultJson, ResourceConditionBean.class);
                        ResourceConditionBean.ResultBean result = resourceConditionBean.getResult();

                        //获取年级
                        List<ResourceConditionBean.ResultBean.GradeBean> grade = result.getRead_grade();
                        for (ResourceConditionBean.ResultBean.GradeBean gradeBean : grade) {
                            gradeMap.put(gradeBean.getName(), gradeBean.getCode());
                        }

                        QZXTools.logE("onResponse: "+gradeMap,null);
                        //出版社
                        List<ResourceConditionBean.ResultBean.PressBean> press = result.getRead_press();
                        for (ResourceConditionBean.ResultBean.PressBean pressBean : press) {
                            pressMap.put(pressBean.getName(), pressBean.getCode()+"");
                        }

                        QZXTools.logE("sectionMap: "+new Gson().toJson(pressMap),null);


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

    /**
     * 查询具体的数据信息
     *
     * @param isLoadingMore 是否是加载更多，如果是的话不需要清空list集合
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetworkForResourceContent(boolean isLoadingMore, String gradeId,String pressId) {
        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        QZXTools.logE("isLoadingMore=" + isLoadingMore + ";gradeId=" + gradeId + ";pressId=" + pressId + ";curPageNo="
                + curPageNo + ";type=" + type+";url="+UrlUtils.BaseUrl + UrlUtils.OldResource, null);

        List<DownloadTask> downloadingTasks = rvAutoLearningAdapter.getDownloadingTasks();

        if (!isLoadingMore) {
            fillResourceList.clear();
            //如果这里不添加刷新的话可能有问题：Scrapped or attached views may not be recycled
            rvAutoLearningAdapter.notifyDataSetChanged();
        }

        String url = UrlUtils.BaseUrl + UrlUtils.OldResource;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("pageNo", curPageNo + "");
        paraMap.put("pageSize", "30");
        paraMap.put("suffix", type);

        if (!TextUtils.isEmpty(gradeId)) {
            paraMap.put("gradeid", gradeId);
        }

        if (!TextUtils.isEmpty(pressId)) {
            paraMap.put("press", pressId);
        }

        if (!TextUtils.isEmpty(et_search.getText().toString())) {
            paraMap.put("title", et_search.getText().toString());
        }

        QZXTools.logE("paraMap:"+new Gson().toJson(paraMap),null);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
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

                            fillResourceList.add(createDownloadTask(fillResource,downloadingTasks));
                        }

                        mHandler.sendEmptyMessage(Operate_Resource_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
                       /* if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }*/
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * 创建任务列表
     *
     * @param fillResource
     * @return
     */
    public DownloadTask createDownloadTask(FillResource fillResource,List<DownloadTask> downloadingTasks) {
        String url;
        String params = "?id=" + fillResource.getId() + "&userid=" + UserUtils.getUserId();
        if (fillResource.isTeachingMaterial()){
            url = UrlUtils.BaseUrl + UrlUtils.ElectronicBookDownload+params;
        }else {
            url = UrlUtils.CommonResourceDownload+params;
        }

        QZXTools.logE("url:"+url,null);

        DownloadTask task = getDownloadingTask(fillResource.getId(),downloadingTasks);
        if (task!=null){
            QZXTools.logE("任务存在",null);
            return task;
        }else {
            QZXTools.logE("任务不存在，重新创建",null);
            PostRequest<File> request = OkGo.<File>post(url);
            return OkDownload.request(url, request)
                    .extra1(fillResource)
                    .save()
                    .register(new LogDownloadListener());
        }
    }

    private DownloadTask getDownloadingTask(String id,List<DownloadTask> downloadingTasks){
        if (downloadingTasks!=null&&downloadingTasks.size()>0){
            for (DownloadTask task:downloadingTasks){
                FillResource fillResource = (FillResource) task.progress.extra1;
                if (fillResource.getId().equals(id)){
                    return task;
                }
            }
        }
        return null;
    }

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
            case R.id.link_network:
                QZXTools.enterWifiSetting(getActivity());
                break;
            case R.id.learning_pull_all:
                //如果下拉显示中则不取消一级背景色
                if (grade_view.pullViewPopShown()
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
                            iconRotate(auto_learning_pull_icon, 0.0f, 180.0f);
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

                    //下载置为可下载状态
                    resetDownloadStatus(delResources);
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
                } else {
                    pull_cb_all.setChecked(false);

                    //全选
                    pull_tv_del.setSelected(false);

                    for (LocalResourceRecord localResourceRecord : pullOperationBeans) {
                        localResourceRecord.setIsChoosed(false);
                    }
                }
                pullOperationAdapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 重置下载状态
     *
     * @param delResources
     */
    private void resetDownloadStatus(List<LocalResourceRecord> delResources){
        for (LocalResourceRecord localResourceRecord:delResources){
            for (DownloadTask task:fillResourceList){
                Progress progress = task.progress;
                FillResource fillResource = (FillResource) progress.extra1;
                if (localResourceRecord.getResourceId().equals(fillResource.getId())){
                    progress.status = Progress.NONE;
                    break;
                }
            }
        }

        rvAutoLearningAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void spinnerClick(View parent, String text) {
        switch (parent.getId()) {
            case R.id.learning_pull_grade:
                grade_view.setPullContent(text);
                break;
            case R.id.learning_pull_press:
                press_view.setPullContent(text);
                break;
        }
        //查询数据---操作比较麻烦
        fetchNetworkForResourceContent(false, gradeMap.get(grade_view.getPullContent()), pressMap.get(press_view.getPullContent()));
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

    TextWatcher textWatcher = new TextWatcher() {
        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                curPageNo = 1;
                fetchNetworkForResourceContent(false, gradeMap.get(grade_view.getPullContent()), pressMap.get(press_view.getPullContent()));
            }catch (Exception e){
                e.fillInStackTrace();
                QZXTools.popToast(MyApplication.getInstance(),"网络不可用",true);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void afterTextChanged(Editable s) {
        }
    };
}
