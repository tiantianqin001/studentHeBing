package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.Fragment.Dialog.VoteDialog;
import com.telit.zhkt_three.Fragment.Dialog.VoteResultDialog;
import com.telit.zhkt_three.JavaBean.Gson.VoteContentBean;
import com.telit.zhkt_three.JavaBean.Gson.VoteResultGsonBean;
import com.telit.zhkt_three.JavaBean.InterActive.VoteBean;
import com.telit.zhkt_three.JavaBean.InterActive.VoteResultTwoBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/6/21 15:22
 * <p>
 * 设置了两次的连接超时再次请求
 */
public class VoteFragment extends Fragment {

    /**
     * 此次投票的id
     */
    private String voteId;

    public void setVoteId(String voteId) {
        this.voteId = voteId;
    }

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private VoteDialog voteDialog;

    public VoteDialog getVoteDialog() {
        return voteDialog;
    }

    private ScheduledExecutorService timeExecutor;

    private long timerCount;

    private boolean isTimeOver = false;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Operator_Result_Success = 3;

    private static final int Operator_Commit_Result = 4;

    //投票的时长
    private long startTime;
    private long endTime;

    private static boolean isShow=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(getContext(), getResources().getString(R.string.current_net_err), false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        NoSercerDialog noSercerDialog=new NoSercerDialog();
                        noSercerDialog.show(getChildFragmentManager(), NoSercerDialog.class.getSimpleName());
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(getContext(), "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        NoResultDialog noResultDialog = new NoResultDialog();
                        noResultDialog.show(getChildFragmentManager(), NoResultDialog.class.getSimpleName());
                    }

                    break;
                case Operator_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        VoteBean voteBean = (VoteBean) msg.obj;

                        if (isVisible()) {
                            //弹出投票项开始计时
                            startTime = System.currentTimeMillis();
                            //弹出投票项Dialog
                            voteDialog = new VoteDialog();
                            voteDialog.setVoteBean(voteBean);
                            voteDialog.show(getChildFragmentManager(), VoteDialog.class.getSimpleName());
                        }
                    }

                    break;
                case Operator_Result_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        List<VoteResultTwoBean> voteResultTwoBeans = (List<VoteResultTwoBean>) msg.obj;
                        int totalNum = msg.arg1;

                        if (isVisible()) {
                            //弹出投票项Dialog
                            VoteResultDialog voteResultDialog = new VoteResultDialog();
                            voteResultDialog.setVoteResultBean(voteResultTwoBeans, totalNum);
                            voteResultDialog.show(getChildFragmentManager(), VoteResultDialog.class.getSimpleName());
                        }
                    }

                    break;
                case Operator_Commit_Result:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        QZXTools.popToast(getContext(), (String) msg.obj, false);
                        if (voteDialog != null && voteDialog.isVisible()) {
                            voteDialog.dismissAllowingStateLoss();
                            voteDialog = null;
                        }
                    }

                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote_layout, container, false);

        EventBus.getDefault().register(this);
        isShow=true;

//        ImageView vote_result_see = view.findViewById(R.id.vote_result_see);
//
//        vote_result_see.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (TextUtils.isEmpty(voteId)) {
//                    QZXTools.popToast(getContext(), "投票ID为空，无法查询", false);
//                    return;
//                }
//
//                //查询当前的投票结果信息
//                requestVoteResult();
//            }
//        });

        //开启互动计时
        TextView interactive_timer = view.findViewById(R.id.interactive_timer);
        timeExecutor = Executors.newSingleThreadScheduledExecutor();
        timeExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                timerCount++;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isTimeOver) {
                            interactive_timer.setText(QZXTools.getTransmitTime(timerCount));
                        }
                    }
                });

            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        fetchNeedData();

        return view;
    }

    /**
     * 单个Fragment不会被调用，一般仅仅工作在FragmentAdapter中
     * <p>
     * <H1>不被调用<H1/>
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        QZXTools.logE("setUserVisibleHint isVisibleToUser=" + isVisibleToUser, null);
    }

    @Override
    public void onDestroyView() {

        EventBus.getDefault().unregister(this);

        if (timeExecutor != null) {
            isTimeOver = true;
            timeExecutor.shutdown();
            timerCount = 0;
        }

        //防止出现界面消失，网络请求才反馈到
        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }

        //todo 如果dialog没有关闭要关闭
        if (voteDialog != null && voteDialog.isVisible()) {
            voteDialog.dismissAllowingStateLoss();
            voteDialog = null;
        }

        //防泄漏
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        isShow=false;
        super.onDestroyView();
    }

    private int curRetryCount = 0;

    /**
     * {"success":true,"errorCode":"1","msg":"查询投票成功","result":[{"id":"e15f213b00e89041000ab03043c8bbe88d42",
     * "title":"中美关系如何","isMultiplecheck":"0","voteOptions":[{"index":"1","content":"正常","image":null},
     * {"index":"2","content":"复杂","image":null},{"index":"3","content":"恶劣","image":null},
     * {"index":"4","content":"良好","image":null}]}],"total":0,"pageNo":0}
     * <p>
     * 获取投票所需的数据，这里表示id
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNeedData() {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        curRetryCount++;

        //请求投票信息
//        String url = UrlUtils.BaseUrl + UrlUtils.VoteContent;
        String url = UrlUtils.BaseUrl + UrlUtils.QueryVote;
        Map<String, String> paraMap = new LinkedHashMap<>();
        QZXTools.logE("voteId=" + voteId, null);
        paraMap.put("id", voteId);
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
                    VoteContentBean voteContentBean = gson.fromJson(resultJson, VoteContentBean.class);

                    if (voteContentBean.getResult() != null) {
                        VoteBean voteBean = voteContentBean.getResult();
                        Message message = mHandler.obtainMessage();
                        message.what = Operator_Success;
                        message.obj = voteBean;
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

    /**
     * {"success":true,"errorCode":"1","msg":"操作成功","result":[{"title":"中美关系如何",
     * "total":"2","createtime":"2019-06-25 14:23:12","isMultiplecheck":"0","list":[{"ischoose":0,"index":"1",
     * "count":"1","option":"正常"},{"ischoose":0,"index":"2","count":"0","option":"复杂"},
     * {"ischoose":1,"index":"3","count":"1","option":"恶劣"},{"ischoose":0,"index":"4","count":"0","option":"良好"}]}],"total":0,"pageNo":0}
     * <p>
     * 查询投票结果
     * <p>
     * 废弃
     */
    private void requestVoteResult() {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

//        String url = UrlUtils.BaseUrl + UrlUtils.VoteResult;
        String url = UrlUtils.BaseUrl + UrlUtils.VoteResultTwo;
        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("voteId", voteId);
//        paraMap.put("studentid", UserUtils.getStudentId());
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
                        message.what = Operator_Result_Success;
                        message.obj = voteResultTwoBeans;
                        message.arg1 = voteResultGsonBean.getTotal();
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

    /**
     * {"success":true,"errorCode":"1","msg":"提交成功！","result":[],"total":0,"pageNo":0}
     * <p>
     * 投票
     */
    @Subscriber(tag = Constant.Vote_Commit, mode = ThreadMode.MAIN)
    public void commitVote(String voteAnswer) {
        QZXTools.logE("voteAnswer=" + voteAnswer, null);

        if (TextUtils.isEmpty(voteAnswer)) {
            QZXTools.popToast(getContext(), "投票项不能为空哦！", false);
            return;
        }

        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        //提交所投的票
        String url = UrlUtils.BaseUrl + UrlUtils.VoteCommit;
        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("voteId", voteId);
        //更改成UserId
        paraMap.put("studentId", UserUtils.getUserId());
        paraMap.put("answerNum", voteAnswer);
        //新增使用时长
        endTime = System.currentTimeMillis();
        long useTime = endTime - startTime;
        paraMap.put("useTime", QZXTools.getTransmitTime(useTime));

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (call.isCanceled()) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QZXTools.popToast(getContext(), "取消了请求", false);
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
                    String resultJson = response.body().string();
                    QZXTools.logE("resultJson=" + resultJson, null);
                    Gson gson = new Gson();
                    Map<String, Object> resultMap = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    //todo 如果失败的处理是否需要？

                    Message message = mHandler.obtainMessage();
                    message.what = Operator_Commit_Result;
                    message.obj = resultMap.get("msg");
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
