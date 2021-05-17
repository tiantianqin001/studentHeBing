package com.telit.zhkt_three.Fragment.AfterHomeWork;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.XToast;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Adapter.AfterHomeWork.HomeworkQuestionExportAdapter;
import com.telit.zhkt_three.Adapter.AfterHomeWork.RVAfterHomeWorkAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.EmojiEditText;
import com.telit.zhkt_three.CustomView.NoScrollRecyclerView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.AfterHomework.AfterHomeworkBean;
import com.telit.zhkt_three.JavaBean.AfterHomework.HandlerByDateHomeworkBean;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkByHandBean;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkByHandBeanTwo;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkListBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.AppInfoUtils;
import com.telit.zhkt_three.Utils.FormatUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.zbv.meeting.util.SharedPreferenceUtil;

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
 * author: qzx
 * Date: 2019/6/4 15:14
 */
public class CompletedHomeWorkFragment extends Fragment implements RVAfterHomeWorkAdapter.OnExportClickListener {

    private Unbinder unbinder;

    @BindView(R.id.homework_completed_recycler)
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

    private RVAfterHomeWorkAdapter rvAfterHomeWorkAdapter;

    private List<HandlerByDateHomeworkBean> mData;

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static boolean isShow=false;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Operator_No_More = 3;
    private static final int Operator_Export= 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(getContext(), "服务端错误！", false);
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
                        QZXTools.popToast(getContext(), "没有相关资源！", false);
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

                        rvAfterHomeWorkAdapter.notifyDataSetChanged();
                    }

                    break;
                case Operator_No_More:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (xRecyclerView != null) {
                            xRecyclerView.refreshComplete();
                            xRecyclerView.loadMoreComplete();
                        }
                        xRecyclerView.setNoMore(true);
                    }

                    break;
                case Operator_Export:

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_completed_homework_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        EventBus.getDefault().register(this);
        isShow=true;

        //连接网络
        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(getActivity());
            }
        });

        mData = new ArrayList<>();
        rvAfterHomeWorkAdapter = new RVAfterHomeWorkAdapter(getContext(), mData,this);
        rvAfterHomeWorkAdapter.setTypes(1);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, getResources().getDimensionPixelSize(R.dimen.x5), 0, getResources().getDimensionPixelSize(R.dimen.x5));
            }
        });

        xRecyclerView.setAdapter(rvAfterHomeWorkAdapter);


        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRefresh() {
                curDateString = null;
                afterHomeworkBeans = null;
                handlerByDateHomeworkBean = null;

                mData.clear();
              //  rvAfterHomeWorkAdapter.notifyDataSetChanged();
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


        requestNetDatas();

        return view;

    }



    @Subscriber(tag = Constant.Homework_Commit, mode = ThreadMode.MAIN)
    public void commitCallback(String flag) {
        if (flag.equals("commit_homework")) {
            QZXTools.logE("completed commit callback", null);
            //提交完成刷新todo界面
            isNeedRefresh = true;
        }
    }


    @Subscriber(tag =Constant.upDataState, mode = ThreadMode.MAIN)
    public void upDataState(String upDataState) {
        mData.clear();
        rvAfterHomeWorkAdapter.notifyDataSetChanged();
        curPageNo = 1;
        requestNetDatas();
    }

    private boolean isNeedRefresh = true;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        QZXTools.logE("completed setUserVisibleHint" + isVisibleToUser, null);
/*        if (isVisibleToUser) {
            if (isNeedRefresh) {
                isNeedRefresh = false;

                if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                    circleProgressDialogFragment.dismissAllowingStateLoss();
                    circleProgressDialogFragment = null;
                }
                circleProgressDialogFragment = new CircleProgressDialogFragment();
                circleProgressDialogFragment.show(getActivity().getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

                curDateString = null;
                afterHomeworkBeans = null;
                handlerByDateHomeworkBean = null;

                mData.clear();
                rvAfterHomeWorkAdapter.notifyDataSetChanged();
                curPageNo = 1;
                requestNetDatas();
            }
        }*/
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onResume() {
        super.onResume();
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getActivity().getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        curDateString = null;
        afterHomeworkBeans = null;
        handlerByDateHomeworkBean = null;
        mData.clear();
        //rvAfterHomeWorkAdapter.notifyDataSetChanged();
        curPageNo = 1;
        requestNetDatas();
    }

    @Override
    public void onDestroyView() {
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
        super.onDestroyView();
    }

    private static final int PageSize = 10;

    /**
     * 请求网络数据
     */

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

        String url = UrlUtils.BaseUrl + UrlUtils.CompletedHomeWork;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("classid", UserUtils.getClassId());
        mapParams.put("pageNo", curPageNo + "");
        mapParams.put("pageSize", PageSize + "");

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
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
                        QZXTools.logE("completed homework resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        HomeWorkListBean homeWorkListBean = gson.fromJson(resultJson, HomeWorkListBean.class);

                        if (homeWorkListBean.getResult().size() <= 0) {
                            mHandler.sendEmptyMessage(Operator_No_More);
                        } else {
                            handlerDateInfo(homeWorkListBean.getResult());

                            mHandler.sendEmptyMessage(Operator_Success);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        //mHandler.sendEmptyMessage(Server_Error);
                       // QZXTools.popToast(MyApplication.getInstance(),"网络错误",true);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    private String curDateString;

    private HandlerByDateHomeworkBean handlerByDateHomeworkBean;
    private List<AfterHomeworkBean> afterHomeworkBeans;

    /**
     * 整合处理日期数据
     * <p>
     * 注意：一次加载十个
     */
    private void handlerDateInfo(List<AfterHomeworkBean> originalBean) {
        //计数
        int count = 0;

        boolean isStartEnter = true;
        for (AfterHomeworkBean afterHomeworkBean : originalBean) {
            String[] emptyStrs = afterHomeworkBean.getStartDate().split(" ");
            if (TextUtils.isEmpty(curDateString)) {
                isStartEnter = false;
                curDateString = emptyStrs[0];
                handlerByDateHomeworkBean = new HandlerByDateHomeworkBean();
                handlerByDateHomeworkBean.setSameDate(curDateString);
                afterHomeworkBeans = new ArrayList<>();
                afterHomeworkBeans.add(afterHomeworkBean);
                count++;
                //刚好只有条
                if (originalBean.size() == count) {
                    handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                    mData.add(handlerByDateHomeworkBean);
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
                    afterHomeworkBeans.add(afterHomeworkBean);
                    count++;
                    //结尾数据都是同一天的哦
                    if (count == PageSize) {
                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                        mData.add(handlerByDateHomeworkBean);
                    } else if (originalBean.size() <= 10 && count == originalBean.size()) {
                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                        mData.add(handlerByDateHomeworkBean);
                    }
                } else {
//                    isStartEnter = false;
//                    curDateString = emptyStrs[0];
//                    handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
//                    mData.add(handlerByDateHomeworkBean);
//                    //重置
//                    afterHomeworkBeans = null;
//                    handlerByDateHomeworkBean = null;
//                    handlerByDateHomeworkBean = new HandlerByDateHomeworkBean();
//                    handlerByDateHomeworkBean.setSameDate(curDateString);
//                    afterHomeworkBeans = new ArrayList<>();
//                    afterHomeworkBeans.add(afterHomeworkBean);
//                    count++;
//                    if (count == 10) {
//                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
//                        mData.add(handlerByDateHomeworkBean);
//                    }

                    //刚好第一次进入日期就不同，如果不是第一次进入这个模块的话，把上一次的结果添加给集合
                    if (!isStartEnter) {
                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                        mData.add(handlerByDateHomeworkBean);
                    }

                    isStartEnter = false;
                    curDateString = emptyStrs[0];

                    //重置
                    afterHomeworkBeans = null;
                    handlerByDateHomeworkBean = null;
                    handlerByDateHomeworkBean = new HandlerByDateHomeworkBean();
                    handlerByDateHomeworkBean.setSameDate(curDateString);
                    afterHomeworkBeans = new ArrayList<>();
                    afterHomeworkBeans.add(afterHomeworkBean);
                    count++;
                    if (count == PageSize) {
                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                        mData.add(handlerByDateHomeworkBean);
                    }else if (originalBean.size() <= PageSize && count == originalBean.size()) {
                        //刚好count等于size
                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                        mData.add(handlerByDateHomeworkBean);
                    }
                }
            }
        }
    }

    //导出的点击
    @Override
    public void onExportClick(View view, String homeworkId,String byHand,String homeworkName,String status) {
        QZXTools.logE("导出的点击:"+homeworkId,null);

        if (!ViewUtils.isFastClick(1000)){
            return;
        }

        if ("1".equals(byHand)){//拍照出题
            showImageQuestionDialog(homeworkId,status,homeworkName);
        }else {//题库出题
            fetchNetHomeWorkDatas(homeworkId,byHand,homeworkName,status);
        }
    }

    private HomeWorkByHandBean homeWorkByHandBean;
    private HomeWorkByHandBeanTwo homeWorkByHandBeanTwo;

    /**
     * 请求作业详情数据
     *
     * @param homeworkId
     * @param byHand
     */
    private void fetchNetHomeWorkDatas(String homeworkId,String byHand,String homeworkName,String status) {
        homeWorkByHandBean = null;
        homeWorkByHandBeanTwo = null;

        String url;
        //题库出题和图片出题调用不同的URL
        if ("1".equals(byHand)) {
            url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHand;
        } else {
            url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHandTwo;
        }

        Map<String, String> mapParams = new LinkedHashMap<>();

        mapParams.put("homeworkid", homeworkId);
        mapParams.put("status", "1");
        mapParams.put("studentid", UserUtils.getUserId());

        QZXTools.logE("mapParams:" + new Gson().toJson(mapParams), null);

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

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
                    QZXTools.logE("resultJson=" + resultJson, null);
                    Gson gson = new Gson();


                    if ("1".equals(byHand)) {//图片出题
                        homeWorkByHandBean = gson.fromJson(resultJson, HomeWorkByHandBean.class);
//                    QZXTools.logE("homeWorkByHandBean=" + homeWorkByHandBean, null);

//                        generateImgDocx(homeWorkByHandBean,homeworkName);
                    } else {
                        homeWorkByHandBeanTwo = gson.fromJson(resultJson, HomeWorkByHandBeanTwo.class);
//                    QZXTools.logE("homeWorkByHandBean=" + homeWorkByHandBean, null);

//                        generateDocx(homeWorkByHandBeanTwo,homeworkName);
                    }

                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (homeWorkByHandBean!=null){
                            }else {
                                if (homeWorkByHandBeanTwo!=null){
                                    showNoImageQuestionDialog(homeWorkByHandBeanTwo.getResult(),homeworkId,status,homeworkName);
                                }
                            }
                        }
                    });
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    private XToast toast;
    private boolean checkedAll;
    private HomeworkQuestionExportAdapter exportAdapter;
    private String flag;
    private EmojiEditText et_email;

    /**
     * 题库出题选择
     *
     * @param list
     * @param homeworkId
     * @param status
     * @param homeworkName
     */
    private void showNoImageQuestionDialog(List<QuestionBank> list,String homeworkId,String status,String homeworkName) {
        toast = new XToast(getActivity())
                .setView(R.layout.toast_export_questions)
                .setOutsideTouchable(false)
                .setBackgroundDimAmount(0.5f)
                .setText(R.id.tv_name,"作业导出")
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setGravity(Gravity.CENTER)
                .setOnClickListener(R.id.iv_close, new OnClickListener() {
                    @Override
                    public void onClick(XToast toast, View view) {
                        toast.cancel();

                        flag = null;
                    }
                })
                .setOnClickListener(R.id.tv_questions, new OnClickListener() {
                    @Override
                    public void onClick(XToast toast, View view) {
                        TextView tv_questions = (TextView) view;
                        Drawable leftDrawable;
                        if (checkedAll){
                            checkedAll = false;
                            for (QuestionBank questionBank:list){
                                questionBank.setChecked(false);
                            }
                            leftDrawable = getResources().getDrawable(R.mipmap.contact_unchecked_icon);
                        }else {
                            checkedAll = true;
                            for (QuestionBank questionBank:list){
                                questionBank.setChecked(true);
                            }
                            leftDrawable = getResources().getDrawable(R.mipmap.contact_checked_icon);
                        }
                        exportAdapter.notifyDataSetChanged();
                        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
                        tv_questions.setCompoundDrawables(leftDrawable, null, null, null);
                    }
                })
                .setOnClickListener(R.id.btn_send, new OnClickListener() {
                    @Override
                    public void onClick(XToast toast, View view) {
                        if (TextUtils.isEmpty(getQuestionIds(list))){
                            QZXTools.popToast(getContext(), "请选择导出的题目", false);
                            return;
                        }

                        ImageView iv_process = toast.getView().findViewById(R.id.iv_process);
                        TextView tv_questions = toast.getView().findViewById(R.id.tv_questions);
                        RelativeLayout rl_email = toast.getView().findViewById(R.id.rl_email);
                        Button btn_send = toast.getView().findViewById(R.id.btn_send);
                        NoScrollRecyclerView rv_questions = toast.getView().findViewById(R.id.rv_questions);
                        ImageView iv_status = toast.getView().findViewById(R.id.iv_sendStatus);

                        if (TextUtils.isEmpty(flag)){
                            iv_process.setImageResource(R.mipmap.input_email);
                            tv_questions.setVisibility(View.GONE);
                            rv_questions.setVisibility(View.GONE);
                            rl_email.setVisibility(View.VISIBLE);

                            flag = "1";
                        }else if ("1".equals(flag)){
                            //校验邮箱
                            if (TextUtils.isEmpty(et_email.getText().toString())){
                                QZXTools.popToast(getContext(), "邮箱不可为空", false);
                                return;
                            }

                            if (!FormatUtils.isEmail(et_email.getText().toString())){
                                QZXTools.popToast(getContext(), "邮箱格式不正确", false);
                                return;
                            }

                            iv_process.setImageResource(R.mipmap.send_finish);
                            rl_email.setVisibility(View.GONE);
                            iv_status.setVisibility(View.VISIBLE);
                            btn_send.setText("完成");

                            flag = "2";

                            //提交
                            sendEmailFromQuestionRank(homeworkId,getQuestionIds(list),status,et_email.getText().toString(),iv_status,homeworkName);
                        }else if ("2".equals(flag)){
                            iv_process.setImageResource(R.mipmap.question_export);

                            toast.cancel();
                            flag = null;
                        }
                    }
                })
                .show();

        NoScrollRecyclerView rv_questions = toast.getView().findViewById(R.id.rv_questions);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_questions.setLayoutManager(manager);
        exportAdapter = new HomeworkQuestionExportAdapter(getActivity(),list);
        rv_questions.setAdapter(exportAdapter);
        exportAdapter.setOnCheckListener(new HomeworkQuestionExportAdapter.OnCheckListener() {
            @Override
            public void OnCheckListener(int position) {
                QZXTools.logE("选择:"+position,null);

                list.get(position).setChecked(!list.get(position).isChecked());
                exportAdapter.notifyDataSetChanged();

                TextView tv_questions = toast.getView().findViewById(R.id.tv_questions);
                Drawable leftDrawable;
                checkedAll = checkedAll(list);
                QZXTools.logE("checkedAll:"+checkedAll,null);
                if (checkedAll){
                    leftDrawable = getResources().getDrawable(R.mipmap.contact_checked_icon);
                }else {
                    leftDrawable = getResources().getDrawable(R.mipmap.contact_unchecked_icon);
                }
                leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
                tv_questions.setCompoundDrawables(leftDrawable, null, null, null);
            }
        });

        et_email = toast.getView().findViewById(R.id.et_email);
        if (!TextUtils.isEmpty(SharedPreferenceUtil.getInstance(getActivity()).getString("exportEmail"))){
            et_email.setText(SharedPreferenceUtil.getInstance(getActivity()).getString("exportEmail"));
            et_email.setSelection(SharedPreferenceUtil.getInstance(getActivity()).getString("exportEmail").length());
        }
    }

    /**
     * 题库出题选择
     *
     * @param homeworkId
     * @param status
     * @param homeworkName
     */
    private void showImageQuestionDialog(String homeworkId, String status, String homeworkName) {
        toast = new XToast(getActivity())
                .setView(R.layout.toast_export_questions)
                .setOutsideTouchable(false)
                .setBackgroundDimAmount(0.5f)
                .setText(R.id.tv_name,"作业导出")
                .setAnimStyle(android.R.style.Animation_Translucent)
                .setGravity(Gravity.CENTER)
                .setOnClickListener(R.id.iv_close, new OnClickListener() {
                    @Override
                    public void onClick(XToast toast, View view) {
                        toast.cancel();

                        flag = null;
                    }
                })
                .setOnClickListener(R.id.btn_send, new OnClickListener() {
                    @Override
                    public void onClick(XToast toast, View view) {
                        ImageView iv_process = toast.getView().findViewById(R.id.iv_process);
                        RelativeLayout rl_email = toast.getView().findViewById(R.id.rl_email);
                        Button btn_send = toast.getView().findViewById(R.id.btn_send);
                        ImageView iv_status = toast.getView().findViewById(R.id.iv_sendStatus);

                        if ("1".equals(flag)){
                            //校验邮箱
                            if (TextUtils.isEmpty(et_email.getText().toString())){
                                QZXTools.popToast(getContext(), "邮箱不可为空", false);
                                return;
                            }

                            if (!FormatUtils.isEmail(et_email.getText().toString())){
                                QZXTools.popToast(getContext(), "邮箱格式不正确", false);
                                return;
                            }

                            iv_process.setImageResource(R.mipmap.send_finish);
                            rl_email.setVisibility(View.GONE);
                            iv_status.setVisibility(View.VISIBLE);
                            btn_send.setText("完成");

                            flag = "2";

                            //提交
                            sendEmailFromImage(homeworkId,status,et_email.getText().toString(),iv_status,homeworkName);
                        }else if ("2".equals(flag)){
                            iv_process.setImageResource(R.mipmap.question_export);

                            toast.cancel();
                            flag = null;
                        }
                    }
                })
                .show();

        et_email = toast.getView().findViewById(R.id.et_email);
        if (!TextUtils.isEmpty(SharedPreferenceUtil.getInstance(getActivity()).getString("exportEmail"))){
            et_email.setText(SharedPreferenceUtil.getInstance(getActivity()).getString("exportEmail"));
            et_email.setSelection(SharedPreferenceUtil.getInstance(getActivity()).getString("exportEmail").length());
        }
        TextView tv_questions = toast.getView().findViewById(R.id.tv_questions);
        NoScrollRecyclerView rv_questions = toast.getView().findViewById(R.id.rv_questions);
        RelativeLayout rl_email = toast.getView().findViewById(R.id.rl_email);
        tv_questions.setCompoundDrawables(null, null, null, null);

        ImageView iv_process = toast.getView().findViewById(R.id.iv_process);
        iv_process.setImageResource(R.mipmap.input_email);
        tv_questions.setVisibility(View.GONE);
        rv_questions.setVisibility(View.GONE);
        rl_email.setVisibility(View.VISIBLE);
        flag = "1";
    }

    /**
     * 是否全选
     *
     * @param list
     * @return
     */
    private boolean checkedAll(List<QuestionBank> list){
        for (QuestionBank questionBank:list){
            if (!questionBank.isChecked()){
                return false;
            }
        }
        return true;
    }

    /**
     * 获取问题Id
     *
     * @param list
     * @return
     */
    private String getQuestionIds(List<QuestionBank> list){
        StringBuffer questionIds = new StringBuffer();
        for (int i=0;i<list.size();i++){
            if (list.get(i).isChecked()){
                questionIds.append(list.get(i).getId()+",");
            }
        }
        if (questionIds.toString().length()>0){
            return questionIds.toString().substring(0,questionIds.toString().length()-1);
        }else {
            return questionIds.toString();
        }
    }

    /**
     * 发送邮件
     *
     * @param homeworkId
     * @param questionIds
     * @param status
     * @param email
     * @param iv_status
     */
    private void sendEmailFromQuestionRank(String homeworkId, String questionIds, String status,String email,ImageView iv_status,String homeworkName){
        String url = UrlUtils.BaseUrl + UrlUtils.Homework_Export;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("homeworkid", homeworkId);
        mapParams.put("questionids", questionIds);
        mapParams.put("status", status);
        mapParams.put("email", email);
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("title", homeworkName);
        mapParams.put("tip", AppInfoUtils.getAppName(getActivity())+"导出已完成作业");

        QZXTools.logE("param:"+new Gson().toJson(mapParams),null);

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QZXTools.popToast(getActivity(), "服务端错误！", false);
                        iv_status.setImageResource(R.mipmap.email_send_fail);
                        QZXTools.logE("onFailure", e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = null;
                    try {
                        resultJson = response.body().string();
                        QZXTools.logE("commit questions resultJson=" + resultJson, null);

                        JSONObject jsonObject=JSONObject.parseObject(resultJson);
                        String errorCode = jsonObject.getString("errorCode");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("1".equals(errorCode)){
                                    iv_status.setImageResource(R.mipmap.email_send_success);
                                }else {
                                    iv_status.setImageResource(R.mipmap.email_send_fail);
                                }
                            }
                        });

                        SharedPreferenceUtil.getInstance(getActivity()).setString("exportEmail",et_email.getText().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    iv_status.setImageResource(R.mipmap.email_send_fail);
                }
            }
        });
    }

    /**
     * 发送邮件
     *
     * @param homeworkId
     * @param status
     * @param email
     * @param iv_status
     */
    private void sendEmailFromImage(String homeworkId,String status,String email,ImageView iv_status,String homeworkName){
        String url = UrlUtils.BaseUrl + UrlUtils.Homework_Export_Image;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("homeworkid", homeworkId);
        mapParams.put("status", status);
        mapParams.put("email", email);
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("title", homeworkName);
        mapParams.put("tip", AppInfoUtils.getAppName(getActivity())+"导出已完成作业");

        QZXTools.logE("param:"+new Gson().toJson(mapParams),null);

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QZXTools.popToast(getActivity(), "服务端错误！", false);
                        iv_status.setImageResource(R.mipmap.email_send_fail);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = null;
                    try {
                        resultJson = response.body().string();
                        QZXTools.logE("commit questions resultJson=" + resultJson, null);

                        JSONObject jsonObject=JSONObject.parseObject(resultJson);
                        String errorCode = jsonObject.getString("errorCode");

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("1".equals(errorCode)){
                                    iv_status.setImageResource(R.mipmap.email_send_success);
                                }else {
                                    iv_status.setImageResource(R.mipmap.email_send_fail);
                                }
                            }
                        });

                        SharedPreferenceUtil.getInstance(getActivity()).setString("exportEmail",et_email.getText().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    iv_status.setImageResource(R.mipmap.email_send_fail);
                }
            }
        });
    }
}
