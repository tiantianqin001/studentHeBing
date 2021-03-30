package com.telit.zhkt_three.Activity.HomeWork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.AfterHomeWork.NewJobReportActivity;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.VPHomeWorkDetailAdapter;
import com.telit.zhkt_three.Adapter.interactive.BankPracticeVPAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CusomPater;
import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.SubjectiveToDoView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.Gson.CollectQuestionByHandBean;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkByHandBean;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkByHandBeanTwo;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfoByhand;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.HomeWorkCommit.HomeworkCommitBean;
import com.telit.zhkt_three.JavaBean.HomeWorkCommit.QuestionIdsBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import java.io.File;
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
 * 学生作业详情页
 * <p>
 * 更正：拍照出题(byhand==1)/题库出题(byhand!=1)两种方式
 * status 0
 */
public class HomeWorkDetailActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.homework_detail_vp)
    CusomPater homework_vp;
    @BindView(R.id.homework_back)
    ImageView homework_back;
    @BindView(R.id.homework_title)
    TextView homework_title;
    @BindView(R.id.homework_count)
    TextView homework_count;
    @BindView(R.id.homework_btn_commit)
    TextView homework_commit;
    @BindView(R.id.layout_left)
    LinearLayout layout_left;
    @BindView(R.id.layout_right)
    LinearLayout layout_right;
    @BindView(R.id.tv_comment_teacher)
    TextView tv_comment_teacher;
    //家庭作业状态  0未提交  1 已提交  2 已批阅
    private String taskStatus;

    //一次作业的id
    private String homeworkId;

    private String byHand;//1、图片出题2、题库出题

    //累计提交的图片文件
    private int commitFileCount;

    private int curPageIndex = 0;
    private int totalPageCount;

    //加载进度标志
    private CircleProgressDialogFragment circleProgressDialogFragment;

    /**
     * 总题目数
     */
    private int totalQuestionCount;

    private static boolean isShow=false;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Commit_Result_Show = 3;
    private VPHomeWorkDetailAdapter vpHomeWorkDetailAdapter;
    private Handler mHandler = new Handler() {



        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(HomeWorkDetailActivity.this, "服务端错误！", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    //使能提交按钮
                    homework_commit.setEnabled(true);
                    break;
                case Error404:
                    QZXTools.popToast(HomeWorkDetailActivity.this, "没有相关资源！", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    //使能提交按钮
                    homework_commit.setEnabled(true);

                    break;
                case Operator_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if ("1".equals(byHand)) {
                            List<QuestionInfoByhand> questionInfoByhandList = (List<QuestionInfoByhand>) msg.obj;

                            totalPageCount = questionInfoByhandList.size();

                            //图片出题的总个数
                            for (int i = 0; i < questionInfoByhandList.size(); i++) {
                                totalQuestionCount += questionInfoByhandList.get(i).getSheetlist().size();
                            }

                            if (totalPageCount > 1) {
                                layout_right.setVisibility(View.VISIBLE);
                                layout_left.setVisibility(View.INVISIBLE);
                            } else {
                                layout_left.setVisibility(View.INVISIBLE);
                                layout_right.setVisibility(View.INVISIBLE);
                            }

                            //塞入Vp的数据  在添加一个类型   0是互动  1是作业 再1是作业的情况下还有作业完成和没有完成 taskStatus 显示答案的状态
                            vpHomeWorkDetailAdapter = new VPHomeWorkDetailAdapter
                                    (HomeWorkDetailActivity.this, questionInfoByhandList, null, taskStatus,1,comType);
                            homework_vp.setAdapter(vpHomeWorkDetailAdapter);
                            if (!TextUtils.isEmpty(questionInfoByhandList.get(0).getComment())){

                                tv_comment_teacher.setText("老师批改:  "+questionInfoByhandList.get(0).getComment());
                            }

                        } else {
                            HomeWorkByHandBeanTwo homeWorkByHandBeanTwo = (HomeWorkByHandBeanTwo) msg.obj;

                            List<QuestionBank> questionBankList = homeWorkByHandBeanTwo.getResult();

                            for (QuestionBank questionBank : questionBankList) {
                                questionBank.setHomeworkId(homeworkId);
                            }

                            totalPageCount = questionBankList.size();

                            totalQuestionCount = totalPageCount;

                            if (totalPageCount > 1) {
                                layout_right.setVisibility(View.VISIBLE);
                                layout_left.setVisibility(View.INVISIBLE);
                            } else {
                                layout_left.setVisibility(View.INVISIBLE);
                                layout_right.setVisibility(View.INVISIBLE);
                            }

                            BankPracticeVPAdapter bankPracticeVPAdapter = new BankPracticeVPAdapter(
                                    HomeWorkDetailActivity.this, questionBankList);
                            bankPracticeVPAdapter.setStatus(taskStatus);
                            homework_vp.setAdapter(bankPracticeVPAdapter);
                            bankPracticeVPAdapter.setOnCollectClickListener(new BankPracticeVPAdapter.OnCollectClickListener() {
                                @Override
                                public void OnCollectClickListener(NewKnowledgeQuestionView newKnowledgeQuestionView,QuestionBank questionBank, int curPosition) {
                                    if ("0".equals(questionBank.getIsCollect())){//收藏
                                        collectYeOrNo(questionBank,"1",curPosition,newKnowledgeQuestionView);
                                    }else {//取消收藏
                                        collectYeOrNo(questionBank,"0",curPosition,newKnowledgeQuestionView);
                                    }
                                }
                            });

                            if (questionBankList!=null&&questionBankList.size()>0){
                                if (!TextUtils.isEmpty(questionBankList.get(0).getComment())){
                                    tv_comment_teacher.setText("老师批改:  "+questionBankList.get(0).getComment());
                                }
                            }
                        }
                    }

                    break;
                case Commit_Result_Show:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    String result = (String) msg.obj;
                    QZXTools.popCommonToast(HomeWorkDetailActivity.this, result, false);

                    //删除本地存储的数据,只有当是以前的出题模式的时候（即非题库出题）
//                    if ("1".equals(byHand)) {
//                        List<LocalTextAnswersBean> localTextAnswersBeanList = MyApplication.getInstance().getDaoSession()
//                                .getLocalTextAnswersBeanDao().queryBuilder()
//                                .where(LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId)).list();
//                        for (LocalTextAnswersBean localTextAnswersBean : localTextAnswersBeanList) {
//                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);
//                        }
//                    }

                    //发送刷新通知
                    EventBus.getDefault().post("commit_homework", Constant.Homework_Commit);

                    finish();
                    //提交作业成功埋点
                    BuriedPointUtils.buriedPoint("2017","","","","");
                    break;
            }
        }
    };

    //个人报告Json
    private String resultBackJson;
    private String comType;
    private int types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_detail);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);
        isShow=true;

        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        EventBus.getDefault().register(this);

        layout_left.setOnClickListener(this);
        layout_right.setOnClickListener(this);
        homework_back.setOnClickListener(this);
        homework_commit.setOnClickListener(this);

        Intent intent = getIntent();
        //如果存在说明从个人报告进入的
        resultBackJson = getIntent().getStringExtra("Report_Json");
        //必须传入HomeWorkId以及作业当前状态taskStatus
        homeworkId = intent.getStringExtra("homeworkId");
        taskStatus = intent.getStringExtra("status");
        byHand = intent.getStringExtra("byHand");
        String title = intent.getStringExtra("title");
        //判断是不是作业还没有做
        comType = intent.getStringExtra("comType");
        //判断是作业的类型
        types = intent.getIntExtra("types", 0);
        if (!TextUtils.isEmpty(title)) {
            homework_title.setText(title);
        }

        QZXTools.logE("homeworkId=" + homeworkId + ";taskStatus=" + taskStatus + ";byHand=" + byHand, null);

        if (homeworkId == null || taskStatus == null) {
            QZXTools.popCommonToast(this, "没有作业ID，无效", false);
            finish();
            return;
        }
        //如果是提交或者批阅的状态则不显示提交按钮
        if (taskStatus.equals(Constant.Review_Status) || taskStatus.equals(Constant.Commit_Status)) {
            if (TextUtils.isEmpty(resultBackJson)) {
                homework_commit.setVisibility(View.GONE);

                //这里说明作业已经做完了
                tv_comment_teacher.setVisibility(View.VISIBLE);



            } else {
                homework_commit.setText("进入个人报告");
            }
        }
        fetchNetHomeWorkDatas();

        homework_vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                curPageIndex = i;
                if (i >= (totalPageCount - 1)) {
                    layout_right.setVisibility(View.INVISIBLE);
                    layout_left.setVisibility(View.VISIBLE);
                } else if (i <= 0) {
                    layout_left.setVisibility(View.INVISIBLE);
                    layout_right.setVisibility(View.VISIBLE);
                } else {
                    layout_left.setVisibility(View.VISIBLE);
                    layout_right.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZBVPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ZBVPermission.ACTIVITY_REQUEST_CODE:
                    //这一点注意，我们使用系统相机和相册的请求码不能和  public static final int ACTIVITY_REQUEST_CODE = 0x9;一样
                    ZBVPermission.getInstance().onActivityResult(requestCode, resultCode, data);
                    break;
                case SubjectiveToDoView.CODE_SYS_CAMERA:
                    //data为null,因为自己设定了拍好照图片的保存位置
                    QZXTools.logE("data=" + data, null);
                    EventBus.getDefault().post("CAMERA_CALLBACK", Constant.Subjective_Camera_Callback);
                   /* if (vpHomeWorkDetailAdapter!=null){
                        vpHomeWorkDetailAdapter.fromCameraCallback("CAMERA_CALLBACK");
                    }*/

                    break;
            }
        }
    }


    /**
     * 添加订阅者   画板保存回调这里存粹保存整个画板位图，没有做其他处理，分辨率是平板分辨率，大小还可以(KB)
     */
    @Subscriber(tag = Constant.Subjective_Board_Callback, mode = ThreadMode.MAIN)
    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
       /* if (vpHomeWorkDetailAdapter!=null){
            vpHomeWorkDetailAdapter.fromBoardCallback(extraInfoBean);
        }*/
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        /**
         * 防止内存泄露
         * */
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();

        ZBVPermission.getInstance().recyclerAll();
        isShow=false;
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void fetchNetHomeWorkDatas() {
        String url;
        //题库出题和图片出题调用不同的URL
        if ("1".equals(byHand)) {
            url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHand;
        } else {
            url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHandTwo;
        }

        Map<String, String> mapParams = new LinkedHashMap<>();

        mapParams.put("homeworkid", homeworkId);
        mapParams.put("status", taskStatus);
        mapParams.put("studentid", UserUtils.getUserId());

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

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
                    if ("1".equals(byHand)) {
                        HomeWorkByHandBean homeWorkByHandBean = gson.fromJson(resultJson, HomeWorkByHandBean.class);
//                    QZXTools.logE("homeWorkByHandBean=" + homeWorkByHandBean, null);

                        Message message = mHandler.obtainMessage();
                        message.what = Operator_Success;
                        message.obj = homeWorkByHandBean.getResult();
                        mHandler.sendMessage(message);

                    } else {

                        HomeWorkByHandBeanTwo homeWorkByHandBeanTwo = gson.fromJson(resultJson, HomeWorkByHandBeanTwo.class);
//                    QZXTools.logE("homeWorkByHandBean=" + homeWorkByHandBean, null);

                        Message message = mHandler.obtainMessage();
                        message.what = Operator_Success;
                        message.obj = homeWorkByHandBeanTwo;
                        mHandler.sendMessage(message);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homework_back:
                finish();
                break;
            case R.id.homework_btn_commit:
                //如果从个人报告进入还可以跳转到个人报告
                if (!TextUtils.isEmpty(resultBackJson)) {
                    //跳转到个人作业分析报告界面
                    Intent intent = new Intent(this, NewJobReportActivity.class);
                    intent.putExtra("Report_Json", resultBackJson);
                    startActivity(intent);
                    finish();
                    return;
                }

                /**
                 * 整理提交的结果：
                 * 1、HomeworkCommitBean集合
                 * 2、QuestionIdsBean集合
                 * 一条答案一条记录
                 * 共同点：questionId   homeworkId
                 * */
                List<HomeworkCommitBean> homeworkCommitBeanList = new ArrayList<>();
                List<QuestionIdsBean> questionIdsBeanList = new ArrayList<>();
                // question_files 文件s
                Map<String, File> fileHashMap = new LinkedHashMap<>();

                //更正：按照作业ID以及用户ID
                List<LocalTextAnswersBean> localTextAnswersBeanList = MyApplication.getInstance().getDaoSession()
                        .getLocalTextAnswersBeanDao().queryBuilder()
                        .where(LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).list();

                if (localTextAnswersBeanList == null || localTextAnswersBeanList.size() <= 0) {
                    QZXTools.popCommonToast(this, "就算不会也要尝试做一做嘛！", false);
                    return;
                } else if (localTextAnswersBeanList.size() < totalQuestionCount) {
                    //作答答案保存记录小于题目总数，表示有题目空白
                    QZXTools.popCommonToast(this, "还有题目未完成呢！", false);
                    return;
                }

                //todo 检测题目答题的完整性，例如多选、填空以及问答题

                homework_commit.setEnabled(false);

                //如果是填空题  加一个  blanknum
                Map<String, String> mapParams = new LinkedHashMap<>();

                for (LocalTextAnswersBean localTextAnswersBean : localTextAnswersBeanList) {

                    switch (localTextAnswersBean.getQuestionType()) {
                        case Constant.Single_Choose:
                        case Constant.Multi_Choose:
                            //填空题
                        case Constant.Fill_Blank:
                            List<AnswerItem> answerItemList = localTextAnswersBean.getList();
                            QZXTools.logE("answerItem=" + answerItemList, null);
                            for (AnswerItem answerItem : answerItemList) {
                                HomeworkCommitBean homeworkCommitBean = new HomeworkCommitBean();
                                homeworkCommitBean.setHomeworkId(homeworkId);
                                homeworkCommitBean.setClassId(UserUtils.getClassId());
                                //把之前的StudentId改成传入UserId
                                homeworkCommitBean.setStudentId(UserUtils.getUserId());
                                homeworkCommitBean.setQuestionId(localTextAnswersBean.getQuestionId());
                                homeworkCommitBean.setBlanknum(answerItem.getBlanknum());
                                QZXTools.logE("id=" + homeworkId + ";type=" + localTextAnswersBean.getQuestionType(), null);
                                homeworkCommitBean.setAnswerId(answerItem.getItemId());
                                //拍照出题
                                if ("1".equals(byHand)) {
                                    homeworkCommitBean.setAnswerContent(answerItem.getContent());
                                } else {
                                    if (localTextAnswersBean.getQuestionType() == Constant.Fill_Blank) {

                                        //因为新增字段修改
                                        homeworkCommitBean.setAnswerContent(answerItem.getContent());
                                        homeworkCommitBean.setBlanknum(answerItem.getBlanknum());

//                                        //封装成Json
//                                        String answer = answerItem.getContent();
//                                        String[] splits = answer.split(":");
//                                        JSONObject jsonObject = new JSONObject();
//                                        try {
//                                            jsonObject.put(splits[0], splits[1]);
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        QZXTools.logE("answer json=" + jsonObject.toString(), null);
//                                        homeworkCommitBean.setAnswerContent(jsonObject.toString());
                                    } else {
                                        homeworkCommitBean.setAnswerContent(answerItem.getContent());
                                    }
                                }
                                homeworkCommitBeanList.add(homeworkCommitBean);
                            }
                            break;
                        case Constant.Subject_Item:
                            commitFileCount = 0;

                            HomeworkCommitBean homeworkCommitBean = new HomeworkCommitBean();
                            homeworkCommitBean.setHomeworkId(homeworkId);
                            homeworkCommitBean.setClassId(UserUtils.getClassId());
                            //把之前的StudentId改成传入UserId
                            homeworkCommitBean.setStudentId(UserUtils.getUserId());
                            homeworkCommitBean.setQuestionId(localTextAnswersBean.getQuestionId());
                            QZXTools.logE("id=" + homeworkId + ";type=" + localTextAnswersBean.getQuestionType(), null);

                            homeworkCommitBean.setAnswerContent(localTextAnswersBean.getAnswerContent());

                            //主观题可能存在图片文件
                            List<String> imgPathList = localTextAnswersBean.getImageList();

                            /**
                             * 如果没有图片也会提交：questionId count=0
                             * */
                            if (imgPathList != null) {

                                QZXTools.logE("累计提交的文件数量=" + imgPathList.size(), null);

                                commitFileCount += imgPathList.size();

                                //封入文件
                                for (String imgPath : imgPathList) {
                                    File file = new File(imgPath);
                                    QZXTools.logE("imgPath=" + imgPath + ";fileName=" + file.getName(), null);
                                    //String fileName = imgPath.substring(imgPath.lastIndexOf("/")+1);===>file.getName();
                                    fileHashMap.put(file.getName(), file);
                                }
                                //封入文件辅助
                                QuestionIdsBean questionIdsBean = new QuestionIdsBean();
                                questionIdsBean.setCount(commitFileCount + "");
                                questionIdsBean.setQuestionId(localTextAnswersBean.getQuestionId());
                                questionIdsBeanList.add(questionIdsBean);
                            }
                            homeworkCommitBeanList.add(homeworkCommitBean);
                            break;
                        case Constant.Judge_Item:

                            HomeworkCommitBean homeworkCommitBean_judge = new HomeworkCommitBean();
                            homeworkCommitBean_judge.setHomeworkId(homeworkId);
                            homeworkCommitBean_judge.setClassId(UserUtils.getClassId());
                            //把之前的StudentId改成传入UserId
                            homeworkCommitBean_judge.setStudentId(UserUtils.getUserId());
                            homeworkCommitBean_judge.setQuestionId(localTextAnswersBean.getQuestionId());

                            if ("1".equals(byHand)) {
                                homeworkCommitBean_judge.setAnswerContent(localTextAnswersBean.getAnswerContent());

                                QZXTools.logE("id=" + homeworkId + ";type=" + localTextAnswersBean.getQuestionType()
                                        + ";content=" + localTextAnswersBean.getAnswerContent(), null);
                            } else {
                                List<AnswerItem> bankJudge = localTextAnswersBean.getList();
                                for (AnswerItem answerItem : bankJudge) {
                                    homeworkCommitBean_judge.setAnswerContent(answerItem.getContent());
                                }
                            }
                            homeworkCommitBeanList.add(homeworkCommitBean_judge);
                            break;
                        case Constant.Linked_Line:
                            HomeworkCommitBean homeworkCommitBean_linked = new HomeworkCommitBean();
                            homeworkCommitBean_linked.setHomeworkId(homeworkId);
                            homeworkCommitBean_linked.setClassId(UserUtils.getClassId());
                            //把之前的StudentId改成传入UserId
                            homeworkCommitBean_linked.setStudentId(UserUtils.getUserId());
                            homeworkCommitBean_linked.setQuestionId(localTextAnswersBean.getQuestionId());
                            QZXTools.logE("id=" + homeworkId + ";type=" + localTextAnswersBean.getQuestionType(), null);

                            homeworkCommitBean_linked.setAnswerContent(localTextAnswersBean.getAnswerContent());
                            homeworkCommitBeanList.add(homeworkCommitBean_linked);
                            break;
                    }
                }

                String url = UrlUtils.BaseUrl + UrlUtils.HomeWorkCommit;

                if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                    circleProgressDialogFragment.dismissAllowingStateLoss();
                    circleProgressDialogFragment = null;
                }
                circleProgressDialogFragment = new CircleProgressDialogFragment();
                circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());


                Gson gson = new Gson();

                String answerlist = gson.toJson(homeworkCommitBeanList);
                String question_ids = gson.toJson(questionIdsBeanList);

                QZXTools.logE("answerlist=" + answerlist + ";question_ids=" + question_ids, null);

                //答案的json字符串

                mapParams.put("answerlist", answerlist);
                mapParams.put("studentid", UserUtils.getUserId());
                mapParams.put("classid", UserUtils.getClassId());
                mapParams.put("homeworkid", homeworkId);
                //简答题的辅助
                mapParams.put("question_ids", question_ids);

                /**
                 * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
                 * */
                OkHttp3_0Utils.getInstance().asyncPostMultiOkHttp(url, "question_files", mapParams, fileHashMap, new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        //服务端错误
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String resultJson = response.body().string();
                            // {"success":true,"errorCode":"1","msg":"提交成功！","result":[],"total":0,"pageNo":0}
                            QZXTools.logE("commit questions resultJson=" + resultJson, null);

                            Gson gson = new Gson();
                            Map<String, Object> data = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                            }.getType());

                            Message message = mHandler.obtainMessage();
                            message.what = Commit_Result_Show;
                            message.obj = data.get("msg");
                            mHandler.sendMessage(message);
                        } else {
                            mHandler.sendEmptyMessage(Error404);
                        }
                    }
                });
                break;
            case R.id.layout_left:
                curPageIndex--;
                if (curPageIndex >= 0) {
                    if (curPageIndex == 0) {
                        layout_left.setVisibility(View.INVISIBLE);
                    } else {
                        layout_left.setVisibility(View.VISIBLE);
                    }
                    layout_right.setVisibility(View.VISIBLE);
                    homework_vp.setCurrentItem(curPageIndex, true);
                }
                break;
            case R.id.layout_right:
                curPageIndex++;
                if (curPageIndex <= totalPageCount - 1) {
                    if (curPageIndex == totalPageCount - 1) {
                        layout_right.setVisibility(View.INVISIBLE);
                    } else {
                        layout_right.setVisibility(View.VISIBLE);
                    }
                    layout_left.setVisibility(View.VISIBLE);
                    homework_vp.setCurrentItem(curPageIndex, true);
                }
                break;
        }
    }

    /**
     * 收藏或取消收藏
     *
     * @param questionBank
     * @param option 1、收藏 0、取消收藏
     * @param curPosition
     */
    private void collectYeOrNo(QuestionBank questionBank, String option,int curPosition,NewKnowledgeQuestionView newKnowledgeQuestionView){
        QZXTools.logE("collectId==========" + questionBank.getCollectId(), null);
        QZXTools.logE("questionId==========" + questionBank.getId(), null);
        QZXTools.logE("HomeworkId==========" + questionBank.getHomeworkId(), null);
        QZXTools.logE("subjectId==========" + questionBank.getSubjectId(), null);
        QZXTools.logE("studentId==========" + UserUtils.getUserId(), null);
        QZXTools.logE("title==========" + questionBank.getHomeworkTitle()+"-"+getQuestionChannelTypeName(questionBank)+"-第"+(curPosition+1)+"题", null);
        QZXTools.logE("option==========" + option, null);

        String url = UrlUtils.BaseUrl + UrlUtils.CollectQuestionYesOrNo;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("collectId", questionBank.getCollectId()+"");
        mapParams.put("questionId", questionBank.getId()+"");
        mapParams.put("homeworkId", questionBank.getHomeworkId());
        mapParams.put("subjectId", questionBank.getSubjectId());
        mapParams.put("studentId", UserUtils.getUserId());
        mapParams.put("title", questionBank.getHomeworkTitle()+"-"+getQuestionChannelTypeName(questionBank)+"-第"+(curPosition+1)+"题");
        mapParams.put("option", option);

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.popToast(HomeWorkDetailActivity.this, "服务端错误！", false);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
                    QZXTools.logE("commit questions resultJson=" + resultJson, null);

                   runOnUiThread(new Runnable() {
                       @Override
                       public void run() {
                           CollectQuestionByHandBean collectQuestionByHandBean = new Gson().fromJson(resultJson, CollectQuestionByHandBean.class);
                           if ("1".equals(collectQuestionByHandBean.getErrorCode())){
                               if ("1".equals(option)){//收藏
                                   questionBank.setCollectId(collectQuestionByHandBean.getResult().get(0).getCollectId());
                               }else{//取消收藏
                                   questionBank.setCollectId(null);
                               }

                               questionBank.setIsCollect(option);
                               newKnowledgeQuestionView.setCollect(option);
                           }
                       }
                   });
                } else {
                    QZXTools.popToast(HomeWorkDetailActivity.this, "没有相关资源！", false);
                }
            }
        });
    }

    /**
     * 获取题目类型
     *
     * @return
     */
    private String getQuestionChannelTypeName(QuestionBank questionBank){
        switch (questionBank.getQuestionChannelType()){
            case Constant.Single_Choose:
                return "单选题";
            case Constant.Multi_Choose:
                return "多选题";
            case Constant.Fill_Blank:
                return "填空题";
            case Constant.Subject_Item:
                return "主观题";
            case Constant.Linked_Line:
                return "连线题";
            case Constant.Judge_Item:
                return "判断题";
            default:
                return "";
        }
    }


}
