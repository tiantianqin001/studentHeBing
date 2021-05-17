package com.telit.zhkt_three.Activity.MistakesCollection;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.XToast;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.AfterHomeWork.MistakesCollectionExportAdapter;
import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionBankAnswerAdapter;
import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionTvAnswerAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.CustomView.EmojiEditText;
import com.telit.zhkt_three.CustomView.NoScrollRecyclerView;
import com.telit.zhkt_three.CustomView.ToUsePullView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.MistakesBean;
import com.telit.zhkt_three.JavaBean.Gson.SubjectiveListBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.MistakesCollection.SubjectBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.AppInfoUtils;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.FormatUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.TimeUtils;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.zbv.docxmodel.DocBean;
import com.zbv.docxmodel.DocxCallback;
import com.zbv.docxmodel.DocxUtils;
import com.zbv.docxmodel.OptionBean;
import com.zbv.meeting.util.SharedPreferenceUtil;

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
    @BindView(R.id.mistakes_pull_mode)
    ToUsePullView pull_mode;
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
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private Map<String, String> subjectMap;
    //题型
    private Map<String, String> typeMap;
    //难易度
    private Map<String, String> difficultyMap;
    //方式
    private Map<String, String> modeMap;
    //时间段
    private List<String> dateTime;

    //当前页码
    private int curPageNo = 1;
    //传递查询的开始时间
    private String startDate;
    //传递查询的结束时间
    private String endDate;

    private RVQuestionTvAnswerAdapter rvQuestionTvAnswerAdapter;
    //是不是图片出题
    boolean isImage=false;

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

                        QZXTools.popToast(MistakesCollectionActivity.this, "网络比较慢！", false);

                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(MistakesCollectionActivity.this, "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;

                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    break;
                case Operate_Subject_Query_Success:
                    if (isShow){
                        List<String> subjectiveList = new ArrayList<String>(subjectMap.keySet());
                        pull_subject.setDataList(subjectiveList);
                        pull_subject.setPullContent(subjectiveList.get(0));
                        swipeRefreshLayout.setRefreshing(false);
                        if (isImage){
                            //图片出题
                            //当主题获取后设置巩固的参数，否则主题Subject是Null,这里xd为1小学，因为没有传递，而且目前只有小学
                            rvQuestionTvAnswerAdapter.fetchNeedParam(modeMap.get(pull_mode.getPullContent()));


                            //请求错题详情
                            requestMistakesDetails(false);
                        }else {
                            //题库出题 todo    目前去巩固  还有问题
                            //请求错题详情
                            requestMistakesDetails(false);
                        }

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
                        //判断是图片出题
                        if (isImage){

                            rvQuestionTvAnswerAdapter.notifyDataSetChanged();
                        }else {
                            //题库出题
                            rvQuestionBankAnswerAdapter.notifyDataSetChanged();
                        }
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
    private RVQuestionBankAnswerAdapter rvQuestionBankAnswerAdapter;

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
        customHeadLayout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());

        pull_tag.setOnClickListener(this);
        mistakes_custom_date_layout.setOnClickListener(this);

        pull_subject.setSpinnerClick(this);
        pull_type.setSpinnerClick(this);
        pull_difficulty.setSpinnerClick(this);
        pull_mode.setSpinnerClick(this);
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
        stringBuilder.append("错题集_");
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

        modeMap = new LinkedHashMap<>();
        modeMap.put("题库出题", "2");
        modeMap.put("自定义出题", "1");
        List<String> modes = new ArrayList<String>(modeMap.keySet());
        pull_mode.setDataList(modes);
        pull_mode.setPullContent(modes.get(0));

        dateTime = new ArrayList<>();
        dateTime.add("今天");
        dateTime.add("一周");
        dateTime.add("一月");
        dateTime.add("自定义");
        pull_date.setDataList(dateTime);
        pull_date.setPullContent(dateTime.get(1));
        //默认选中今天
        calculateDateSection(pull_date.getPullContent());

        mistakes_start_tv.setText(startDate);
        mistakes_end_tv.setText(endDate);

        //初始化错题集RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xRecyclerView.setLayoutManager(layoutManager);

        //设置没有更多数据的显示
        xRecyclerView.getDefaultFootView().setNoMoreHint("没有更多数据了");

        // When the item number of the screen number is list.size-2,we call the onLoadMore
//        xRecyclerView.setLimitNumberToCallLoadMore(2);
        //添加上拉或者下拉加载
        xRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                questionInfoList.clear();
                curPageNo = 1;
                requestMistakesDetails(false);
            }

            @Override
            public void onLoadMore() {
                //滑动到底部
                curPageNo++;
                requestMistakesDetails(true);

            }
        });




        String imageType = modeMap.get(pull_mode.getPullContent());


        if (imageType.equals("1")){
            isImage=true;
        }else {
            isImage=false;
        }
        questionInfoList = new ArrayList<>();
        //若果是图片出题，走图片出题的适配，题库出题走题库的适配
        if (isImage){

            /**
             * status 作业的状态,
             * isImage 是不是图片出题
             * mistakesShown 是不是错题集
             *  types   0 是互动   1是作业
             */
            questionInfoList = new ArrayList<>();
            rvQuestionTvAnswerAdapter = new RVQuestionTvAnswerAdapter(this, "2",
                    isImage, true, 1);

            rvQuestionTvAnswerAdapter.setQuestionInfoList(questionInfoList, "");
            xRecyclerView.setAdapter(rvQuestionTvAnswerAdapter);
        }else {
            //这里是题库出题的作业详情
            questionInfoList = new ArrayList<>();
            rvQuestionBankAnswerAdapter = new RVQuestionBankAnswerAdapter(this, "2",
                    isImage );

            rvQuestionBankAnswerAdapter.setQuestionInfoList(questionInfoList, difficultyMap.get(pull_difficulty.getPullContent()),
                    subjectMap.get(pull_subject.getPullContent()));
            xRecyclerView.setAdapter(rvQuestionBankAnswerAdapter);
        }



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

                        QZXTools.logE("resultJson=" + subjectiveListBean.getResult().size()+"", null);

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

        //如果不是加载更多的话就清空集合 是图片出题
        if (!isLoadingMore && isImage) {
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
        paraMap.put("byhand", modeMap.get(pull_mode.getPullContent()));
        // 例如 2019-05-12 00:00:00
        paraMap.put("startTime", startDate);
        paraMap.put("endTime", endDate);

        paraMap.put("pageSize","10");

        paraMap.put("pageNo", curPageNo + "");

        // 9c5e8fc2cc6e5a197b4ea823497f3da7e11c63b33fa2c7c3b1d76c42aa45b88c746fcb62bd7ab0fb00131e0cb389bea9a6b7c3b97860a87a
        QZXTools.logE("paraMap:"+new Gson().toJson(paraMap), null);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                e.fillInStackTrace();
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
            case R.id.mistakes_pull_mode:
                curPageNo = 1;
                pull_mode.setPullContent(text);
                if (text.equals("自定义出题")){
                    isImage=true;
                }else {
                    isImage=false;
                }
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
        if (isImage){
            //更新参数  学科   难度    typeMap  题型  比如简单和困难    模式   题库出题 和图片出题
            /**
             * status 作业的状态,
             * isImage 是不是图片出题
             * mistakesShown 是不是错题集
             *  types   0 是互动   1是作业
             */
            questionInfoList = new ArrayList<>();
            rvQuestionTvAnswerAdapter = new RVQuestionTvAnswerAdapter(this, "2",
                    isImage, true, 1);

            rvQuestionTvAnswerAdapter.setQuestionInfoList(questionInfoList, "");
            xRecyclerView.setAdapter(rvQuestionTvAnswerAdapter);
            rvQuestionTvAnswerAdapter.fetchNeedParam(modeMap.get(pull_mode.getPullContent()));


            requestMistakesDetails(false);
        }else {
            //题库出题

            questionInfoList = new ArrayList<>();
            rvQuestionBankAnswerAdapter = new RVQuestionBankAnswerAdapter(this, "2",
                    isImage );

            rvQuestionBankAnswerAdapter.setQuestionInfoList(questionInfoList, difficultyMap.get(pull_difficulty.getPullContent()),
                    subjectMap.get(pull_subject.getPullContent()));
            xRecyclerView.setAdapter(rvQuestionBankAnswerAdapter);


            requestMistakesDetails(false);
        }

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


    /**
     * 导出Docx
     */
    public void exportDocx(View view) {
        if (questionInfoList==null||questionInfoList.size()==0){
            QZXTools.popCommonToast(MistakesCollectionActivity.this,"暂无错题",false);
            return;
        }

        if (!ViewUtils.isFastClick(1000)){
            return;
        }

        //设置为全不选
        for (QuestionInfo questionInfo:questionInfoList){
            questionInfo.setChecked(false);
        }

        if ("1".equals(modeMap.get(pull_mode.getPullContent()))){//图片出题
            showImageQuestionDialog(questionInfoList);
        }else {//题库出题
            showQuestionDialog(questionInfoList);
        }
    }

    private XToast toast;
    private boolean checkedAll;
    private MistakesCollectionExportAdapter exportAdapter;
    private String flag;
    private EmojiEditText et_email;

    /**
     * 题库出题选择
     *
     * @param list
     */
    private void showImageQuestionDialog(List<QuestionInfo> list) {
        toast = new XToast(this)
                .setView(R.layout.toast_export_questions)
                .setOutsideTouchable(false)
                .setBackgroundDimAmount(0.5f)
                .setText(R.id.tv_name,"错题集导出")
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
                                QZXTools.popToast(MistakesCollectionActivity.this, "邮箱不可为空", false);
                                return;
                            }

                            if (!FormatUtils.isEmail(et_email.getText().toString())){
                                QZXTools.popToast(MistakesCollectionActivity.this, "邮箱格式不正确", false);
                                return;
                            }

                            iv_process.setImageResource(R.mipmap.send_finish);
                            rl_email.setVisibility(View.GONE);
                            iv_status.setVisibility(View.VISIBLE);
                            btn_send.setText("完成");

                            flag = "2";

                            //提交
                            sendEmailFromImage(list,et_email.getText().toString(),iv_status);
                        }else if ("2".equals(flag)){
                            iv_process.setImageResource(R.mipmap.question_export);

                            toast.cancel();
                            flag = null;
                        }
                    }
                })
                .show();

        et_email = toast.getView().findViewById(R.id.et_email);
        if (!TextUtils.isEmpty(SharedPreferenceUtil.getInstance(this).getString("exportEmail"))){
            et_email.setText(SharedPreferenceUtil.getInstance(this).getString("exportEmail"));
            et_email.setSelection(SharedPreferenceUtil.getInstance(this).getString("exportEmail").length());
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
     * 发送邮件
     *
     * @param list
     * @param email
     * @param iv_status
     */
    private void sendEmailFromImage(List<QuestionInfo> list, String email, ImageView iv_status){
        String url = UrlUtils.BaseUrl + UrlUtils.Mistake_Collection_Export_Image;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("homeworkids", getHomeworkIdsFromImage(list));
        mapParams.put("questionids", getQuestionIdsFromImage(list));
        mapParams.put("email", email);
        mapParams.put("byhand","1");
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("title", "错题集"+ TimeUtils.timeStamp());
        mapParams.put("tip", AppInfoUtils.getAppName(this)+"导出错题集");

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
                        QZXTools.popToast(MistakesCollectionActivity.this, "服务端错误！", false);
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

                        SharedPreferenceUtil.getInstance(MistakesCollectionActivity.this).setString("exportEmail",et_email.getText().toString());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
//                    iv_status.setImageResource(R.mipmap.email_send_fail);
                }
            }
        });
    }

    /**
     * 问题选择
     */
    private void showQuestionDialog(List<QuestionInfo> list) {
        toast = new XToast(this)
                .setView(R.layout.toast_export_questions)
                .setOutsideTouchable(false)
                .setBackgroundDimAmount(0.5f)
                .setText(R.id.tv_name,"错题集导出")
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
                            for (QuestionInfo questionInfo:list){
                                questionInfo.setChecked(false);
                            }
                            leftDrawable = getResources().getDrawable(R.mipmap.contact_unchecked_icon);
                        }else {
                            checkedAll = true;
                            for (QuestionInfo questionInfo:list){
                                questionInfo.setChecked(true);
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
                            QZXTools.popToast(MistakesCollectionActivity.this, "请选择导出的题目", false);
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
                                QZXTools.popToast(MistakesCollectionActivity.this, "邮箱不可为空", false);
                                return;
                            }

                            if (!FormatUtils.isEmail(et_email.getText().toString())){
                                QZXTools.popToast(MistakesCollectionActivity.this, "邮箱格式不正确", false);
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
        exportAdapter = new MistakesCollectionExportAdapter(this,list);
        rv_questions.setAdapter(exportAdapter);
        exportAdapter.setOnCheckListener(new MistakesCollectionExportAdapter.OnCheckListener() {
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
    private boolean checkedAll(List<QuestionInfo> list){
        for (QuestionInfo questionInfo:list){
            if (!questionInfo.isChecked()){
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
    private void sendEmail(List<QuestionInfo> list, String email, ImageView iv_status){
        String url = UrlUtils.BaseUrl + UrlUtils.Mistake_Collection_Export_Image;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("homeworkids", getHomeworkIds(list));
        mapParams.put("questionids", getQuestionIds(list));
        mapParams.put("email", email);
        mapParams.put("byhand","2");
        mapParams.put("studentid", UserUtils.getUserId());
        mapParams.put("title", "错题集"+ TimeUtils.timeStamp());
        mapParams.put("tip", AppInfoUtils.getAppName(this)+"导出错题集");

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
                        QZXTools.popToast(MistakesCollectionActivity.this, "服务端错误！", false);
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

                        SharedPreferenceUtil.getInstance(MistakesCollectionActivity.this).setString("exportEmail",et_email.getText().toString());

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
    private String getQuestionIds(List<QuestionInfo> list){
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
     * 获取问题Id
     *
     * @param list
     * @return
     */
    private String getQuestionIdsFromImage(List<QuestionInfo> list){
        StringBuffer questionIds = new StringBuffer();
        for (int i=0;i<list.size();i++){
            questionIds.append(list.get(i).getId()+",");
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
    private String getHomeworkIds(List<QuestionInfo> list){
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

    /**
     * 获取作业Id
     *
     * @param list
     * @return
     */
    private String getHomeworkIdsFromImage(List<QuestionInfo> list){
        StringBuffer questionIds = new StringBuffer();
        for (int i=0;i<list.size();i++){
            questionIds.append(list.get(i).getHomeworkId()+",");
        }
        if (questionIds.toString().length()>0){
            return questionIds.toString().substring(0,questionIds.toString().length()-1);
        }else {
            return questionIds.toString();
        }
    }
}
