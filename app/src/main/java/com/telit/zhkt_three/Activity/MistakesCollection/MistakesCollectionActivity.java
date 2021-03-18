package com.telit.zhkt_three.Activity.MistakesCollection;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionTvAnswerAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.Gson.MistakesBean;
import com.telit.zhkt_three.JavaBean.Gson.SubjectiveListBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.MistakesCollection.SubjectBean;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.zbv.docxmodel.DocBean;
import com.zbv.docxmodel.DocxCallback;
import com.zbv.docxmodel.DocxUtils;
import com.zbv.docxmodel.OptionBean;

import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
 * 错题集默认题库出题，所以要判断是否存在图片出题
 * <p>
 * 关于导出功能：每次只能导出加载的数据，如果下拉还需要导入下拉的数据集，还会存在docx文档合并问题（待优化）
 * todo 合并多docx文件
 * todo 在线预览docx
 * todo 优化docx显示样式，譬如设置字体颜色【需要高版本POI】
 */
public class MistakesCollectionActivity extends BaseActivity implements View.OnClickListener, ToUsePullView.SpinnerClickInterface {
    private Unbinder unbinder;
    @BindView(R.id.mistakes_head_layout)
    CustomHeadLayout customHeadLayout;
    @BindView(R.id.mistakes_pull_all)
    LinearLayout mistakes_pull_all;
    @BindView(R.id.mistakes_pull_layout)
    LinearLayout pull_layout;
    @BindView(R.id.mistakes_pull_subject)
    ToUsePullView pull_subject;
    @BindView(R.id.mistakes_pull_type)
    ToUsePullView pull_type;
    @BindView(R.id.mistakes_pull_difficulty)
    ToUsePullView pull_difficulty;
    @BindView(R.id.mistakes_pull_date)
    ToUsePullView pull_date;
    @BindView(R.id.mistakes_pull_tag)
    FrameLayout pull_tag;
    @BindView(R.id.mistakes_pull_icon)
    ImageView pull_icon;
    @BindView(R.id.mistakes_custom_date_layout)
    LinearLayout mistakes_custom_date_layout;
    @BindView(R.id.mistakes_start_tv)
    TextView mistakes_start_tv;
    @BindView(R.id.mistakes_end_tv)
    TextView mistakes_end_tv;
    @BindView(R.id.mistakes_xrecycler)
    XRecyclerView xRecyclerView;

    //-----------无网络或者无资源
    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;

    private Map<String, String> subjectMap;
    //题型
    private Map<String, String> typeMap;
    //难易度
    private Map<String, String> difficultyMap;
    //时间段
    private List<String> dateTime;

    //当前页码
    private int curPageNo = 1;
    //传递查询的开始时间
    private String startDate;
    //传递查询的结束时间
    private String endDate;

    private RVQuestionTvAnswerAdapter rvQuestionTvAnswerAdapter;

    /**
     * 错题集详情主体
     */
    private List<QuestionInfo> questionInfoList;

    private boolean isShown = false;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operate_Subject_Query_Success = 2;
    private static final int Operate_Query_Success = 3;
    private static final int Operate_Delay_Date_Query = 4;

    private static boolean isShow=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(MistakesCollectionActivity.this, "服务端错误！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(MistakesCollectionActivity.this, "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }

                    break;
                case Operate_Subject_Query_Success:
                    if (isShow){
                        List<String> subjectiveList = new ArrayList<String>(subjectMap.keySet());
                        pull_subject.setDataList(subjectiveList);
                        pull_subject.setPullContent(subjectiveList.get(0));

                        //当主题获取后设置巩固的参数，否则主题Subject是Null,这里xd为1小学，因为没有传递，而且目前只有小学
                        rvQuestionTvAnswerAdapter.fetchNeedParam("1", subjectMap.get(pull_subject.getPullContent()),
                                difficultyMap.get(pull_difficulty.getPullContent()), typeMap.get(pull_type.getPullContent()));

                        //请求错题详情
                        requestMistakesDetails(false);
                    }


                    break;
                case Operate_Query_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        //如果没有数据就显示无数据提示画面
                        if (questionInfoList.size() > 0) {
                            leak_resource.setVisibility(View.GONE);

                            //xRecyclerView的重置上拉刷新和下拉加载界面
                            if (xRecyclerView != null) {

                                xRecyclerView.refreshComplete();
                                xRecyclerView.loadMoreComplete();

                                if (msg.arg1 == 1) {
                                    xRecyclerView.setNoMore(false);
                                } else {
                                    xRecyclerView.setNoMore(true);
                                }
                            }

                        } else {
                            if (xRecyclerView != null) {
                                xRecyclerView.refreshComplete();
                                xRecyclerView.loadMoreComplete();
                            }

                            leak_resource.setVisibility(View.VISIBLE);
                        }
                        rvQuestionTvAnswerAdapter.notifyDataSetChanged();
                    }

                    break;
                case Operate_Delay_Date_Query:
                    if (isShow){
                        requestMistakesDetails(false);
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
        setContentView(R.layout.activity_misstakes_collection);
        isShow=true;
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        //设置头像信息等
        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                .where(StudentInfoDao.Properties.StudentId.eq(UserUtils.getStudentId())).unique();
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
            customHeadLayout.setHeadInfo(studentInfo.getPhoto(), studentInfo.getStudentName(), clazz);
        }

        pull_tag.setOnClickListener(this);
        mistakes_custom_date_layout.setOnClickListener(this);

        pull_subject.setSpinnerClick(this);
        pull_type.setSpinnerClick(this);
        pull_difficulty.setSpinnerClick(this);
        pull_date.setSpinnerClick(this);

        initData();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        //release xRecyclerView
        if (xRecyclerView != null) {
            xRecyclerView.destroy();
            xRecyclerView = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        isShow=false;
        super.onDestroy();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void generatePdf(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PdfDocument document = new PdfDocument();
                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder
                        (xRecyclerView.getWidth(), xRecyclerView.getHeight(), 1).create();
                PdfDocument.Page page = document.startPage(pageInfo);
                xRecyclerView.draw(page.getCanvas());
                document.finishPage(page);
                try {
                    FileOutputStream fos = new FileOutputStream(new File(
                            QZXTools.createSDCardDirectory("mypdf", "try.pdf")));
                    document.writeTo(fos);
                } catch (IOException e) {
                    e.printStackTrace();
                    QZXTools.logE("pdf happen exception", e);
                } finally {
                    document.close();
                    QZXTools.logE("write pdf over", null);
                }
            }
        }).start();
    }

    /**
     * 是否正在导出Docx
     */
    private boolean isGenerating = false;

    /**
     * 导出Docx
     */
    public void generateDocx(View view) {
        if (isGenerating) {
            return;
        }

        if (questionInfoList == null || questionInfoList.size() <= 0) {
            return;
        }

        Gson gson = new Gson();

        List<DocBean> docBeans = new ArrayList<>();

        //封装成导出类
        for (int i = 0; i < questionInfoList.size(); i++) {
            //源数据
            QuestionInfo questionInfo = questionInfoList.get(i);

            int qType = questionInfo.getQuestionType();

            DocBean docBean = new DocBean();
            docBean.setqIndex(i + 1);
            docBean.setqType(qType);
            docBean.setqTitle(questionInfo.getQuestionContent());

            if (!TextUtils.isEmpty(questionInfo.getScore())) {
                docBean.setqTotalScore(Integer.parseInt(questionInfo.getScore()));
            }
            if (TextUtils.isEmpty(questionInfo.getOwnscore())) {
                docBean.setqMyScore(Double.parseDouble(questionInfo.getOwnscore()));
            }
            docBean.setqRightAnswer(questionInfo.getAnswer());

            String answerOptions = questionInfo.getAnswerOption();

            List<OptionBean> optionBeans = new ArrayList<>();

            if (TextUtils.isEmpty(answerOptions)) {
                //填空题没有
                List<WorkOwnResult> workOwnResults = questionInfo.getOwnList();
                for (int j = 0; j < workOwnResults.size(); j++) {
                    OptionBean optionBean = new OptionBean();
                    optionBean.setIndex((j + 1) + "");
                    optionBean.setOptionContent(workOwnResults.get(j).getAnswerContent());
                    optionBeans.add(optionBean);
                }
            } else {
                // 选项
                switch (qType) {
                    case DocxUtils.Judge_Item:
                    case DocxUtils.Multi_Choose:
                    case DocxUtils.Fill_Blank:
                    case DocxUtils.Single_Choose:
                        Map<String, String> optionMap = gson.fromJson(answerOptions, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();
                            OptionBean optionBean = new OptionBean();
                            optionBean.setIndex(entry.getKey());
                            optionBean.setOptionContent(entry.getValue());

                            optionBeans.add(optionBean);
                        }
                        break;
                }
            }
            docBean.setOptionBeans(optionBeans);
            docBeans.add(docBean);
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("poi_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".doc");

        String targetDocPath = getExternalFilesDir("poi").getPath() +
                File.separator + stringBuilder.toString();//这个目录，不需要申请存储权限

        DocxUtils.createXWPF(docBeans, targetDocPath, new DocxCallback() {
            @Override
            public void isDocxing(boolean docx) {
                isGenerating = docx;
            }

            @Override
            public void onCompleted() {
                QZXTools.popToast(MistakesCollectionActivity.this,
                        "导出成功！文件位于：" + targetDocPath, false);

            }
        });
    }

    /**
     * 初始化数据
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initData() {
        subjectMap = new LinkedHashMap<>();

        typeMap = new LinkedHashMap<>();
        typeMap.put("全部", "");
        typeMap.put("单选题", "0");
        typeMap.put("多选题", "1");
        typeMap.put("填空题", "2");
        typeMap.put("主观题", "3");
        typeMap.put("判断题", "5");
        List<String> typeList = new ArrayList<String>(typeMap.keySet());
        pull_type.setDataList(typeList);
        pull_type.setPullContent(typeList.get(0));

        difficultyMap = new LinkedHashMap<>();
        difficultyMap.put("简单", "1");
        difficultyMap.put("较易", "2");
        difficultyMap.put("普通", "3");
        difficultyMap.put("较难", "4");
        difficultyMap.put("困难", "5");
        List<String> difficulty = new ArrayList<String>(difficultyMap.keySet());
        pull_difficulty.setDataList(difficulty);
        pull_difficulty.setPullContent(difficulty.get(2));

        dateTime = new ArrayList<>();
        dateTime.add("今天");
        dateTime.add("一周");
        dateTime.add("一月");
        dateTime.add("自定义");
        pull_date.setDataList(dateTime);
        pull_date.setPullContent(dateTime.get(0));
        //默认选中今天
        calculateDateSection(pull_date.getPullContent());

        mistakes_start_tv.setText(startDate);
        mistakes_end_tv.setText(endDate);

        //初始化错题集RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);

        //使用带时间的刷新头
        xRecyclerView
                .getDefaultRefreshHeaderView()
                .setRefreshTimeVisible(true);

        //设置没有更多数据的显示
        xRecyclerView.getDefaultFootView().setNoMoreHint("没有更多数据了");

        // When the item number of the screen number is list.size-2,we call the onLoadMore
//        xRecyclerView.setLimitNumberToCallLoadMore(2);

        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                curPageNo = 1;
                requestMistakesDetails(false);
            }

            @Override
            public void onLoadMore() {
                curPageNo++;
                requestMistakesDetails(true);
            }
        });

        rvQuestionTvAnswerAdapter = new RVQuestionTvAnswerAdapter(this, "2",
                false, true, 1, "");
        questionInfoList = new ArrayList<>();
        rvQuestionTvAnswerAdapter.setQuestionInfoList(questionInfoList, "", null);
        xRecyclerView.setAdapter(rvQuestionTvAnswerAdapter);

        fetchNetSubjectData();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchNetSubjectData() {

        String url = UrlUtils.BaseUrl + UrlUtils.MistakesSubjectList;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("studentid", UserUtils.getStudentId());

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
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
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        SubjectiveListBean subjectiveListBean = gson.fromJson(resultJson, SubjectiveListBean.class);
                        for (SubjectBean subjectBean : subjectiveListBean.getResult()) {
                            subjectMap.put(subjectBean.getName(), subjectBean.getId());
                        }
                        mHandler.sendEmptyMessage(Operate_Subject_Query_Success);
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

    /**
     * 获取错题详情
     * <p>
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "操作成功",
     * "result": [{
     * "id": "ddce8d8d55164dd3a24e8d43b3d01623",
     * "homeworkId": "402649872f32466cbb5096edb3e2b85f",
     * "questionContent": null,
     * "image": null,
     * "imageopted": null,
     * "list": [{
     * "id": "edb337dfccae4dceb937faa1356cd35e",
     * "content": null,
     * "index": 1,
     * "options": "A"
     * },
     * {
     * "id": "7d3dc913930f4e80adfa8fe3fedb7a26",
     * "content": null,
     * "index": 2,
     * "options": "B"
     * },
     * {
     * "id": "a428af2243004fe99045483fb866ac99",
     * "content": null,
     * "index": 3,
     * "options": "C"
     * },
     * {
     * "id": "c2f059930bd3451c94d38bab626931e2",
     * "content": null,
     * "index": 4,
     * "options": "D"
     * }
     * ],
     * "attachment": null,
     * "knowledge": "[{'tid': '4602', 'name': '基础知识'}]",
     * "leftList": [],
     * "rightList": [],
     * "ownList": [
     * "B"
     * ],
     * "answer": "B",
     * "analysis": null,
     * "imgFile": [],
     * "voiceFile": []
     * }
     * ],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 34262218745142@fllxx 用这个登录
     */
    private void requestMistakesDetails(boolean isLoadingMore) {
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        //如果不是加载更多的话就清空集合
        if (!isLoadingMore) {
            questionInfoList.clear();
            rvQuestionTvAnswerAdapter.notifyDataSetChanged();
        }

        String url = UrlUtils.BaseUrl + UrlUtils.MistakesDetails;

        Map<String, String> paraMap = new LinkedHashMap<>();
        //因为作业改成使用用户ID，所以错题集这里获取数据也改成使用用户ID,以前是StudentID
        paraMap.put("studentid", UserUtils.getUserId());

        //如果学科为‘全部’则不传该字段
        if (!TextUtils.isEmpty(subjectMap.get(pull_subject.getPullContent()))) {

            paraMap.put("subjectid", subjectMap.get(pull_subject.getPullContent()));
        }
        //如果题型为‘全部’则不传该字段
        if (!TextUtils.isEmpty(typeMap.get(pull_type.getPullContent()))) {
            paraMap.put("questionType", typeMap.get(pull_type.getPullContent()));
        }
        paraMap.put("difficultIndex", difficultyMap.get(pull_difficulty.getPullContent()));
        // 例如 2019-05-12 00:00:00
        paraMap.put("startTime", startDate);
        paraMap.put("endTime", endDate);
//        paraMap.put("pageSize","30");
        paraMap.put("pageNo", curPageNo + "");

        // 9c5e8fc2cc6e5a197b4ea823497f3da7e11c63b33fa2c7c3b1d76c42aa45b88c746fcb62bd7ab0fb00131e0cb389bea9a6b7c3b97860a87a
        QZXTools.logE("studentid=" + UserUtils.getStudentId() + ";subjectid=" + subjectMap.get(pull_subject.getPullContent())
                + ";questionType=" + typeMap.get(pull_type.getPullContent())
                + ";difficultIndex" + difficultyMap.get(pull_difficulty.getPullContent())
                + ";startTime=" + startDate + ";endTime=" + endDate + ";pageNo=" + curPageNo, null);

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
                    MistakesBean mistakesBean = gson.fromJson(resultJson, MistakesBean.class);
                    /**
                     * 1表示有数据，0表示无数据
                     * */
                    int hadNoData = 0;

                    if (mistakesBean != null && mistakesBean.getResult() != null && mistakesBean.getResult().size() > 0) {
                        for (QuestionInfo questionInfo : mistakesBean.getResult()) {
                            questionInfoList.add(questionInfo);
                        }
                        hadNoData = 1;
                    }
                    Message message = mHandler.obtainMessage();
                    message.what = Operate_Query_Success;
                    message.arg1 = hadNoData;
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
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

    private int preValue;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mistakes_custom_date_layout:
                popupCustomDate();
                break;
            case R.id.mistakes_pull_tag:
                preValue = 0;
                if (isShown) {
                    isShown = false;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pull_layout.getMeasuredHeight());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mistakes_pull_all.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.topMargin -= offset;
                            mistakes_pull_all.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.setDuration(500);
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(pull_icon, 180.0f, 0.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            pull_layout.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.start();
                } else {
                    //可见
                    pull_layout.setVisibility(View.VISIBLE);
                    isShown = true;
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, pull_layout.getMeasuredHeight());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mistakes_pull_all.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.topMargin += offset;
                            mistakes_pull_all.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(pull_icon, 0f, 180.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {


                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    valueAnimator.start();
                }
                break;
        }
    }

    /**
     * 图标的旋转180度
     */
    private void iconRotate(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
    }

    @Override
    public void spinnerClick(View parent, String text) {
        switch (parent.getId()) {
            case R.id.mistakes_pull_subject:
                curPageNo = 1;
                pull_subject.setPullContent(text);
                break;
            case R.id.mistakes_pull_type:
                curPageNo = 1;
                pull_type.setPullContent(text);
                break;
            case R.id.mistakes_pull_difficulty:
                curPageNo = 1;
                pull_difficulty.setPullContent(text);
                break;
            case R.id.mistakes_pull_date:
                curPageNo = 1;
                pull_date.setPullContent(text);
                if (text.equals("自定义")) {
                    mistakes_custom_date_layout.setVisibility(View.VISIBLE);
                    popupCustomDate();
                    return;
                } else {
                    calculateDateSection(text);
                    mistakes_custom_date_layout.setVisibility(View.INVISIBLE);
                }
                break;
        }

        //更新参数
        rvQuestionTvAnswerAdapter.fetchNeedParam("1", subjectMap.get(pull_subject.getPullContent()),
                difficultyMap.get(pull_difficulty.getPullContent()), typeMap.get(pull_type.getPullContent()));

        requestMistakesDetails(false);
        //错题集点击学科埋点
        BuriedPointUtils.buriedPoint("2020","","",text,"");
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
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                if (dates.size() > 0) {
                    String start = FORMATTER.format(dates.get(0).getDate());
                    String end = FORMATTER.format(dates.get(dates.size() - 1).getDate());

                    decorator.addFirstAndLast(dates.get(0), dates.get(dates.size() - 1));
                    materialCalendarView.invalidateDecorators();

                    startDate = start.concat(" 00:00:00");
                    endDate = end.concat(" 00:00:00");

                    mistakes_start_tv.setText(startDate);
                    mistakes_end_tv.setText(endDate);

                    mHandler.sendEmptyMessageDelayed(Operate_Delay_Date_Query, 1000);

                    QZXTools.popCommonToast(MistakesCollectionActivity.this,
                            "firstDate=" + start + ";secondDate=" + end, false);
                }
            }
        });

        datePopup.setBackgroundDrawable(new ColorDrawable());
        datePopup.setOutsideTouchable(true);

        //popup只有具体的尺寸，底部空间不够才会在上面显示
        datePopup.showAsDropDown(mistakes_custom_date_layout, 0, 0);
    }
}
