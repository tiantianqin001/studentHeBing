package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Adapter.interactive.RVSelectGroupAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.Gson.SelectGroupBean;
import com.telit.zhkt_three.JavaBean.InterActive.SelectGroup;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/5/30 19:26
 */
public class SelectGroupFragment extends DialogFragment {
    private Unbinder unbinder;

    @BindView(R.id.select_group_recycler)
    RecyclerView recyclerView;
    @BindView(R.id.select_group_timer)
    TextView select_group_timer;
    @BindView(R.id.select_group_btn_commit)
    TextView select_group_btn_commit;

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

    //进度转圈
    @BindView(R.id.progress_linear)
    LinearLayout progress_linear;

    private SelectGroup selectGroup;

    private RVSelectGroupAdapter selectGroupAdapter;

    private String discussId;

    public String getDiscussId() {
        return discussId;
    }

    public void setDiscussId(String discussId) {
        this.discussId = discussId;
    }

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 3;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(getContext(), "服务端错误！", false);

                    progress_linear.setVisibility(View.GONE);

                    if(leak_resource_layout!=null || request_retry_layout!=null){
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.VISIBLE);
                    }
                    break;
                case Error404:
                    QZXTools.popToast(getContext(), "没有相关资源！", false);

                    progress_linear.setVisibility(View.GONE);

                    if(leak_resource_layout!=null || request_retry_layout!=null){
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.VISIBLE);
                    }
                    break;
                case Operator_Success:
                    progress_linear.setVisibility(View.GONE);

                    if (selectGroup == null) {
                        leak_resource_layout.setVisibility(View.VISIBLE);
                        request_retry_layout.setVisibility(View.GONE);
                    } else {
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.GONE);

                        selectGroupAdapter.setmData(selectGroup.getDiscussionGroup());

                        //更新数据
                        selectGroupAdapter.notifyDataSetChanged();

                        //倒计时
                        if (timer == null) {
                            timer = new Timer();
                        }
                        TimerTask timerTask = new TimerTask() {
                            @Override
                            public void run() {

                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        timeCount--;
                                        select_group_timer.setText(timeCount + "");
                                        if (timeCount == 0) {
                                            timer.cancel();
                                            timer = null;
                                            //还没有提交选组，则随机选一个提交
                                            int size = selectGroup.getDiscussionGroup().size();
                                            int random = (int) Math.round(Math.random() * size);
                                            //提交random下标的小组
                                            String toSendMsg = passMsg(selectGroup.getDiscussionGroup()
                                                            .get(random).getDiscussId() + "",
                                                    selectGroup.getDiscussionGroup()
                                                            .get(random).getGroupIndex());
                                            SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_FREE_JOIN_GROUP,
                                                    MsgUtils.selectedGroup(toSendMsg));
                                        }
                                    }
                                });

                            }
                        };

                        timer.schedule(timerTask, 1000, 1000);

                    }
                    break;
            }
        }
    };


    private Timer timer;
    private int timeCount = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_select_group_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        getDialog().setCanceledOnTouchOutside(false);

        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(getContext());
            }
        });
        request_retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestGroupInfo();
            }
        });

        //提交选组
        select_group_btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                select_group_btn_commit.setEnabled(false);

                if (timeCount > 0) {
                    timer.cancel();
                    timer = null;
                }
                //提交选组信息给教师端
                String toSendMsg = passMsg(selectGroupAdapter.getSelectedGroupId() + "",
                        selectGroupAdapter.getSelectedGroupIndex());
                EventBus.getDefault().post(toSendMsg, Constant.Free_Theme_Over);
            }
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        //展示数据
        selectGroupAdapter = new RVSelectGroupAdapter(getContext());
        recyclerView.setAdapter(selectGroupAdapter);

        requestGroupInfo();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        super.onDestroyView();
    }

    private int discussGroupId;

    /**
     */
    private void requestGroupInfo() {

        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        //转圈
        progress_linear.setVisibility(View.VISIBLE);

        String url = UrlUtils.BaseUrl + UrlUtils.DiscussSelectTheme;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("discussId", discussId);

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
                    QZXTools.logE("requestGroupInfo resultJson=" + resultJson, null);
                    Gson gson = new Gson();
                    SelectGroupBean selectGroupBean = gson.fromJson(resultJson, SelectGroupBean.class);
                    selectGroup = selectGroupBean.getResult();
                    //获取当前分组的组ID
//                    discussGroupId = selectGroup.getDiscussionGroup().get(0).getId();
                    mHandler.sendEmptyMessage(Operator_Success);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * 封装成json格式
     * {"groupIndex":"",
     * "stuInfo":{}
     * }
     */
    private String passMsg(String discussGroupId, String groupIndex) {
        JSONObject childJsonObject = new JSONObject();
        try {
            String className = UserUtils.getClassName();
            String classSplit[] = className.split(" ");

            childJsonObject.put("discussGroupId", discussGroupId);
            childJsonObject.put("studentName", UserUtils.getStudentName());
            childJsonObject.put("gradeName", classSplit[0]);
            childJsonObject.put("className", classSplit[1]);
            childJsonObject.put("photo", UserUtils.getAvatarUrl());
            childJsonObject.put("userId", UserUtils.getUserId());
            childJsonObject.put("groupIndex", groupIndex);
        } catch (JSONException e) {
            e.printStackTrace();
            QZXTools.logE("Json Exception", null);
        }

        QZXTools.logE("toSendMsg = " + childJsonObject.toString(), null);

        return childJsonObject.toString();
    }
}
