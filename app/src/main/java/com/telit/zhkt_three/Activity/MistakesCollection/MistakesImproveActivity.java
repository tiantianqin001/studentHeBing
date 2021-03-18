package com.telit.zhkt_three.Activity.MistakesCollection;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.RVQuestionAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.Gson.KnowledgeQuestionsBean;
import com.telit.zhkt_three.JavaBean.MistakesCollection.ImproveKnowledgeInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;

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
 * 巩固提升：引用自主学习题库，所以暂时展示用吧
 */
public class MistakesImproveActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.improvement_back)
    ImageView improvement_back;

    @BindView(R.id.mistakes_improve_swipeRefresh)
    SwipeRefreshLayout mistakes_improve_swipeRefresh;
    @BindView(R.id.mistakes_improve_rv)
    RecyclerView mistakes_improve_rv;

    //-----------无网络或者无资源
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    //题型内容适配器
    private RVQuestionAdapter questionAdapter;
    private List<QuestionBank> questionBankList;
    private LinearLayoutManager linearLayoutManager;

    private int curPageNo = 1;
    private String learning_section;//学段
    private String subject;//学科
    private String json_knowledge;//知识点Json
    private String questionType;//题型
    private String difficulty;//难度

    private static final int Default_Count_Load = 3;

    private static boolean isShow=false;
    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Refresh_Start = 7;
    private static final int Refresh_End = 8;
    private static final int No_Resource = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (  isShow){
                        QZXTools.popToast(MistakesImproveActivity.this, "服务端错误！", false);
                        if (mistakes_improve_swipeRefresh.isRefreshing())
                            mistakes_improve_swipeRefresh.setRefreshing(false);
                        break;
                    }

                case Error404:
                    if (  isShow){
                        QZXTools.popToast(MistakesImproveActivity.this, "没有相关资源！", false);
                        if (mistakes_improve_swipeRefresh.isRefreshing())
                            mistakes_improve_swipeRefresh.setRefreshing(false);
                    }

                    break;
                case Refresh_Start:
                    if (  isShow){
                        mistakes_improve_swipeRefresh.setRefreshing(true);
                    }

                    break;
                case Refresh_End:
                    if (  isShow){
                        leak_resource.setVisibility(View.GONE);
                        if (mistakes_improve_swipeRefresh.isRefreshing())
                            mistakes_improve_swipeRefresh.setRefreshing(false);
                        questionAdapter.notifyDataSetChanged();
                    }

                    break;
                case No_Resource:
                    if (  isShow){
                        leak_resource.setVisibility(View.VISIBLE);
                        if (mistakes_improve_swipeRefresh.isRefreshing())
                            mistakes_improve_swipeRefresh.setRefreshing(false);
                        questionBankList.clear();
                        questionAdapter.notifyDataSetChanged();
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mistakes_improve);
        unbinder = ButterKnife.bind(this);
        isShow=true;

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("improvement");
        if (bundle != null) {
            learning_section = bundle.getString("xd");
            subject = bundle.getString("subject");
            difficulty = bundle.getString("difficulty");
            questionType = bundle.getString("type");
            String knowledgeJson = bundle.getString("knowledge_json");

            //                Gson gson = new Gson();
//                List<ImproveKnowledgeInfo> infos = gson.fromJson(knowledgeInfo, new TypeToken<List<ImproveKnowledgeInfo>>() {
//                }.getType());
//                QZXTools.logE("infos=" + infos, null);

            //额外的优点在于驼峰式命名自动转换识别
            List<ImproveKnowledgeInfo> knowledgeInfos = JSONObject.parseArray(knowledgeJson, ImproveKnowledgeInfo.class);
//            QZXTools.logE("knowledge_info=" + knowledgeInfos, null);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < knowledgeInfos.size(); i++) {
                stringBuilder.append(knowledgeInfos.get(i).getTid());
                if (i < (knowledgeInfos.size() - 1)) {
                    stringBuilder.append(",");
                }
            }
            json_knowledge = stringBuilder.toString();
        }

        improvement_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(MistakesImproveActivity.this);
            }
        });

        mistakes_improve_swipeRefresh.setColorSchemeResources(R.color.colorAccent);
        mistakes_improve_swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新请求题型数据
                requestQuestions(false);
            }
        });

        questionBankList = new ArrayList<>();
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mistakes_improve_rv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mistakes_improve_rv.setLayoutManager(linearLayoutManager);
        mistakes_improve_rv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 7, 0, 7);
            }
        });
        questionAdapter = new RVQuestionAdapter(this, questionBankList);
        mistakes_improve_rv.setAdapter(questionAdapter);

        mistakes_improve_rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (questionAdapter.isAllEnd()) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        linearLayoutManager.findLastVisibleItemPosition() >= questionBankList.size() - Default_Count_Load) {
                    questionAdapter.setFootVisible(true);
                    curPageNo++;
                    QZXTools.logE("要加载更多... curPageNo=" + curPageNo, null);
                    requestQuestions(true);
                }
            }
        });

        requestQuestions(false);

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        isShow=false;
        //防止泄露
        QZXTools.setmToastNull();
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    /**
     * 请求数据
     * <p>
     * questionType为空可以不传入该参数
     */
    private void requestQuestions(boolean loadMore) {

        //学科全部是：空字符串
        if (learning_section == null || learning_section.equals("") || subject == null) {
            return;
        }

        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        //非加载清空重新依据条件加载
        if (!loadMore) {
            //说明是重新刷新
            curPageNo = 1;
            questionAdapter.setAllEnd(false);
            questionBankList.clear();
            questionAdapter.notifyDataSetChanged();
            mHandler.sendEmptyMessage(Refresh_Start);
        }

        String url = UrlUtils.BaseUrl + UrlUtils.QuestionKnowledgeBankQuery1;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("xd", learning_section);
        mapParams.put("chid", subject);
        if (!TextUtils.isEmpty(questionType)) {
            mapParams.put("questionChannelType", questionType);
        }
        mapParams.put("difficultIndex", difficulty);
        mapParams.put("tKnowledge", json_knowledge);
        mapParams.put("pageNo", curPageNo + "");

        QZXTools.logE("xd=" + learning_section + ";chid=" + subject + ";pageNo=" + curPageNo
                + ";questionChannelType=" + questionType + ";difficultIndex=" + difficulty + ";tKnowledge=" + json_knowledge, null);

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
                    String resultJson = response.body().string();
//                    QZXTools.logE("resultJson=" + resultJson, null);
                    Gson gson = new Gson();
                    KnowledgeQuestionsBean knowledgeQuestionsBean = gson.fromJson(resultJson, KnowledgeQuestionsBean.class);

                    if (knowledgeQuestionsBean.getResult().size() <= 0) {
                        if (!loadMore) {
                            mHandler.sendEmptyMessage(No_Resource);
                            return;
                        } else {
                            //没有数据了
                            questionAdapter.setAllEnd(true);
                        }
                    } else {
                        for (QuestionBank questionBank : knowledgeQuestionsBean.getResult()) {
                            questionBankList.add(questionBank);
                        }
                        //加载到数据底部不可见
                        questionAdapter.setFootVisible(false);
                    }
                    mHandler.sendEmptyMessage(Refresh_End);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
