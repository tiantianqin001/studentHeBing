package com.telit.zhkt_three.Fragment.AfterHomeWork;

import android.graphics.Rect;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Adapter.AfterHomeWork.RVAfterHomeWorkAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.AfterHomework.AfterHomeworkBean;
import com.telit.zhkt_three.JavaBean.AfterHomework.HandlerByDateHomeworkBean;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkListBean;
import com.telit.zhkt_three.MyApplication;
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
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/6/4 15:14
 * <p>
 * todo 网络连接正常，但是无法正常使用网络，这个时候问题
 */
public class ToDoHomeWorkFragment extends Fragment {
    private Unbinder unbinder;

    @BindView(R.id.homework_todo_recycler)
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
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        QZXTools.logE("todo onCreateView...", null);

        View view = inflater.inflate(R.layout.fragment_todo_homework_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        isShow=true;

        EventBus.getDefault().register(this);

        //连接网络
        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(getActivity());
            }
        });

        mData = new ArrayList<>();
        rvAfterHomeWorkAdapter = new RVAfterHomeWorkAdapter(getContext(), mData);
        //设置状态是没有完成的作业
        rvAfterHomeWorkAdapter.setType("未完成");

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
                rvAfterHomeWorkAdapter.notifyDataSetChanged();
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
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        requestNetDatas();

        return view;
    }

    /**
     * 注意啊：这个方法setUserVisibleHint在onCreateView前面执行
     * 解决的的方式是：第一个可见fragment直接在onCreateView中执行网络数据请求
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        QZXTools.logE("todo setUserVisibleHint=" + isVisibleToUser, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscriber(tag = Constant.Homework_Commit, mode = ThreadMode.MAIN)
    public void commitCallback(String flag) {
        if (flag.equals("commit_homework")) {
            QZXTools.logE("todo commit callback", null);
            //提交完成刷新todo界面
            curDateString = null;
            afterHomeworkBeans = null;
            handlerByDateHomeworkBean = null;

            mData.clear();
            rvAfterHomeWorkAdapter.notifyDataSetChanged();
            curPageNo = 1;
            requestNetDatas();

//            xRecyclerView.refresh();
        }
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        String url = UrlUtils.BaseUrl + UrlUtils.ToDoHomeWork;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("classid", UserUtils.getClassId());
        mapParams.put("pageNo", curPageNo + "");
        //一次加载十个数据
        mapParams.put("pageSize", PageSize + "");

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
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

                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("todo homework resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        HomeWorkListBean homeWorkListBean = gson.fromJson(resultJson, HomeWorkListBean.class);

                        handlerDateInfo(homeWorkListBean.getResult());

                        mHandler.sendEmptyMessage(Operator_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();

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
                    } else if (originalBean.size() <= PageSize && count == originalBean.size()) {
                        //刚好count等于size
                        handlerByDateHomeworkBean.setAfterHomeworkBeans(afterHomeworkBeans);
                        mData.add(handlerByDateHomeworkBean);
                    }
                }
            }
        }
    }
}
