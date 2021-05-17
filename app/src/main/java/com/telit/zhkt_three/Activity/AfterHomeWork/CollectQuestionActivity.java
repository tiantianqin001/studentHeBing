package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.XToast;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.RangeDayDecorator;
import com.telit.zhkt_three.Adapter.AfterHomeWork.CollectQuestionAdapter;
import com.telit.zhkt_three.Adapter.AfterHomeWork.HomeworkQuestionExportAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.EmojiEditText;
import com.telit.zhkt_three.CustomView.NoScrollRecyclerView;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.Gson.CollectQuestionByHandBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.AppInfoUtils;
import com.telit.zhkt_three.Utils.FormatUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.TimeUtils;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.tencent.bugly.crashreport.CrashReport;
import com.zbv.meeting.util.SharedPreferenceUtil;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/2/1 8:41
 * name;
 * overview:
 * usage: 收藏题目
 * ******************************************************************
 */
public class CollectQuestionActivity extends BaseActivity implements ToUsePullView.SpinnerClickInterface {
    private Unbinder unbinder;

    @BindView(R.id.collect_question_recycler)
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

    private CollectQuestionAdapter collectQuestionAdapter;

    private List<QuestionBank> questionBanks;

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Operate_Delay_Date_Query = 4;

    private ToUsePullView pull_date;
    private String startDate,endDate;

    private RelativeLayout rl_time;
    private TextView start_tv,end_tv;

    private static boolean isShow=false;
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(CollectQuestionActivity.this, "当前网络不佳....", false);
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
                        QZXTools.popToast(CollectQuestionActivity.this, "没有相关资源！", false);
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

                        if (questionBanks.size() > 0) {
                            leak_resource.setVisibility(View.GONE);
                        } else {
                            leak_resource.setVisibility(View.VISIBLE);
                        }

                        collectQuestionAdapter.notifyDataSetChanged();
                    }

                    break;
                case Operate_Delay_Date_Query:
                    if (isShow){
                        requestNetDatas();
                        datePopup.dismiss();
                    }
                    break;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_question);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();

        unbinder = ButterKnife.bind(this);
        isShow=true;

        EventBus.getDefault().register(this);

        rl_time = findViewById(R.id.rl_time);
        start_tv = findViewById(R.id.start_tv);
        end_tv = findViewById(R.id.end_tv);

        //连接网络
        link_network.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(CollectQuestionActivity.this);
            }
        });

        questionBanks = new ArrayList<>();
        collectQuestionAdapter = new CollectQuestionAdapter(this, questionBanks);

        xRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        xRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, getResources().getDimensionPixelSize(R.dimen.x5), 0, getResources().getDimensionPixelSize(R.dimen.x5));
            }
        });

        xRecyclerView.setAdapter(collectQuestionAdapter);


        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRefresh() {
                questionBanks.clear();
                collectQuestionAdapter.notifyDataSetChanged();
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
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        pull_date =findViewById(R.id.pull_date);
        pull_date.setSpinnerClick(this);
        initDate();

        requestNetDatas();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
    }

    private static final int PageSize = 10;

    /**
     * 请求网络数据
     */
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        String url = UrlUtils.BaseUrl + UrlUtils.CollectQuestionList;
        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("studentId", UserUtils.getUserId());
        mapParams.put("pageNo", curPageNo + "");
        mapParams.put("pageSize", PageSize + "");
        mapParams.put("startTime", startDate + "");
        mapParams.put("endTime", endDate + "");

        QZXTools.logE("Params:"+new Gson().toJson(mapParams), null);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (call.isCanceled()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QZXTools.popToast(CollectQuestionActivity.this, "取消了请求", false);
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
                        CollectQuestionByHandBean collectQuestionByHandBean = gson.fromJson(resultJson, CollectQuestionByHandBean.class);

                        handlerDateInfo(collectQuestionByHandBean.getResult());

                        mHandler.sendEmptyMessage(Operator_Success);
                    }catch (Exception e){
                        e.fillInStackTrace();

                        QZXTools.logE("Exception", e);

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


    /**
     * 整合处理日期数据
     * <p>
     * 注意：一次加载十个
     */
    private void handlerDateInfo(List<QuestionBank> collectQuestionBeans) {
        questionBanks.addAll(collectQuestionBeans);
    }

    @OnClick(R.id.collect_question_back)
    void collectQuestionBack(){
        finish();
    }

    //收藏或取消成功
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscriber(tag = Constant.Question_Collect_Success, mode = ThreadMode.MAIN)
    public void collectQuestion(String teacher_end) {
        QZXTools.logE("收藏或取消成功", null);
        questionBanks.clear();
        collectQuestionAdapter.notifyDataSetChanged();
        curPageNo = 1;
        requestNetDatas();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void spinnerClick(View parent, String text) {
        curPageNo = 1;
        pull_date.setPullContent(text);
        if (text.equals("自定义")) {
            rl_time.setVisibility(View.VISIBLE);
            popupCustomDate();
        } else {
            questionBanks.clear();
            collectQuestionAdapter.notifyDataSetChanged();

            rl_time.setVisibility(View.GONE);
            calculateDateSection(text);
            requestNetDatas();
        }
    }

    private PopupWindow datePopup;

    /**
     * 弹出自定义日期选择弹框
     */
    private void popupCustomDate() {
        if (datePopup != null) {
            datePopup.dismiss();
        }

        View menuView = LayoutInflater.from(this).inflate(R.layout.pop_mistakes_date_layout, null);
        datePopup = new PopupWindow(menuView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        MaterialCalendarView materialCalendarView = menuView.findViewById(R.id.mistakes_pop_calendarView);

        RangeDayDecorator decorator = new RangeDayDecorator(this);
        materialCalendarView.addDecorator(decorator);
        materialCalendarView.setSelectedDate(LocalDate.parse(startDate.split(" ")[0]));
        materialCalendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRangeSelected(@android.support.annotation.NonNull MaterialCalendarView widget, @android.support.annotation.NonNull List<CalendarDay> dates) {
                if (dates.size() > 0) {
                    String start = FORMATTER.format(dates.get(0).getDate());
                    String end = FORMATTER.format(dates.get(dates.size() - 1).getDate());

                    decorator.addFirstAndLast(dates.get(0), dates.get(dates.size() - 1));
                    materialCalendarView.invalidateDecorators();

                    startDate = start.concat(" 00:00:00");
                    endDate = end.concat(" 00:00:00");

                    start_tv.setText(startDate);
                    end_tv.setText(endDate);

                    questionBanks.clear();
                    collectQuestionAdapter.notifyDataSetChanged();

                    requestNetDatas();

                    mHandler.sendEmptyMessageDelayed(Operate_Delay_Date_Query, 1000);
                }
            }
        });

        datePopup.setBackgroundDrawable(new ColorDrawable());
        datePopup.setOutsideTouchable(true);

        //popup只有具体的尺寸，底部空间不够才会在上面显示
        datePopup.showAsDropDown(start_tv, 0, 0);
    }

    /**
     * 计算起止时间
     *
     * @param dateStr 今天、一周和一月
     */
    private void calculateDateSection(String dateStr) {
        //代表起止时间的时间差值 ms/毫秒
        long timeInterval;
        if (dateStr.equals("今天")) {
            timeInterval = 24 * 60 * 60 * 1000;
        } else if (dateStr.equals("一周")) {
            timeInterval = 7 * 24 * 60 * 60 * 1000;
        } else if (dateStr.equals("一月")) {
            // 2592000000 这个超过int所以必须加上L表示是Long类型，不然是负数
            timeInterval = 30 * 24 * 60 * 60 * 1000L;
        } else {
            return;
        }

        QZXTools.logE("timeInterval=" + timeInterval, null);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        QZXTools.logE("year=" + year + ";month=" + month + ";day=" + day, null);

        int maxDay = QZXTools.calculate(year, month);
        //当前天数加一
        if ((day + 1) > maxDay) {
            month++;
            if (month > 12) {
                month = 1;
                year++;
            }
            day = 1;
        } else {
            day = day + 1;
        }

        String strDay;
        String strMonth;
        if (day <= 9) {
            strDay = "0" + day;
        } else {
            strDay = day + "";
        }

        if (month <= 9) {
            strMonth = "0" + month;
        } else {
            strMonth = month + "";
        }

        //结束时间
        String tomorrowStr = year + "-" + strMonth + "-" + strDay + " " + "00:00:00";
        endDate = tomorrowStr;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(tomorrowStr);
            long endTime = date.getTime();

            long startTime = endTime - timeInterval;
            QZXTools.logE("startTime=" + startTime + ";endTime=" + endTime + ";timeInterval=" + timeInterval, null);

            //传入时间值转成Date然后格式化
            startDate = simpleDateFormat.format(new Date(startTime));

            QZXTools.logE("tomorrowStr=" + tomorrowStr + ";startDate=" + startDate + ";endDate=" + endDate, null);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    private List<String> dateTime;

    /**
     * 初始化数据
     */
    @android.support.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initDate() {
        dateTime = new ArrayList<>();
        dateTime.add("今天");
        dateTime.add("一周");
        dateTime.add("一月");
        dateTime.add("自定义");
        pull_date.setDataList(dateTime);
        pull_date.setPullContent(dateTime.get(1));

        //默认选中今天
        calculateDateSection(pull_date.getPullContent());

        start_tv.setText(startDate);
        end_tv.setText(endDate);
    }

    /**
     * 导出Docx
     */
    public void exportDocx(View view) {
        if (questionBanks==null||questionBanks.size()==0){
            QZXTools.popCommonToast(this,"暂无收藏",false);
            return;
        }

        if (!ViewUtils.isFastClick(1000)){
            return;
        }

        //设置为全不选
        for (QuestionBank questionBank:questionBanks){
            questionBank.setChecked(false);
        }

        showQuestionDialog(questionBanks);
    }

    private XToast toast;
    private boolean checkedAll;
    private HomeworkQuestionExportAdapter exportAdapter;
    private String flag;
    private EmojiEditText et_email;

    /**
     * 问题选择
     */
    private void showQuestionDialog(List<QuestionBank> list) {
        toast = new XToast(this)
                .setView(R.layout.toast_export_questions)
                .setOutsideTouchable(false)
                .setBackgroundDimAmount(0.5f)
                .setText(R.id.tv_name,"收藏题目导出")
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
                            QZXTools.popToast(CollectQuestionActivity.this, "请选择导出的题目", false);
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
                                QZXTools.popToast(CollectQuestionActivity.this, "邮箱不可为空", false);
                                return;
                            }

                            if (!FormatUtils.isEmail(et_email.getText().toString())){
                                QZXTools.popToast(CollectQuestionActivity.this, "邮箱格式不正确", false);
                                return;
                            }

                            iv_process.setImageResource(R.mipmap.send_finish);
                            rl_email.setVisibility(View.GONE);
                            iv_status.setVisibility(View.VISIBLE);
                            btn_send.setText("完成");

                            flag = "2";

                            //提交
                            sendEmail(list,et_email.getText().toString(),iv_status);
                        }else if ("2".equals(flag)){
                            iv_process.setImageResource(R.mipmap.question_export);

                            toast.cancel();
                            flag = null;
                        }
                    }
                })
                .show();

        NoScrollRecyclerView rv_questions = toast.getView().findViewById(R.id.rv_questions);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        rv_questions.setLayoutManager(manager);
        exportAdapter = new HomeworkQuestionExportAdapter(this,list);
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
        if (!TextUtils.isEmpty(SharedPreferenceUtil.getInstance(this).getString("exportEmail"))){
            et_email.setText(SharedPreferenceUtil.getInstance(this).getString("exportEmail"));
            et_email.setSelection(SharedPreferenceUtil.getInstance(this).getString("exportEmail").length());
        }
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
     * 发送邮件
     *
     * @param email
     * @param iv_status
     */
    private void sendEmail(List<QuestionBank> list,String email,ImageView iv_status){
        String url = UrlUtils.BaseUrl + UrlUtils.Homework_Export;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("homeworkids", getHomeworkIds(list));
        mapParams.put("questionids", getQuestionIds(list));
        mapParams.put("status", "1");
        mapParams.put("email", email);
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("title", "作业收藏"+ TimeUtils.timeStamp());
        mapParams.put("tip", AppInfoUtils.getAppName(this)+"导出作业收藏");

        QZXTools.logE("param:"+new Gson().toJson(mapParams),null);

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QZXTools.popToast(CollectQuestionActivity.this, "当前网络不佳....", false);
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

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if ("1".equals(errorCode)){
                                    iv_status.setImageResource(R.mipmap.email_send_success);
                                }else {
                                    iv_status.setImageResource(R.mipmap.email_send_fail);
                                }
                            }
                        });

                        SharedPreferenceUtil.getInstance(CollectQuestionActivity.this).setString("exportEmail",et_email.getText().toString());

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
     * 获取问题Id
     *
     * @param list
     * @return
     */
    private String getQuestionIds(List<QuestionBank> list){
        StringBuffer questionIds = new StringBuffer();
        for (int i=0;i<list.size();i++){
            if (list.get(i).isChecked()){
                questionIds.append(list.get(i).getQuestionId()+",");
            }
        }
        if (questionIds.toString().length()>0){
            return questionIds.toString().substring(0,questionIds.toString().length()-1);
        }else {
            return questionIds.toString();
        }
    }

    /**
     * 获取作业Id
     *
     * @param list
     * @return
     */
    private String getHomeworkIds(List<QuestionBank> list){
        StringBuffer questionIds = new StringBuffer();
        for (int i=0;i<list.size();i++){
            if (list.get(i).isChecked()){
                questionIds.append(list.get(i).getHomeworkId()+",");
            }
        }
        if (questionIds.toString().length()>0){
            return questionIds.toString().substring(0,questionIds.toString().length()-1);
        }else {
            return questionIds.toString();
        }
    }
}
