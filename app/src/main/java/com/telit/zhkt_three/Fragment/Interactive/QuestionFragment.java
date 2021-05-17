package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Adapter.interactive.PracticeVPAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.LazyViewPager;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.Gson.CollectionInfoBean;
import com.telit.zhkt_three.JavaBean.Gson.PracticeBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.HomeWorkCommit.HomeworkCommitBean;
import com.telit.zhkt_three.JavaBean.HomeWorkCommit.QuestionIdsBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/5/18 9:14
 * <p>
 * 设置了两次的连接超时再次请求
 */
public class QuestionFragment extends Fragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.practice_collect_layout)
    RelativeLayout practice_collect_layout;
    @BindView(R.id.practice_collect_white)
    ImageView practice_collect_white;
    @BindView(R.id.practice_collect_red)
    ImageView practice_collect_red;
    @BindView(R.id.practice_time)
    TextView practice_time;
    @BindView(R.id.practice_viewpager)
    LazyViewPager practice_viewpager;
    @BindView(R.id.practice_commit)
    TextView practice_commit;
    @BindView(R.id.practice_left)
    LinearLayout practice_left;
    @BindView(R.id.practice_right)
    LinearLayout practice_right;

    private int curPageIndex = 0;
    private int totalPageCount;

    /**
     * 练习状态：0 要做 1 提交了 2 批阅了
     */
    private String status = "0";

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 随堂练习ID
     */
    private String practiceId;

    private String collectPrimaryId;

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private ScheduledExecutorService timeExecutor;

    private long timerCount;

    private boolean isTimeOver = false;

    /**
     * 习题标题
     */
    private String practiceTitle;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Commit_Result_Show_Success = 3;
    private static final int Commit_Result_Show_Failed = 4;
    private static final int Add_Collect_Success = 5;
    private static final int Add_Collect_Failed = 6;
    private static final int Cancel_Collect_Success = 7;
    private static final int Cancel_Collect_Failed = 8;

    private static boolean isShow=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(getContext(), getContext().getResources().getString(R.string.current_net_err), false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        NoSercerDialog noSercerDialog=new NoSercerDialog();
                        noSercerDialog.show(getChildFragmentManager(), NoSercerDialog.class.getSimpleName());
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(getContext(), "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        NoResultDialog noResultDialog = new NoResultDialog();
                        noResultDialog.show(getChildFragmentManager(), NoResultDialog.class.getSimpleName());
                    }

                    break;
                case Operator_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        List<QuestionInfo> questionInfoList = (List<QuestionInfo>) msg.obj;

                        questionInfoList.get(0).getQuestionContent();

                        totalPageCount = questionInfoList.size();
                        if (totalPageCount > 1) {
                            practice_right.setVisibility(View.VISIBLE);
                            practice_left.setVisibility(View.INVISIBLE);
                        } else {
                            practice_left.setVisibility(View.INVISIBLE);
                            practice_right.setVisibility(View.INVISIBLE);
                        }

                        PracticeVPAdapter practiceVPAdapter = new PracticeVPAdapter(getContext(), questionInfoList);
                        practice_viewpager.setAdapter(practiceVPAdapter);

                        //下发成功，学生开始作答中...
                        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.PAPER_DOING + "", MsgUtils.createPracticeStatus(MsgUtils.PAPER_DOING));
                    }

                    break;
                case Commit_Result_Show_Success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        String result_success = (String) msg.obj;
                        QZXTools.popCommonToast(getContext(), result_success, false);

                        //隐藏提交按钮
                        practice_commit.setVisibility(View.GONE);

                        //通知练习状态
                        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.PAPER_DOING + "", MsgUtils.createPracticeStatus(MsgUtils.PAPER_DOING));
                    }

                    break;
                case Commit_Result_Show_Failed:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        String result_fail = (String) msg.obj;
                        QZXTools.popCommonToast(getContext(), result_fail, false);
                    }

                    break;
                case Add_Collect_Success:
                    if (isShow){
                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                        practice_collect_white.setVisibility(View.GONE);
                        practice_collect_red.setVisibility(View.VISIBLE);
                    }

                    break;
                case Add_Collect_Failed:
                    if (isShow){
                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                    }

                    break;
                case Cancel_Collect_Success:
                    if (isShow){
                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                        practice_collect_red.setVisibility(View.GONE);
                        practice_collect_white.setVisibility(View.VISIBLE);
                    }

                    break;
                case Cancel_Collect_Failed:
                    if (isShow){

                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                    }
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_layout, container, false);
         unbinder = ButterKnife.bind(this, view);
            isShow=true;
        //开启互动计时
        timeExecutor = Executors.newSingleThreadScheduledExecutor();
        timeExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                timerCount++;

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isTimeOver) {
                            practice_time.setText("用时：".concat(QZXTools.getTransmitTime(timerCount)));
                        }
                    }
                });
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);

        practice_viewpager.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                curPageIndex = position;
                if (position >= (totalPageCount - 1)) {
                    practice_right.setVisibility(View.INVISIBLE);
                    practice_left.setVisibility(View.VISIBLE);
                } else if (position <= 0) {
                    practice_left.setVisibility(View.INVISIBLE);
                    practice_right.setVisibility(View.VISIBLE);
                } else {
                    practice_left.setVisibility(View.VISIBLE);
                    practice_right.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        practice_commit.setOnClickListener(this);
        practice_left.setOnClickListener(this);
        practice_right.setOnClickListener(this);
        practice_collect_layout.setOnClickListener(this);

        fetchNeedData();
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //防止出现界面消失，网络请求才反馈到
        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment = null;
        }

        if (timeExecutor != null) {
            isTimeOver = true;
            timeExecutor.shutdown();
            timeExecutor = null;
        }
        isShow=false;
        super.onDestroyView();
    }

    //累计提交的图片文件
    private int commitFileCount;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.practice_commit:
                commitAnswer();
                break;
            case R.id.practice_left:
                curPageIndex--;
                if (curPageIndex >= 0) {
                    if (curPageIndex == 0) {
                        practice_left.setVisibility(View.INVISIBLE);
                    } else {
                        practice_left.setVisibility(View.VISIBLE);
                    }
                    practice_right.setVisibility(View.VISIBLE);
                    practice_viewpager.setCurrentItem(curPageIndex, true);
                }
                break;
            case R.id.practice_right:
                curPageIndex++;
                if (curPageIndex <= totalPageCount - 1) {
                    if (curPageIndex == totalPageCount - 1) {
                        practice_right.setVisibility(View.INVISIBLE);
                    } else {
                        practice_right.setVisibility(View.VISIBLE);
                    }
                    practice_left.setVisibility(View.VISIBLE);
                    practice_viewpager.setCurrentItem(curPageIndex, true);
                }
                break;
            case R.id.practice_collect_layout:
                if (practice_collect_red.getVisibility() == View.VISIBLE) {
                    cancelCollect();
                } else {
                    collectPractice();
                }
                break;
        }
    }

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "添加收藏成功！",
     * "result": {
     * "id": 24,
     * "collectId": "657030420102004b410aea2056c8effd5715",
     * "collectType": "1",
     * "collectName": "123",
     * "userId": "66666702506",
     * "createDate": null,
     * "delFlag": 0
     * },
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 收藏习题
     */
    private void collectPractice() {
        String url = UrlUtils.BaseUrl + UrlUtils.CollectAdd;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("collectId", practiceId);
        if (!TextUtils.isEmpty(practiceTitle)) {
            paraMap.put("collectName", practiceTitle);
        } else {
            practiceTitle = practiceId;
            paraMap.put("collectName", practiceTitle);
        }
        paraMap.put("collectType", "1");//作业
        paraMap.put("userId", UserUtils.getUserId());

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
                    CollectionInfoBean collectionInfoBean = gson.fromJson(resultJson, CollectionInfoBean.class);
                    collectPrimaryId = collectionInfoBean.getResult().getId() + "";
                    Message message = mHandler.obtainMessage();
                    if (collectionInfoBean.getErrorCode().equals("1")) {
                        message.what = Add_Collect_Success;
                    } else {
                        message.what = Add_Collect_Failed;
                    }
                    message.obj = collectionInfoBean.getMsg();
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "取消收藏成功！",
     * "result": [],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 取消收藏习题
     */
    private void cancelCollect() {
        String url = UrlUtils.BaseUrl + UrlUtils.CollectCancel;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("id", "");

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
                    Map<String, Object> resultMap = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    Message message = mHandler.obtainMessage();
                    if (resultMap.get("errorCode").equals("1")) {
                        message.what = Cancel_Collect_Success;
                    } else {
                        message.what = Cancel_Collect_Failed;
                    }
                    message.obj = resultMap.get("msg");
                    mHandler.sendMessage(message);
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    private int curRetryCount = 0;

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "操作成功",
     * "result": [{
     * "id": "638e77a9f1cf4d64a3299aea74184bfb",
     * "homeworkId": "76d8bc720090604ef6089f302503b174d65a",
     * "questionType": "0",
     * "questionContent": "新闻1+1=？",
     * "questionScore": "0.0",
     * "image": "http://172.16.4.40:8090/filesystem/question/036c07dda7b042afa5f1420f39470ef9.png",
     * "imageopted": "http://172.16.4.40:8090/filesystem/question/opted_036c07dda7b042afa5f1420f39470ef9.png",
     * "list": [{
     * "id": "b0df60bba5994cecb7a331025533435f",
     * "content": "0",
     * "index": 1,
     * "options": "A"
     * }, {
     * "id": "5a6114fa4b7244549f29fd353bddc8b8",
     * "content": "1",
     * "index": 2,
     * "options": "B"
     * }, {
     * "id": "bfabe63824c343a4bb11c8983364d83a",
     * "content": "2",
     * "index": 3,
     * "options": "C"
     * }, {
     * "id": "4631864d5be14f3a9a22db77f63a6d00",
     * "content": "3",
     * "index": 4,
     * "options": "D"
     * }],
     * "attachment": null,
     * "index": 1,
     * "leftList": [],
     * "rightList": [],
     * "ownList": [],
     * "resultList": [],
     * "answer": null,
     * "teaDesc": null,
     * "stuRemark": null,
     * "analysis": null,
     * "imgFile": [],
     * "voiceFile": []
     * }],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 获取必要的数据
     */
    private void fetchNeedData() {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        curRetryCount++;

        //请求投票信息
        String url = UrlUtils.BaseUrl + UrlUtils.ClassPractice;
        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("classexamid", practiceId);
        paraMap.put("studentid", UserUtils.getStudentId());
        //todo的状态
        paraMap.put("status", status);
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
                    PracticeBean practiceBean = gson.fromJson(resultJson, PracticeBean.class);
                    if (practiceBean.getResult() != null && practiceBean.getResult().size() > 0) {
                        Message message = mHandler.obtainMessage();
                        message.what = Operator_Success;
                        message.obj = practiceBean.getResult();
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(Error404);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * {"success":true,"errorCode":"1","msg":"提交成功！","result":[],"total":0,"pageNo":0}
     * <p>
     * 提交随堂练习答案
     */
    public void commitAnswer() {
        /**
         * 整理提交的结果：
         * 1、HomeworkCommitBean集合
         * 2、QuestionIdsBean集合
         * 共同点：questionId   homeworkId
         * */
        List<HomeworkCommitBean> homeworkCommitBeanList = new ArrayList<>();
        List<QuestionIdsBean> questionIdsBeanList = new ArrayList<>();
        // question_files 文件s
        Map<String, File> fileHashMap = new LinkedHashMap<>();

        //这里的HomeworkId等同于PracticeId
        List<LocalTextAnswersBean> localTextAnswersBeanList = MyApplication.getInstance().getDaoSession()
                .getLocalTextAnswersBeanDao().queryBuilder()
                .where(LocalTextAnswersBeanDao.Properties.HomeworkId.eq(practiceId)).list();

        if (localTextAnswersBeanList == null || localTextAnswersBeanList.size() <= 0) {
            QZXTools.popCommonToast(getContext(), "就算不会也要尝试做一做哦", false);
            return;
        }

        for (LocalTextAnswersBean localTextAnswersBean : localTextAnswersBeanList) {

            switch (localTextAnswersBean.getQuestionType()) {
                case Constant.Single_Choose:
                case Constant.Multi_Choose:
                case Constant.Fill_Blank:
                    List<AnswerItem> answerItemList = localTextAnswersBean.getList();
                    for (AnswerItem answerItem : answerItemList) {
                        HomeworkCommitBean homeworkCommitBean = new HomeworkCommitBean();
                        homeworkCommitBean.setHomeworkId(practiceId);
                        homeworkCommitBean.setClassId(UserUtils.getClassId());
                        homeworkCommitBean.setStudentId(UserUtils.getStudentId());
                        homeworkCommitBean.setQuestionId(localTextAnswersBean.getQuestionId());
                        QZXTools.logE("id=" + practiceId + ";type=" + localTextAnswersBean.getQuestionType(), null);
                        homeworkCommitBean.setAnswerId(answerItem.getItemId());
                        homeworkCommitBean.setAnswerContent(answerItem.getContent());
                        homeworkCommitBeanList.add(homeworkCommitBean);
                    }
                    break;
                case Constant.Subject_Item:
                    commitFileCount = 0;

                    HomeworkCommitBean homeworkCommitBean = new HomeworkCommitBean();
                    homeworkCommitBean.setHomeworkId(practiceId);
                    homeworkCommitBean.setClassId(UserUtils.getClassId());
                    homeworkCommitBean.setStudentId(UserUtils.getStudentId());
                    homeworkCommitBean.setQuestionId(localTextAnswersBean.getQuestionId());
                    QZXTools.logE("id=" + practiceId + ";type=" + localTextAnswersBean.getQuestionType(), null);

                    homeworkCommitBean.setAnswerContent(localTextAnswersBean.getAnswerContent());

                    //主观题可能存在图片文件
                    List<String> imgPathList = localTextAnswersBean.getImageList();

                    QZXTools.logE("累计提交的文件数量=" + imgPathList.size(), null);

                    commitFileCount += imgPathList.size();

                    if (imgPathList != null) {
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
                    homeworkCommitBean_judge.setHomeworkId(practiceId);
                    homeworkCommitBean_judge.setClassId(UserUtils.getClassId());
                    homeworkCommitBean_judge.setStudentId(UserUtils.getStudentId());
                    homeworkCommitBean_judge.setQuestionId(localTextAnswersBean.getQuestionId());
                    QZXTools.logE("id=" + practiceId + ";type=" + localTextAnswersBean.getQuestionType(), null);

                    homeworkCommitBean_judge.setAnswerContent(localTextAnswersBean.getAnswerContent());
                    homeworkCommitBeanList.add(homeworkCommitBean_judge);
                    break;
            }
        }

        String url = UrlUtils.BaseUrl + UrlUtils.PracticeCommit;

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        Map<String, String> mapParams = new LinkedHashMap<>();

        Gson gson = new Gson();

        String answerlist = gson.toJson(homeworkCommitBeanList);
        String question_ids = gson.toJson(questionIdsBeanList);

        QZXTools.logE("answerlist=" + answerlist + ";question_ids=" + question_ids, null);

        //答案的json字符串
        mapParams.put("answerlist", answerlist);
        mapParams.put("studentid", UserUtils.getStudentId());
        mapParams.put("classid", UserUtils.getClassId());
        mapParams.put("classexamid", practiceId);
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

                    if (data.get("errorCode").equals("1")) {
                        Message message = mHandler.obtainMessage();
                        message.what = Commit_Result_Show_Success;
                        message.obj = data.get("msg");
                        mHandler.sendMessage(message);
                    } else {
                        Message message = mHandler.obtainMessage();
                        message.what = Commit_Result_Show_Failed;
                        message.obj = data.get("msg");
                        mHandler.sendMessage(message);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
}
