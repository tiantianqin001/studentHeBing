package com.telit.zhkt_three.Activity.ClassRecord;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.ClassRecord.RVClassRecordAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.ClassRecord.ClassRecord;
import com.telit.zhkt_three.JavaBean.Gson.ClassRecordBean;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.StudentInfoDao;

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
 * 废弃
 */
public class ClassRecordActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.class_record_head)
    RelativeLayout class_record_head;
    @BindView(R.id.class_record_head_layout)
    CustomHeadLayout class_record_head_layout;
    @BindView(R.id.class_record_refresh)
    SwipeRefreshLayout class_record_refresh;
    @BindView(R.id.class_record_recycler)
    RecyclerView class_record_recycler;

    //-----------无网络或者无资源
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    private LinearLayoutManager linearLayoutManager;
    private RVClassRecordAdapter rvClassRecordAdapter;
    private List<ClassRecord> classRecordList;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    /**
     * 当前的页码
     */
    private int curPageNo = 1;
    private static final String pageSize = "5";
    private static final int Default_Count_Load = 3;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(ClassRecordActivity.this, "当前网络不佳....", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    break;
                case Error404:
                    QZXTools.popToast(ClassRecordActivity.this, "没有相关资源！", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    break;
                case Operator_Success:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    if (class_record_refresh.isRefreshing()) {
                        class_record_refresh.setRefreshing(false);
                    }

                    if (classRecordList != null && classRecordList.size() > 0) {
                        leak_resource.setVisibility(View.GONE);
                        rvClassRecordAdapter.setmDatas(classRecordList);
                        rvClassRecordAdapter.notifyDataSetChanged();
                    } else {
                        leak_resource.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_record);
        unbinder = ButterKnife.bind(this);

        //设置头像信息等
        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                .where(StudentInfoDao.Properties.StudentId.eq(UserUtils.getStudentId())).list().get(0);
        if (studentInfo != null) {
            String clazz;
            if (studentInfo.getClassName() != null) {
                if (studentInfo.getGradeName() != null) {
                    clazz = studentInfo.getGradeName().concat(studentInfo.getClassName());
                } else {
                    clazz = studentInfo.getClassName();
                }
            } else {
                clazz = "";
            }
            class_record_head_layout.setHeadInfo(studentInfo.getPhoto(), studentInfo.getStudentName(), clazz);
        }

        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(ClassRecordActivity.this);
            }
        });
        linearLayoutManager = new LinearLayoutManager(this);
        class_record_recycler.setLayoutManager(linearLayoutManager);
        rvClassRecordAdapter = new RVClassRecordAdapter(ClassRecordActivity.this);
        class_record_recycler.setAdapter(rvClassRecordAdapter);

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        requestClassRecordInfo(false);

        class_record_refresh.setColorSchemeResources(R.color.colorAccent);
        class_record_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //重新请求题型数据
                requestClassRecordInfo(false);
            }
        });

        class_record_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (rvClassRecordAdapter.isAllEnd()) {
                    return;
                }
                if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                        linearLayoutManager.findLastVisibleItemPosition() >= classRecordList.size() - Default_Count_Load) {
                    rvClassRecordAdapter.setFootVisible(true);
                    curPageNo++;
                    QZXTools.logE("要加载更多... curPageNo=" + curPageNo, null);
                    requestClassRecordInfo(true);
                }
            }
        });
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

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "操作成功",
     * "result": [{
     * "title": null,
     * "id": null,
     * "type": null,
     * "typeName": null,
     * "interData": null,
     * "date": "2019-06-28",
     * "countInterationList": [{
     * "title": "666",
     * "id": "0ea85e4971074b4ca1ead5085ec8aca2",
     * "type": "2",
     * "typeName": "分组讨论",
     * "interData": null,
     * "date": null,
     * "countInterationList": null
     * },
     * {
     * "title": "",
     * "id": "b860ced30458504cf9098dc06568c38ed1d9",
     * "type": "3",
     * "interData": null,
     * "date": null,
     * "countInterationList": null
     * }]
     * },
     * {
     * "title": null,
     * "id": null,
     * "type": null,
     * "typeName": null,
     * "interData": null,
     * "date": "2019-06-27",
     * "countInterationList": [{
     * "title": "3232",
     * "id": "4d5c17e70cba004d130ab0b0860c20e4cf44",
     * "type": "1",
     * "typeName": "投票",
     * "interData": null,
     * "date": null,
     * "countInterationList": null
     * }]
     * }
     * ],
     * "total": 2,
     * "pageNo": 1
     * }
     * <p>
     * 请求课堂记录的参数
     */
    private void requestClassRecordInfo(boolean isLoadMore) {
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (!isLoadMore) {
            if (classRecordList != null && classRecordList.size() > 0) {
                curPageNo = 1;
                classRecordList.clear();
                rvClassRecordAdapter.notifyDataSetChanged();
            }
        }

        String url = UrlUtils.BaseUrl + UrlUtils.ClassRecord;
        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("studentId", UserUtils.getStudentId());
        paraMap.put("pageNo", curPageNo + "");
        paraMap.put("pageSize", pageSize);
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
                    ClassRecordBean classRecordBean = gson.fromJson(resultJson, ClassRecordBean.class);
                    if (isLoadMore) {
                        if (classRecordBean.getResult() != null && classRecordBean.getResult().size() > 0) {
                            rvClassRecordAdapter.setAllEnd(false);
                            //加载到数据底部不可见
                            rvClassRecordAdapter.setFootVisible(false);
                            for (ClassRecord classRecord : classRecordBean.getResult()) {
                                classRecordList.add(classRecord);
                            }
                        } else {
                            rvClassRecordAdapter.setAllEnd(true);
                        }
                    } else {
                        classRecordList = classRecordBean.getResult();
                    }
                    mHandler.sendEmptyMessage(Operator_Success);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
