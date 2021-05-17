package com.telit.zhkt_three.Activity.ClassRecord;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.ClassRecord.NewRVClassRecordAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.ClassRecord.ActualOrderClassRecord;
import com.telit.zhkt_three.JavaBean.ClassRecord.ClassRecordTwo;
import com.telit.zhkt_three.JavaBean.ClassRecord.OrderByDateClassRecord;
import com.telit.zhkt_three.JavaBean.Gson.ClassRecordTwoBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

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
 * 三种：截屏分享(1)、投票(2)、分组讨论(3)
 * <p>
 * mData 数据时按照时间分好的一组组的
 * actualData 数据时一行行的，每行四个数据
 */
public class NewClassRecordActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.class_record_head_layout)
    CustomHeadLayout class_record_head_layout;
    @BindView(R.id.class_record_refresh)
    SwipeRefreshLayout class_record_refresh;
    @BindView(R.id.class_record_recycler)
    RecyclerView class_record_recycler;

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

    private NewRVClassRecordAdapter newRVClassRecordAdapter;

    //处理的mDatas
    private List<OrderByDateClassRecord> mData;

    private List<ActualOrderClassRecord> actualData;

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
                        QZXTools.popToast(NewClassRecordActivity.this, getResources().getString(R.string.current_net_err), false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        if (leak_resource_layout != null || request_retry_layout != null) {
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }

                        if (class_record_refresh != null && class_record_refresh.isRefreshing())
                            class_record_refresh.setRefreshing(false);

                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(NewClassRecordActivity.this, "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        if (leak_resource_layout != null || request_retry_layout != null) {
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }

                        if (class_record_refresh != null && class_record_refresh.isRefreshing())
                            class_record_refresh.setRefreshing(false);


                    }


                    break;
                case Operator_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (class_record_refresh.isRefreshing())
                            class_record_refresh.setRefreshing(false);

                        QZXTools.logE("mActualData Size=" + actualData.size(), null);

                        newRVClassRecordAdapter.notifyDataSetChanged();
                    }

                    break;
            }
        }
    };
private static final String TAG="tiantian008";
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class_record);
        isShow=true;
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        //设置头像信息等
        class_record_head_layout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());
        request_retry.setOnClickListener(this);
        link_network.setOnClickListener(this);

        mData = new ArrayList<>();
        actualData = new ArrayList<>();
        newRVClassRecordAdapter = new NewRVClassRecordAdapter(this, actualData);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        class_record_recycler.setLayoutManager(linearLayoutManager);
        class_record_recycler.setAdapter(newRVClassRecordAdapter);

        //刷新
        class_record_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDatas(true, true);
            }
        });

        //RV加载更多
        class_record_recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newRVClassRecordAdapter.isAllEnd()) {
                    return;
                }

                if (linearLayoutManager.findFirstVisibleItemPosition() == 0) {

                } else {
                    //一般情况下在可见位置与actualData总数相差小于PageSize，主动加载更多
                    if (newState == RecyclerView.SCROLL_STATE_IDLE &&
                            linearLayoutManager.findLastVisibleItemPosition() >= actualData.size() - PageSize) {
                        QZXTools.logE("more 要加载更多... curPageNo=" + curPageNo, null);
                        requestDatas(false, false);
                        return;
                    }
                }

                //第二种方式
                //得到当前显示的最后一个item的view
                View lastChildView = recyclerView.getLayoutManager().getChildAt(recyclerView.getLayoutManager()
                        .getChildCount() - 1);
                //得到lastChildView的bottom坐标值
                int lastChildBottom = lastChildView.getBottom();
                //得到Recycler view的底部坐标减去底部padding值，也就是显示内容最底部的坐标
                int recyclerBottom = recyclerView.getBottom() - recyclerView.getPaddingBottom();
                //通过这个lastChildView得到这个view当前的position值
                int lastPosition = recyclerView.getLayoutManager().getPosition(lastChildView);

                //判断lastChildView的bottom值跟recyclerBottom
                //判断lastPosition是不是最后一个position
                //如果两个条件都满足则说明是真正的滑动到了底部
                if (lastChildBottom == recyclerBottom && lastPosition ==
                        recyclerView.getLayoutManager().getItemCount() - 1
                        && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    QZXTools.logE("bottom 要加载更多... curPageNo=" + curPageNo, null);
                    requestDatas(false, false);
                }

            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                QZXTools.logE("onScrolled: "+dy,null);
            }
        });
        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        requestDatas(false, true);

    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        isShow=false;

        BuriedPointUtils.buriedPoint("2030","","","","");

        super.onDestroy();
    }

    /**
     * 当前分页的页数，初始化是1
     */
    private int curPageNo = 1;
    private static final int PageSize = 30;

    //分页的总数
    private int totalSize;

    private void requestDatas(boolean isRefresh, boolean isInit) {
        //是否存在网络
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (isInit) {
            if (mData != null && mData.size() > 0) {
                //刷新重置日期处理类
                curDateString = null;
                orderByDateClassRecord = null;
                classRecordTwos = null;

                curPageNo = 1;
                mData.clear();

                //刷新隐藏Foot
                newRVClassRecordAdapter.setFootVisible(false);

                actualData.clear();
                newRVClassRecordAdapter.notifyDataSetChanged();
            }
        } else {
            curPageNo++;
        }

        if (isRefresh) {
            class_record_refresh.setRefreshing(true);
        } else {

        }

        String url = UrlUtils.BaseUrl + UrlUtils.ClassRecordTwo;
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("studentId", UserUtils.getUserId());
        paraMap.put("pageNo", curPageNo + "");
        //一次加载十个数据
        paraMap.put("pageSize", PageSize + "");
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

                    try {
                        String result = response.body().string();
                        QZXTools.logE("record class result=" + result, null);
                        Gson gson = new Gson();
                        ClassRecordTwoBean classRecordTwoBean = gson.fromJson(result, ClassRecordTwoBean.class);
                        totalSize = classRecordTwoBean.getTotal();
                        List<ClassRecordTwo> classRecordTwos = classRecordTwoBean.getResult();

                        QZXTools.logE("load classRecordTwos=" + classRecordTwos.size(), null);

                        if (!isInit) {
                            newRVClassRecordAdapter.setFootVisible(true);
                            //刷新如果没有数据说明上一次就取完数据了
                            if (classRecordTwos.size() > 0) {
                                newRVClassRecordAdapter.setAllEnd(false);
                            } else {
                                newRVClassRecordAdapter.setAllEnd(true);
                            }
                        } else {
                            newRVClassRecordAdapter.setAllEnd(false);
                            newRVClassRecordAdapter.setFootVisible(false);
                        }

                        if (classRecordTwos.size() > 0) {
                            //日期转化处理
                            handlerDateInfo(classRecordTwos);

                            //处理实际的数据
                            handlerActualData();
                        }

                        mHandler.sendEmptyMessage(Operator_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();
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

    private OrderByDateClassRecord orderByDateClassRecord;
    private List<ClassRecordTwo> classRecordTwos;

    private static final int DefaultRowCount = 4;

    /**
     * 整合处理日期数据
     * <p>
     * 注意：一次加载十个,默认,我这里设置一次性加载20个
     */
    private void handlerDateInfo(List<ClassRecordTwo> originalBean) {
        //计数
        int count = 0;

        boolean isStartEnter = true;
        for (ClassRecordTwo classRecordTwo : originalBean) {
            //因为给的日期是例如： 2019-12-25 20:25，所以分割取年月日即可
            String[] emptyStrs = classRecordTwo.getCreateDate().split(" ");
            if (TextUtils.isEmpty(curDateString)) {
                isStartEnter = false;
                curDateString = emptyStrs[0];
                orderByDateClassRecord = new OrderByDateClassRecord();
                orderByDateClassRecord.setSameDate(curDateString);
                classRecordTwos = new ArrayList<>();
                classRecordTwos.add(classRecordTwo);
                count++;
                //刚好只有条
                if (originalBean.size() == count) {
                    //notes 因为仅仅一条，所以一定属于OnlyOneRow
                    orderByDateClassRecord.setClassRecordTwos(classRecordTwos);
                    mData.add(orderByDateClassRecord);
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
                    classRecordTwos.add(classRecordTwo);
                    count++;
                    //结尾数据都是同一天的哦
                    if (count == PageSize) {
                        orderByDateClassRecord.setClassRecordTwos(classRecordTwos);
                        mData.add(orderByDateClassRecord);
                    } else if (originalBean.size() <= PageSize && count == originalBean.size()) {
                        orderByDateClassRecord.setClassRecordTwos(classRecordTwos);
                        mData.add(orderByDateClassRecord);
                    }
                } else {
                    //刚好第一次进入日期就不同，如果不是第一次进入这个模块的话，把上一次的结果添加给集合
                    if (!isStartEnter) {
                        orderByDateClassRecord.setClassRecordTwos(classRecordTwos);
                        mData.add(orderByDateClassRecord);
                    }

                    isStartEnter = false;

                    curDateString = emptyStrs[0];

                    //重置,新增
                    classRecordTwos = null;
                    orderByDateClassRecord = null;
                    orderByDateClassRecord = new OrderByDateClassRecord();
                    orderByDateClassRecord.setSameDate(curDateString);
                    classRecordTwos = new ArrayList<>();
                    classRecordTwos.add(classRecordTwo);

                    count++;
                    //刚好这一次的List还剩最后一个
                    if (count == PageSize) {
                        orderByDateClassRecord.setClassRecordTwos(classRecordTwos);
                        mData.add(orderByDateClassRecord);
                    } else if (originalBean.size() <= PageSize && count == originalBean.size()) {
                        //刚好count等于size
                        orderByDateClassRecord.setClassRecordTwos(classRecordTwos);
                        mData.add(orderByDateClassRecord);
                    }
                }
            }
        }
    }

    /**
     * 处理实际的数据，四个作为一行
     */
    private void handlerActualData() {
        actualData.clear();

        if (mData != null && mData.size() > 0) {
            for (int i = 0; i < mData.size(); i++) {
                String sameDate = mData.get(i).getSameDate();
                List<ClassRecordTwo> classRecordTwos = mData.get(i).getClassRecordTwos();

                QZXTools.logE("mData size=" + mData.get(i).getClassRecordTwos().size()
                        + "sameDate=" + sameDate, null);

                ActualOrderClassRecord actualOrderClassRecord = null;
                int multiFactor = 0;//4的倍数
                for (int j = 0; j < classRecordTwos.size(); j++) {
                    //4 的倍数 才创建一个真实的ActualOrderClassRecord对象
                    if (j == DefaultRowCount * multiFactor) {
                        //添加上一波数据
                        if (actualOrderClassRecord != null) {
                            actualData.add(actualOrderClassRecord);
                        }
                        actualOrderClassRecord = new ActualOrderClassRecord();
                        actualOrderClassRecord.setSameDate(sameDate);
                        //设置是否是head,只有一个
                        if (i == 0) {
                            actualOrderClassRecord.setFirst(true);
                        } else {
                            actualOrderClassRecord.setFirst(false);
                        }

                        //设置是否是foot,只有一个
                        if (i == (mData.size() - 1) && j == (classRecordTwos.size() - 1)) {
                            actualOrderClassRecord.setLast(true);
                        } else {
                            actualOrderClassRecord.setLast(false);
                        }

                        multiFactor++;
                    }

                    if (j < DefaultRowCount * multiFactor) {
                        //设置类型
                        if (classRecordTwos.size() <= DefaultRowCount) {
                            actualOrderClassRecord.setType(Constant.Only_One_Row);
                        } else {
                            if (j >= 0 && j < DefaultRowCount) {
                                actualOrderClassRecord.setType(Constant.Head_ClassRecord);
                            } else if (j < classRecordTwos.size() && j >= classRecordTwos.size() - DefaultRowCount) {
                                actualOrderClassRecord.setType(Constant.Foot_ClassRecord);
                            } else {
                                actualOrderClassRecord.setType(Constant.Mid_ClassRecord);
                            }
                        }

                        //赋值内容
                        if (j % DefaultRowCount == 0) {
                            actualOrderClassRecord.setOne(classRecordTwos.get(j));
                        } else if (j % DefaultRowCount == 1) {
                            actualOrderClassRecord.setTwo(classRecordTwos.get(j));
                        } else if (j % DefaultRowCount == 2) {
                            actualOrderClassRecord.setThree(classRecordTwos.get(j));
                        } else if (j % DefaultRowCount == 3) {
                            actualOrderClassRecord.setFour(classRecordTwos.get(j));
                        }
                    }

                    //添加最后的一波数据
                    if (j == (classRecordTwos.size() - 1)) {
                        actualData.add(actualOrderClassRecord);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_retry:
                requestDatas(false, true);
                break;
            case R.id.link_network:
                QZXTools.enterWifiSetting(this);
                break;
        }
    }
}
