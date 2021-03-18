package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.AfterHomeWork.CollectQuestionAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.Gson.CollectQuestionByHandBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/2/1 8:41
 * name;
 * overview:
 * usage: 收藏题目
 * ******************************************************************
 */
public class CollectQuestionActivity extends BaseActivity {
    private Unbinder unbinder;

    @BindView(R.id.collect_question_recycler)
    XRecyclerView xRecyclerView;

    //-----------无网络或者无资源
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    /**
     * 当前分页的页数，初始化是1
     */
    private int curPageNo = 1;

    private CollectQuestionAdapter collectQuestionAdapter;

    private List<QuestionBank> mData;

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;

    private static boolean isShow=false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(CollectQuestionActivity.this, "服务端错误！", false);
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
                        QZXTools.popToast(CollectQuestionActivity.this, "没有相关资源！", false);
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
                case Operator_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }

                        if (mData.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }

                        collectQuestionAdapter.notifyDataSetChanged();
                    }

                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_question);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();

        unbinder = ButterKnife.bind(this);
        isShow=true;

        EventBus.getDefault().register(this);

        //连接网络
        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(CollectQuestionActivity.this);
            }
        });

        mData = new ArrayList<>();
        collectQuestionAdapter = new CollectQuestionAdapter(this, mData);

        xRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, getResources().getDimensionPixelSize(R.dimen.x5), 0, getResources().getDimensionPixelSize(R.dimen.x5));
            }
        });

        xRecyclerView.setAdapter(collectQuestionAdapter);


        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRefresh() {
                mData.clear();
                collectQuestionAdapter.notifyDataSetChanged();
                curPageNo = 1;
                requestNetDatas();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLoadMore() {
                curPageNo++;
                requestNetDatas();
            }
        });

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        requestNetDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }

        EventBus.getDefault().unregister(this);
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        isShow=false;
    }

    private static final int PageSize = 10;

    /**
     * 请求网络数据
     */
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestNetDatas() {
        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);

            if (circleProgressDialogFragment != null) {
                circleProgressDialogFragment.dismissAllowingStateLoss();
                circleProgressDialogFragment = null;
            }

            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        String url = UrlUtils.BaseUrl + UrlUtils.CollectQuestionList;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("studentId", UserUtils.getUserId());
        mapParams.put("pageNo", curPageNo + "");
        mapParams.put("pageSize", PageSize + "");

        QZXTools.logE("Params:"+new Gson().toJson(mapParams), null);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (call.isCanceled()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QZXTools.popToast(CollectQuestionActivity.this, "取消了请求", false);
                        }
                    });
                }

                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
                CrashReport.postCatchedException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("todo homework resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        CollectQuestionByHandBean collectQuestionByHandBean = gson.fromJson(resultJson, CollectQuestionByHandBean.class);

                        handlerDateInfo(collectQuestionByHandBean.getResult());

                        mHandler.sendEmptyMessage(Operator_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();

                        QZXTools.logE("Exception", e);

                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
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
     * 整合处理日期数据
     * <p>
     * 注意：一次加载十个
     */
    private void handlerDateInfo(List<QuestionBank> collectQuestionBeans) {
        mData.addAll(collectQuestionBeans);
    }

    @OnClick(R.id.collect_question_back)
    void collectQuestionBack(){
        finish();
    }

    //收藏或取消成功
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscriber(tag = Constant.Question_Collect_Success, mode = ThreadMode.MAIN)
    public void collectQuestion(String teacher_end) {
        QZXTools.logE("收藏或取消成功", null);
        mData.clear();
        collectQuestionAdapter.notifyDataSetChanged();
        curPageNo = 1;
        requestNetDatas();
    }
}
