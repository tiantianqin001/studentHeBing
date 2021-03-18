package com.telit.zhkt_three.Activity.ClassRecord;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.interactive.PracticeVPAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CusomPater;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.Gson.PracticeBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RecordPracticeDoneActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.record_practice_back)
    ImageView record_practice_back;
    @BindView(R.id.practice_viewpager)
    CusomPater practice_viewpager;
    @BindView(R.id.practice_left)
    LinearLayout practice_left;
    @BindView(R.id.practice_right)
    LinearLayout practice_right;

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

    /**
     * 通过Intent获取的练习ID
     */
    private String practiceId;

    /**
     * 练习状态：0 要做 1 提交了 2 批阅了
     */
    private String status = "1";

    public void setStatus(String status) {
        this.status = status;
    }

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private int curPageIndex = 0;
    private int totalPageCount;
    private static boolean isShow=false;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operate_Success = 2;
    private static final int No_Resource = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(RecordPracticeDoneActivity.this, "服务端错误！", false);
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
                        QZXTools.popToast(RecordPracticeDoneActivity.this, "没有相关资源！", false);
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
                case Operate_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        List<QuestionInfo> questionInfoList = (List<QuestionInfo>) msg.obj;

                        totalPageCount = questionInfoList.size();
                        if (totalPageCount > 1) {
                            practice_right.setVisibility(View.VISIBLE);
                            practice_left.setVisibility(View.INVISIBLE);
                        } else {
                            practice_left.setVisibility(View.INVISIBLE);
                            practice_right.setVisibility(View.INVISIBLE);
                        }

                        PracticeVPAdapter practiceVPAdapter = new PracticeVPAdapter
                                (RecordPracticeDoneActivity.this, questionInfoList);
                        practiceVPAdapter.setStatus(status);
                        practice_viewpager.setAdapter(practiceVPAdapter);
                    }

                    break;
                case No_Resource:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        request_retry_layout.setVisibility(View.GONE);
                        leak_resource_layout.setVisibility(View.VISIBLE);
                    }

                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_practice_done);
        unbinder = ButterKnife.bind(this);
        isShow=true;

        practiceId = getIntent().getStringExtra("practice_id");

        practice_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                curPageIndex = i;
                if (i >= (totalPageCount - 1)) {
                    practice_right.setVisibility(View.INVISIBLE);
                    practice_left.setVisibility(View.VISIBLE);
                } else if (i <= 0) {
                    practice_left.setVisibility(View.INVISIBLE);
                    practice_right.setVisibility(View.VISIBLE);
                } else {
                    practice_left.setVisibility(View.VISIBLE);
                    practice_right.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        record_practice_back.setOnClickListener(this);
        practice_left.setOnClickListener(this);
        practice_right.setOnClickListener(this);

        if (TextUtils.isEmpty(practiceId)) {
            //没有资源
            mHandler.sendEmptyMessage(No_Resource);
        } else {
            requestPracticeInfo();
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        //防止内存泄露 ---放置于onDestroy()中
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        isShow=false;
        super.onDestroy();
    }

    /**
     * 请求随堂练习信息
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestPracticeInfo() {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        //请求投票信息
        String url = UrlUtils.BaseUrl + UrlUtils.ClassPractice;
        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("classexamid", practiceId);
        paraMap.put("studentid", UserUtils.getStudentId());
        //todo的状态
        paraMap.put("status", status);
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
                    PracticeBean practiceBean = gson.fromJson(resultJson, PracticeBean.class);
                    if (practiceBean.getResult() != null && practiceBean.getResult().size() > 0) {
                        Message message = mHandler.obtainMessage();
                        message.what = Operate_Success;
                        message.obj = practiceBean.getResult();
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(Error404);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_practice_back:
                finish();
                break;
            case R.id.practice_left:
                curPageIndex--;
                if (curPageIndex >= 0) {
                    if (curPageIndex == 0) {
                        practice_left.setVisibility(View.INVISIBLE);
                    } else {
                        practice_left.setVisibility(View.VISIBLE);
                    }
                    practice_right.setVisibility(View.VISIBLE);
                    practice_viewpager.setCurrentItem(curPageIndex, true);
                }
                break;
            case R.id.practice_right:
                curPageIndex++;
                if (curPageIndex <= totalPageCount - 1) {
                    if (curPageIndex == totalPageCount - 1) {
                        practice_right.setVisibility(View.INVISIBLE);
                    } else {
                        practice_right.setVisibility(View.VISIBLE);
                    }
                    practice_left.setVisibility(View.VISIBLE);
                    practice_viewpager.setCurrentItem(curPageIndex, true);
                }
                break;
        }
    }
}
