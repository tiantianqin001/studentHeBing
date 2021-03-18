package com.telit.zhkt_three.Activity.ClassRecord;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.VoteResult.VoteCountView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.Gson.VoteResultGsonBean;
import com.telit.zhkt_three.JavaBean.InterActive.VoteResultTwoBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;

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

/**
 * 展示投票的结果详情
 */
public class RecordVoteResultActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.record_vote_back)
    ImageView record_vote_back;
    @BindView(R.id.vote_result_topic)
    TextView vote_result_topic;
    @BindView(R.id.vote_main_result_layout)
    LinearLayout vote_main_result_layout;

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

    /**
     * 通过Intent获取的投票ID
     */
    private String voteId;

    /**
     * 类型，需要传递给服务端
     */
    private String type;

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
                    QZXTools.popToast(RecordVoteResultActivity.this, "服务端错误！", false);
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isAdded()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    if(leak_resource_layout!=null || request_retry_layout!=null){
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.VISIBLE);
                    }

                    break;
                case Error404:
                    QZXTools.popToast(RecordVoteResultActivity.this, "没有相关资源！", false);
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isAdded()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    if(leak_resource_layout!=null || request_retry_layout!=null){
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.VISIBLE);
                    }


                    break;
                case Operate_Success:
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isAdded()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    request_retry_layout.setVisibility(View.GONE);
                    leak_resource_layout.setVisibility(View.GONE);

                    List<VoteResultTwoBean> voteResultTwoBeans = (List<VoteResultTwoBean>) msg.obj;

                    int count = 0;
                    for (VoteResultTwoBean voteResultTwoBean : voteResultTwoBeans) {
                        count += Integer.valueOf(voteResultTwoBean.getNumberOfVote());
                    }
//                    int totalNum = msg.arg1;
                    int totalNum = count;

                    addVoteItem(voteResultTwoBeans, totalNum);

                    break;
                case No_Resource:
                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isAdded()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    request_retry_layout.setVisibility(View.GONE);
                    leak_resource_layout.setVisibility(View.VISIBLE);
                    break;
            }
        }
    };

    /**
     * 添加投票子项
     */
    private void addVoteItem(List<VoteResultTwoBean> voteResultTwoBeans, int totalCount) {
        for (int i = 0; i < voteResultTwoBeans.size(); i++) {
            if (i == 0) {
                vote_result_topic.setText(voteResultTwoBeans.get(i).getText());
            }
            VoteCountView voteCountView = new VoteCountView(this);
            voteCountView.setVoteResultDisplay(Integer.parseInt(voteResultTwoBeans.get(i).getNumberOfVote()),
                    totalCount, voteResultTwoBeans.get(i).getText(), voteResultTwoBeans.get(i).getImageUrl());
            vote_main_result_layout.addView(voteCountView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_vote_result);
        unbinder = ButterKnife.bind(this);

        vote_result_topic.setText("");

        voteId = getIntent().getStringExtra("recordId");
        type = getIntent().getStringExtra("type");

        if (TextUtils.isEmpty(voteId)) {
            QZXTools.popToast(this, "投票ID无效", false);
            return;
        }

        record_vote_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(RecordVoteResultActivity.this);
            }
        });
        request_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestVoteResult();
            }
        });

        if (TextUtils.isEmpty(voteId)) {
            //没有资源
            mHandler.sendEmptyMessage(No_Resource);
        } else {
            requestVoteResult();
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
        super.onDestroy();
    }

    private int curRetryCount = 0;

    /**
     * {"title":"9999999","index":"0","image":null,"numberOfVote":"0","percentage":"0",
     * "optionContent":null,"imageUrl":"/filesystem/vote/20191218/1576661738448.jpeg","text":"23434"}
     * 查询投票结果
     */
    private void requestVoteResult() {
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());


        curRetryCount++;

        String url = UrlUtils.BaseUrl + UrlUtils.QueryRecordInfo;
        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("recordId", voteId);
        paraMap.put("type", type);

        QZXTools.logE("voteId=" + voteId + ";type=" + type, null);

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
                    VoteResultGsonBean voteResultGsonBean = gson.fromJson(resultJson, VoteResultGsonBean.class);
                    if (voteResultGsonBean.getResult() != null && voteResultGsonBean.getResult().size() > 0) {
                        List<VoteResultTwoBean> voteResultTwoBeans = voteResultGsonBean.getResult();


                        Message message = mHandler.obtainMessage();
                        message.what = Operate_Success;
                        message.obj = voteResultTwoBeans;
                        message.arg1 = voteResultGsonBean.getTotal();
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(No_Resource);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
