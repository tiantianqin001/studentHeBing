package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Adapter.interactive.CollectPracticeRVAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.Gson.CollectionBean;
import com.telit.zhkt_three.JavaBean.InterActive.CollectInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

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
 * Date: 2019/5/25 10:44
 */
public class CollectDisplayDialog extends DialogFragment {

    private Unbinder unbinder;
    @BindView(R.id.collect_display_recycler)
    XRecyclerView xRecyclerView;
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
     * 当前分页的页数，初始化是1
     */
    private int curPageNo = 1;

    private List<CollectInfo> mData;

    private CollectPracticeRVAdapter collectPracticeRVAdapter;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(getContext(), "服务端错误！", false);

                    if (xRecyclerView != null) {
                        xRecyclerView.refreshComplete();
                        xRecyclerView.loadMoreComplete();
                    }

                    if(leak_resource_layout!=null || request_retry_layout!=null){
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.VISIBLE);
                    }
                    break;
                case Error404:
                    QZXTools.popToast(getContext(), "没有相关资源！", false);

                    if (xRecyclerView != null) {
                        xRecyclerView.refreshComplete();
                        xRecyclerView.loadMoreComplete();
                    }

                    if(leak_resource_layout!=null || request_retry_layout!=null){
                        leak_resource_layout.setVisibility(View.GONE);
                        request_retry_layout.setVisibility(View.VISIBLE);
                    }

                    break;
                case Operator_Success:
                    if (xRecyclerView != null) {
                        xRecyclerView.refreshComplete();
                        xRecyclerView.loadMoreComplete();
                    }

                    if (mData.size() > 0) {
                        request_retry_layout.setVisibility(View.GONE);
                        leak_resource_layout.setVisibility(View.GONE);
                    } else {
                        request_retry_layout.setVisibility(View.GONE);
                        leak_resource_layout.setVisibility(View.VISIBLE);
                    }

                    collectPracticeRVAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collect_display_dialog_layout, container, false);

        ButterKnife.bind(this, view);

        //可以取消重置样式设置
        getDialog().setCanceledOnTouchOutside(true);

        //连接网络
        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(getActivity());
            }
        });

        xRecyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        xRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mData = new ArrayList<>();
        collectPracticeRVAdapter = new CollectPracticeRVAdapter(getContext(), mData);
        xRecyclerView.setAdapter(collectPracticeRVAdapter);

        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mData.clear();
                collectPracticeRVAdapter.notifyDataSetChanged();
                curPageNo = 1;
                requestAllCollects();
            }

            @Override
            public void onLoadMore() {
                curPageNo++;
                requestAllCollects();
            }
        });

        requestAllCollects();

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

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "查询我的收藏成功",
     * "result": [
     * {
     * "id": 25,
     * "collectId": "657030420102004b410aea2056c8effd5715",
     * "collectType": "1",
     * "collectName": "123",
     * "userId": null,
     * "createDate": "2019-07-04 21:00:57",
     * "delFlag": 0
     * }
     * ],
     * "total": 1,
     * "pageNo": 1
     * }
     * <p>
     * 请求收藏列表
     */
    private void requestAllCollects() {
        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        String url = UrlUtils.BaseUrl + UrlUtils.CollectQuery;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("userId", UserUtils.getUserId());
        paraMap.put("pageNo", curPageNo + "");
//        mapParams.put("pageSize", 10+"");

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
                    CollectionBean collectionBean = gson.fromJson(resultJson, CollectionBean.class);
                    for (CollectInfo collectInfo : collectionBean.getResult()) {
                        mData.add(collectInfo);
                    }
                    mHandler.sendEmptyMessage(Operator_Success);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
