package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.telit.zhkt_three.Adapter.AfterHomeWork.TypicalAnswersAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.TypicalAnswers;
import com.telit.zhkt_three.JavaBean.Gson.TypicalAnswersBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
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
 * *****************************************************************
 * author: Administrator
 * time: 2021/3/30 7:52
 * name;
 * overview:
 * usage: 典型答题
 * ******************************************************************
 */
public class TypicalAnswersActivity extends BaseActivity implements View.OnClickListener {
    private Unbinder unbinder;

    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.tv_perfectAnswers)
    TextView tv_perfectAnswers;
    @BindView(R.id.tv_typicalMistake)
    TextView tv_typicalMistake;

    @BindView(R.id.typical_answers_recycler)
    XRecyclerView xRecyclerView;

    //-----------无网络或者无资源
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    private String homeworkId;
    private String questionId;

    /**
     * 当前分页的页数，初始化是1
     */
    private int curPageNo = 1;

    private TypicalAnswersAdapter typicalAnswersAdapter;

    private List<TypicalAnswers> perfectAnswers;
    private List<TypicalAnswers> typicalMistakes;

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;

    private static boolean isShow=false;
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(TypicalAnswersActivity.this, "当前网络不佳....", false);
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
                        QZXTools.popToast(TypicalAnswersActivity.this, "没有相关资源！", false);
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

                        if (perfectAnswers.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }

                        tv_perfectAnswers.setText("优秀作答（"+perfectAnswers.size()+"人）");
                        tv_typicalMistake.setText("典型错误（"+typicalMistakes.size()+"人）");

                        typicalAnswersAdapter.setDatas(perfectAnswers,"1");
                    }

                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_typical_answers);

        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();

        questionId = getIntent().getStringExtra("questionId");
        homeworkId = getIntent().getStringExtra("homeworkId");

        unbinder = ButterKnife.bind(this);
        isShow=true;

        EventBus.getDefault().register(this);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tv_typicalMistake.setSelected(false);
        tv_perfectAnswers.setSelected(true);

        tv_typicalMistake.setOnClickListener(this);
        tv_perfectAnswers.setOnClickListener(this);

        //连接网络
        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(TypicalAnswersActivity.this);
            }
        });

        perfectAnswers = new ArrayList<>();
        typicalMistakes = new ArrayList<>();
        typicalAnswersAdapter = new TypicalAnswersAdapter(this, perfectAnswers);

        xRecyclerView.setLayoutManager(new GridLayoutManager(this,3));

        xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, getResources().getDimensionPixelSize(R.dimen.x5), 0, getResources().getDimensionPixelSize(R.dimen.x5));
            }
        });

        xRecyclerView.setAdapter(typicalAnswersAdapter);

        xRecyclerView.setLoadingMoreEnabled(false);
        xRecyclerView.setPullRefreshEnabled(false);


        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRefresh() {
                perfectAnswers.clear();
                typicalMistakes.clear();
                typicalAnswersAdapter.notifyDataSetChanged();
                curPageNo = 1;
                requestNetDatas(homeworkId,questionId);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLoadMore() {
                curPageNo++;
                requestNetDatas(homeworkId,questionId);
            }
        });

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        requestNetDatas(homeworkId,questionId);
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
    private void requestNetDatas(String homeworkId,String questionId) {
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

        String url = UrlUtils.BaseUrl + UrlUtils.TypicalAnswers;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("pageNo", curPageNo + "");
        mapParams.put("pageSize", PageSize + "");
        mapParams.put("homeworkId", homeworkId);
        mapParams.put("questionId", questionId);

        QZXTools.logE("Params:"+new Gson().toJson(mapParams), null);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (call.isCanceled()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QZXTools.popToast(TypicalAnswersActivity.this, "取消了请求", false);
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

                        if (!TextUtils.isEmpty(resultJson)){
                            TypicalAnswersBean typicalAnswersBean = new Gson().fromJson(resultJson,TypicalAnswersBean.class);
                            if (typicalAnswersBean!=null&&typicalAnswersBean.getResult()!=null){
                                if (typicalAnswersBean.getResult().getFineList()!=null&&typicalAnswersBean.getResult().getFineList().size()>0){
                                    perfectAnswers.addAll(typicalAnswersBean.getResult().getFineList());
                                }

                                if (typicalAnswersBean.getResult().getWrongList()!=null&&typicalAnswersBean.getResult().getWrongList().size()>0){
                                    typicalMistakes.addAll(typicalAnswersBean.getResult().getWrongList());
                                }
                            }
                        }

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_perfectAnswers:
                tv_perfectAnswers.setSelected(true);
                tv_typicalMistake.setSelected(false);
                typicalAnswersAdapter.setDatas(perfectAnswers,"1");
                break;
            case R.id.tv_typicalMistake:
                tv_perfectAnswers.setSelected(false);
                tv_typicalMistake.setSelected(true);
                typicalAnswersAdapter.setDatas(typicalMistakes,"2");
                break;
        }
    }
}
