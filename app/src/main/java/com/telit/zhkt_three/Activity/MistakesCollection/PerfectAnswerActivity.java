package com.telit.zhkt_three.Activity.MistakesCollection;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Adapter.Mistake.PerfectAnswerLeftAdapter;
import com.telit.zhkt_three.Adapter.Mistake.PerfectAnswerRightAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.MistakesBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.MistakesCollection.PerfectLeftBean;
import com.telit.zhkt_three.JavaBean.MistakesCollection.PerfectRightBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

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
 * 优秀答案界面
 */
public class PerfectAnswerActivity extends AppCompatActivity implements View.OnClickListener {

    private Unbinder unbinder;

    private String questionId;
    private String homeworkId;

    @BindView(R.id.perfect_answer_back)
    ImageView perfect_answer_back;
    @BindView(R.id.perfect_answer_recycler_left)
    RecyclerView perfect_answer_recycler_left;
    @BindView(R.id.perfect_answer_recycler_right)
    RecyclerView perfect_answer_recycler_right;

    private PerfectAnswerLeftAdapter perfectAnswerLeftAdapter;
    private PerfectAnswerRightAdapter perfectAnswerRightAdapter;

    private List<PerfectLeftBean> perfectLeftBeans;
    private List<PerfectRightBean> perfectRightBeans;

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
    private static boolean isShow=false;
    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(PerfectAnswerActivity.this, "当前网络不佳....", false);

                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if(leak_resource_layout!=null || request_retry_layout!=null){
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(PerfectAnswerActivity.this, "没有相关资源！", false);

                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if(leak_resource_layout!=null || request_retry_layout!=null){
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                case Operator_Success:
                    if (isShow){
                        //之所以去掉isVisible的判断是因为：会存在不为空但是界面未显示的情况
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (perfectLeftBeans.size() > 0) {
                            if (request_retry_layout!=null || leak_resource_layout!=null){
                                request_retry_layout.setVisibility(View.GONE);
                                leak_resource_layout.setVisibility(View.GONE);
                            }

                        } else {

                            if (request_retry_layout!=null || leak_resource_layout!=null){
                                request_retry_layout.setVisibility(View.GONE);
                                leak_resource_layout.setVisibility(View.GONE);
                            }
                        }

                        //通知刷新视图
                        perfectAnswerLeftAdapter.notifyDataSetChanged();
                        perfectAnswerRightAdapter.notifyDataSetChanged();
                    }

                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfect_answer);
        unbinder = ButterKnife.bind(this);
        isShow=true;
        //需要questionId和homeworkId
        Intent intent = getIntent();
        questionId = intent.getStringExtra("questionId");
        homeworkId = intent.getStringExtra("homeworkId");
        if (TextUtils.isEmpty(questionId) || TextUtils.isEmpty(homeworkId)) {
            QZXTools.popToast(this, "缺少必要的参数信息", false);
            finish();
        }

        initView();

        requestBestAnswer();

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //如果还在转圈就关闭界面的话，直接消失置空
        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        isShow=false;
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 初始化界面Adapter等
     */
    private void initView() {
        perfect_answer_back.setOnClickListener(this);
        request_retry.setOnClickListener(this);
        link_network.setOnClickListener(this);

        //取消上下底的遮罩层
        perfect_answer_recycler_left.setOverScrollMode(View.OVER_SCROLL_NEVER);
        perfect_answer_recycler_right.setOverScrollMode(View.OVER_SCROLL_NEVER);

        perfectLeftBeans = new ArrayList<>();
        perfectAnswerLeftAdapter = new PerfectAnswerLeftAdapter(this, perfectLeftBeans);
        perfect_answer_recycler_left.setLayoutManager(new LinearLayoutManager(this));
        perfect_answer_recycler_left.setAdapter(perfectAnswerLeftAdapter);

        perfectRightBeans = new ArrayList<>();
        perfectAnswerRightAdapter = new PerfectAnswerRightAdapter(this, perfectRightBeans);
        perfect_answer_recycler_right.setLayoutManager(new LinearLayoutManager(this));
        perfect_answer_recycler_right.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 10, 0, 0);
            }
        });
        perfect_answer_recycler_right.setAdapter(perfectAnswerRightAdapter);
    }

    /**
     * 请求网络优秀答案
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestBestAnswer() {
        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }

        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        perfectRightBeans.clear();
        perfectLeftBeans.clear();

        // "http://172.16.5.160:8090"

        String url = UrlUtils.BaseUrl + UrlUtils.PerfectAnswerLists;
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("questionId", questionId);
        paraMap.put("homeworkId", homeworkId);
        paraMap.put("studentId", UserUtils.getUserId());
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
                    Gson gson = new Gson();
                    MistakesBean mistakesBean = gson.fromJson(resultJson, MistakesBean.class);
                    List<QuestionInfo> questionInfoList = mistakesBean.getResult();

                    for (int i = 0; i < questionInfoList.size(); i++) {
                        PerfectLeftBean perfectLeftBean = new PerfectLeftBean();
                        perfectLeftBean.setRank(i + 1);
                        perfectLeftBean.setName(questionInfoList.get(i).getStudentName());
                        perfectLeftBean.setScore(questionInfoList.get(i).getOwnscore());
                        perfectLeftBean.setPhotoUrl(questionInfoList.get(i).getStudentPhoto());
                        perfectLeftBeans.add(perfectLeftBean);

                        PerfectRightBean perfectRightBean = new PerfectRightBean();
                        perfectRightBean.setTxtAnswer(questionInfoList.get(i).getOwnList().get(0).getAnswerContent());
                        perfectRightBean.setImgLists(questionInfoList.get(i).getImgFile());
                        perfectRightBean.setPhotoUrl(questionInfoList.get(i).getStudentPhoto());
                        perfectRightBeans.add(perfectRightBean);
                    }

                    mHandler.sendEmptyMessage(Operator_Success);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.perfect_answer_back:
                EventBus.getDefault().post(new ExtraInfoBean(), Constant.Subjective_Board_Callback);
                finish();


                break;
            case R.id.request_retry:
                //重新请求所有的请求
                requestBestAnswer();
                break;
            case R.id.link_network:
                QZXTools.enterWifiSetting(PerfectAnswerActivity.this);
                break;
        }
    }
}
