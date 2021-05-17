package com.telit.zhkt_three.Activity.MicroClass;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.MicroClass.RVMicroClassAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.MicroClassBean;
import com.telit.zhkt_three.JavaBean.MicroClass.ActualMicBean;
import com.telit.zhkt_three.JavaBean.MicroClass.OrderByDateMicBean;
import com.telit.zhkt_three.JavaBean.PreView.Disk;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 微课中心，视频
 * <p>
 * 微课资源名称、评分、上传日期、缩略图、预览地址
 * <p>
 * mData 数据时按照时间分好的一组组的
 */
public class MicroClassCenterActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "qin998";
    private Unbinder unbinder;
    @BindView(R.id.micro_class_head_layout)
    CustomHeadLayout customHeadLayout;
    @BindView(R.id.micro_class_refresh)
    SwipeRefreshLayout micro_class_refresh;
    @BindView(R.id.micro_class_recycler)
    RecyclerView micro_class_recycler;

    //-----------无网络或者无资源
    @BindView(R.id.request_retry_layout)
    LinearLayout request_retry_layout;
    @BindView(R.id.request_retry)
    TextView request_retry;
    @BindView(R.id.leak_resource_layout)
    LinearLayout leak_resource_layout;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private RVMicroClassAdapter rvMicroClassAdapter;
    //传递给Adapter的类
    private List<OrderByDateMicBean> mData;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;

    private static boolean isShow = false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow) {
                        //  QZXTools.popToast(MicroClassCenterActivity.this, "服务端错误！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        if (leak_resource_layout != null || request_retry_layout != null) {
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }

                        if (micro_class_refresh != null && micro_class_refresh.isRefreshing())
                            micro_class_refresh.setRefreshing(false);
                    }


                    break;
                case Error404:
                    if (isShow) {
                        // QZXTools.popToast(MicroClassCenterActivity.this, "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        if (leak_resource_layout != null || request_retry_layout != null) {
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }

                        if (micro_class_refresh != null && micro_class_refresh.isRefreshing())
                            micro_class_refresh.setRefreshing(false);
                    }


                    break;
                case Operator_Success:
                    if (isShow) {
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (micro_class_refresh.isRefreshing())
                            micro_class_refresh.setRefreshing(false);

//                    QZXTools.logE("mData Size=" + mData.size(), null);
//                    for (OrderByDateMicBean orderByDateMicBean : mData) {
//                        QZXTools.logE("mData date=" + orderByDateMicBean.getSameDate(), null);
//                    }

                        rvMicroClassAdapter.notifyDataSetChanged();
                    }


                    break;
            }
        }
    };


    private int totalDataCount;
    private int totalY;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro_class_center);
        isShow = true;
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        //设置头像信息等


        customHeadLayout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());


        request_retry.setOnClickListener(this);
        link_network.setOnClickListener(this);

        mData = new ArrayList<>();
        rvMicroClassAdapter = new RVMicroClassAdapter(this, mData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        micro_class_recycler.setLayoutManager(linearLayoutManager);
        micro_class_recycler.setAdapter(rvMicroClassAdapter);

        //刷新
        micro_class_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onRefresh() {
                QZXTools.logE( "onScrollStateChanged: "+1,null);

                requestMicroDatas(true, true);
            }
        });

        //RV加载更多
        micro_class_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @SuppressLint("LongLogTag")
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        totalY>0 ) {
                    QZXTools.logE("要加载更多... curPageNo=" + curPageNo, null);
                    requestMicroDatas(false, false);
                    QZXTools.logE( "onScrollStateChanged: "+2+"...." +
                            "......PageSize="+PageSize+".....+mData=="+mData.size()+".......+totaly"+totalY,null);
                } else {
                    rvMicroClassAdapter.setFootVisible(false);

                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //这个dy 是滑动的方向
                totalY=dy;




            }
        });
        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        QZXTools.logE("onScrollStateChanged: "+"第一次",null);
        requestMicroDatas(true, false);

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        isShow = false;
        super.onDestroy();

        BuriedPointUtils.buriedPoint("2024", "", "", "", "");
    }

    /**
     * 当前分页的页数，初始化是1
     */
    private int curPageNo = 1;
    private static final int PageSize = 30;
    private SharedPreferences sp_student;

    private void requestMicroDatas(boolean isInit, boolean isRefresh) {

        QZXTools.logE("isInit=" + isInit + ";isRefresh=" + isRefresh, null);

        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (isInit) {
            if (mData != null && mData.size() > 0) {
                //刷新重置日期处理类
                curDateString = null;
                orderByDateMicBean = null;
                actualMicBeans = null;

                curPageNo = 1;
                mData.clear();
                QZXTools.logE("requestMicroDatas: "+mData.size(),null);
                rvMicroClassAdapter.notifyDataSetChanged();
            }
        } else {
            curPageNo++;
        }

        if (isRefresh) {
            micro_class_refresh.setRefreshing(true);
        } else {

        }

        String url = UrlUtils.BaseUrl + UrlUtils.stuQueryDir;
        Map<String, String> paraMap = new HashMap<>();
       /* paraMap.put("parentId", "0");
        //传递1表示微课资源，固定的
        paraMap.put("fileType", "1");
        paraMap.put("pageNo", curPageNo + "");
        //一次加载十个数据
        paraMap.put("pageSize", PageSize + "");*/

        paraMap.put("classId", UserUtils.getClassId());
        paraMap.put("studentId", UserUtils.getUserId());
        paraMap.put("dateSize", "-1");
        paraMap.put("format", "mp4");
        paraMap.put("fileType", "1");
        paraMap.put("pageSize", PageSize + "");
        paraMap.put("pageNo", curPageNo + "");
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(Server_Error);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String result = response.body().string();
//                    QZXTools.logE("micro class result=" + result, null);
                        Gson gson = new Gson();
                        MicroClassBean microClassBean = gson.fromJson(result, MicroClassBean.class);
                        List<Disk> disks = microClassBean.getResult();
                        List<ActualMicBean> actualMicBeans = new ArrayList<>();

                        totalDataCount = microClassBean.getTotal();

                        if (!isInit) {
                            rvMicroClassAdapter.setFootVisible(true);
                            if (disks.size() > 0) {
                                rvMicroClassAdapter.setAllEnd(false);
                            } else {
                                rvMicroClassAdapter.setAllEnd(true);
                            }
                        } else {
                            rvMicroClassAdapter.setAllEnd(false);
                            rvMicroClassAdapter.setFootVisible(false);
                        }
                        for (int i = 0; i < disks.size(); i++) {
                            ActualMicBean actualMicBean = new ActualMicBean();
                          //  actualMicBean.setCreateDate(disks.get(i).getUpdateDate());
                            actualMicBean.setPreviewUrl(disks.get(i).getPreviewUrl());
                            actualMicBean.setSize(disks.get(i).getFileSize());
                            actualMicBean.setScore(disks.get(i).getAvgStar());
                            actualMicBean.setCreateDate(disks.get(i).getCreateDate());
                            actualMicBean.setThumbnail(disks.get(i).getThumbnail());
                            actualMicBean.setFileName(disks.get(i).getName());

                           // actualMicBean.setThumbnail(disks.get(i).getThumbnail());
                            actualMicBeans.add(actualMicBean);
                        }
                       handlerDateInfo(actualMicBeans);

                        mHandler.sendEmptyMessage(Operator_Success);
                    } catch (Exception e) {
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

    private String curDateString;

    private OrderByDateMicBean orderByDateMicBean;
    private List<ActualMicBean> actualMicBeans;

    /**
     * 整合处理日期数据
     * <p>
     * 注意：一次加载十个,默认,我这里设置一次性加载20个
     */
    private void handlerDateInfo(List<ActualMicBean> originalBean) {

//        QZXTools.logE("originalBean size=" + originalBean.size(), null);

        //计数
        int count = 0;

        boolean isStartEnter = true;
        for (ActualMicBean actualMicBean : originalBean) {


            //因为给的日期是例如： 2019-12-25 20:25，所以分割取年月日即可
           String[] emptyStrs = actualMicBean.getCreateDate().split(" ");
            if (TextUtils.isEmpty(curDateString)) {
                isStartEnter = false;
                curDateString = emptyStrs[0];
                orderByDateMicBean = new OrderByDateMicBean();
                orderByDateMicBean.setSameDate(curDateString);
                actualMicBeans = new ArrayList<>();
                actualMicBeans.add(actualMicBean);
                count++;
                //刚好只有条
                if (originalBean.size() == count) {
                    orderByDateMicBean.setActualMicBeans(actualMicBeans);
                    mData.add(orderByDateMicBean);
                }
            } else {
                if (emptyStrs[0].equals(curDateString)) {
                    if (isStartEnter) {
                        //每次首次进入
                        isStartEnter = false;
                        //因为最后afterHomeworkBeans还没有清空
                        mData.remove(mData.size() - 1);
                    }
                    //同一天
                    actualMicBeans.add(actualMicBean);
                    count++;
                    //结尾数据都是同一天的哦
                    if (count == PageSize) {
                        orderByDateMicBean.setActualMicBeans(actualMicBeans);
                        mData.add(orderByDateMicBean);
                    } else if (originalBean.size() <= PageSize && count == originalBean.size()) {
                        orderByDateMicBean.setActualMicBeans(actualMicBeans);
                        mData.add(orderByDateMicBean);
                    }
                } else {
                    //刚好第一次进入日期就不同，如果不是第一次进入这个模块的话，把上一次的结果添加给集合
                    if (!isStartEnter) {
                        orderByDateMicBean.setActualMicBeans(actualMicBeans);
                        mData.add(orderByDateMicBean);
                    }

                    isStartEnter = false;
                    curDateString = emptyStrs[0];
                    //重置
                    actualMicBeans = null;
                    orderByDateMicBean = null;
                    orderByDateMicBean = new OrderByDateMicBean();
                    orderByDateMicBean.setSameDate(curDateString);
                    actualMicBeans = new ArrayList<>();
                    actualMicBeans.add(actualMicBean);
                    count++;
                    if (count == PageSize) {
                        orderByDateMicBean.setActualMicBeans(actualMicBeans);
                        mData.add(orderByDateMicBean);
                    } else if (originalBean.size() <= PageSize && count == originalBean.size()) {
                        //刚好count等于size
                        orderByDateMicBean.setActualMicBeans(actualMicBeans);
                        mData.add(orderByDateMicBean);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_retry:
                requestMicroDatas(true, false);
                break;
            case R.id.link_network:
                QZXTools.enterWifiSetting(this);
                break;
        }
    }
}
