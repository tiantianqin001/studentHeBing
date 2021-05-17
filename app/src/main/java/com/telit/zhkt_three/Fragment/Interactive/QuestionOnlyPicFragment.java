package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.toast.ToastUtils;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Adapter.VPHomeWorkDetailAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.LazyViewPager;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.CollectionInfoBean;
import com.telit.zhkt_three.JavaBean.Gson.HomeWorkByHandBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfoByhand;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.HomeWorkCommit.HomeworkCommitBean;
import com.telit.zhkt_three.JavaBean.HomeWorkCommit.QuestionIdsBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
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
 * Date: 2020/3/23 16:25
 * <p>
 * 提问Fragment：单选、多选、判断、填空和主观题
 */
public class QuestionOnlyPicFragment extends Fragment implements View.OnClickListener {
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

    //  private CircleProgressDialogFragment circleProgressDialogFragment;

    private ScheduledExecutorService timeExecutor;

    private long timerCount;

    private boolean isTimeOver = false;

    /**
     * 提问ID
     */
    private String practiceId;

    /**
     * 提问状态,为0即要作答的状态
     */
    private String taskStatus = "0";
    private CircleProgressDialogFragment circleProgressDialog;
    private List<QuestionInfoByhand> questionInfoByhandList;
    private String homeworkId;
    private TextView homework_commit;
    private VPHomeWorkDetailAdapter vpHomeWorkDetailAdapter;

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Add_Collect_Success = 5;
    private static final int Add_Collect_Failed = 6;
    private static final int Cancel_Collect_Success = 7;
    private static final int Cancel_Collect_Failed = 8;

    private static boolean isShow = false;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow) {
                        QZXTools.popToast(getContext(), getContext().getResources().getString(R.string.current_net_err), false);
                        if (circleProgressDialog != null) {
                            circleProgressDialog.dismissAllowingStateLoss();
                            circleProgressDialog = null;
                        }
                    }

                    break;
                case Error404:
                    if (isShow) {
                        QZXTools.popToast(getContext(), "没有相关资源！", false);
                        if (circleProgressDialog != null) {
                            circleProgressDialog.dismissAllowingStateLoss();
                            circleProgressDialog = null;
                        }
                    }

                    break;
                case Operator_Success:
                    if (isShow) {
                        if (circleProgressDialog != null) {
                            circleProgressDialog.dismissAllowingStateLoss();
                            circleProgressDialog = null;
                        }
                        //清除已经有的数据
                        if (questionInfoByhandList != null) {
                            questionInfoByhandList.clear();
                        }
                        questionInfoByhandList = (List<QuestionInfoByhand>) msg.obj;

                        totalPageCount = questionInfoByhandList.size();
                        //下一题和上一题
                        if (totalPageCount > 1) {
                            practice_right.setVisibility(View.VISIBLE);
                            practice_left.setVisibility(View.INVISIBLE);
                        } else {
                            practice_left.setVisibility(View.INVISIBLE);
                            practice_right.setVisibility(View.INVISIBLE);
                        }
                        //塞入Vp的数据
                        vpHomeWorkDetailAdapter = new VPHomeWorkDetailAdapter
                                (getActivity(), questionInfoByhandList, null, taskStatus, 0, "");
                        //添加提问的答案显示 在提交答案的时候正确答案不显示
                         vpHomeWorkDetailAdapter.needShowAnswer();
                        practice_viewpager.setAdapter(vpHomeWorkDetailAdapter);
                    }

                    break;
                case Add_Collect_Success:
                    if (isShow) {
                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                        practice_collect_white.setVisibility(View.GONE);
                        practice_collect_red.setVisibility(View.VISIBLE);
                    }

                    break;
                case Add_Collect_Failed:
                    if (isShow) {

                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                    }
                    break;
                case Cancel_Collect_Success:
                    if (isShow) {
                        QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                        practice_collect_red.setVisibility(View.GONE);
                        practice_collect_white.setVisibility(View.VISIBLE);
                    }
                    break;
                case Cancel_Collect_Failed:
                    if (isShow) {
                       // QZXTools.popCommonToast(getContext(), (String) msg.obj, false);
                        ToastUtils.show((String) msg.obj);
                    }
                    break;

                case Commit_Result_Show:

                    String result = (String) msg.obj;


                    //发送刷新通知  作业提交成功
                    EventBus.getDefault().post(homeworkId, Constant.Homework_Commit_Success);

                    //显示作业详情 这个是作业的分析报告
               /*     Intent intent = new Intent(getContext(), HomeWorkDetailActivity.class);
                    intent.putExtra("homeworkId", homeworkId);
                    intent.putExtra("status", 1);
                    intent.putExtra("status", 1);
                    startActivity(intent);*/


                    break;
            }
        }
    };


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_question_layout, container, false);
        homework_commit = view.findViewById(R.id.practice_commit);
        unbinder = ButterKnife.bind(this, view);
        isShow = true;

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
        fetchNetHomeWorkDatas();
        //获取作业id
        homeworkId = getArguments().getString("homeworkId", "");
        EventBus.getDefault().register(this);
        return view;

    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //防止出现界面消失，网络请求才反馈到
        if (circleProgressDialog != null && circleProgressDialog.isAdded()) {
            circleProgressDialog.dismissAllowingStateLoss();
            circleProgressDialog = null;
        }

        if (timeExecutor != null) {
            isTimeOver = true;
            timeExecutor.shutdown();
            timeExecutor = null;
        }
        isShow = false;
        super.onDestroyView();
    }

    private void fetchNetHomeWorkDatas() {

        //图片出题
        String url = UrlUtils.BaseUrl + UrlUtils.HomeWorkDetailsByHand;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("homeworkid", practiceId);
        mapParams.put("status", taskStatus);
        mapParams.put("studentid", UserUtils.getUserId());

        circleProgressDialog = new CircleProgressDialogFragment();

        circleProgressDialog.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());


        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        //查询章节数据
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        HomeWorkByHandBean homeWorkByHandBean = gson.fromJson(resultJson, HomeWorkByHandBean.class);
                        Message message = mHandler.obtainMessage();
                        message.obj = homeWorkByHandBean.getResult();
                        QZXTools.logE("resultJson=" + message.obj, null);
                        message.what = Operator_Success;
                        mHandler.sendMessage(message);
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        if (circleProgressDialog != null) {
                            circleProgressDialog.dismissAllowingStateLoss();
                            circleProgressDialog = null;
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
     * 习题标题
     */
    private String practiceTitle;

    private String collectPrimaryId;

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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

    private String byHand = "1";
    //累计提交的图片文件
    private int commitFileCount;
    private static final int Commit_Result_Show = 3;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.practice_commit:
//                todo  作业的提交
                commitHomeWork();
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

    private void commitHomeWork() {
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
        //一共出了几道题
        int totalQuestionCount =0;
        for (int i=0;i<questionInfoByhandList.size();i++){
            for (int j = 0; j < questionInfoByhandList.get(i).getSheetlist().size(); j++) {
                totalQuestionCount++;
            }
        }
        //更正：按照作业ID以及用户ID
        List<LocalTextAnswersBean> localTextAnswersBeanList = MyApplication.getInstance().getDaoSession()
                .getLocalTextAnswersBeanDao().queryBuilder()
                .where(LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).list();

        if (localTextAnswersBeanList == null || localTextAnswersBeanList.size() <= 0) {
            QZXTools.popCommonToast(getContext(), "就算不会也要尝试做一做嘛！", false);
            return;
        } else if (localTextAnswersBeanList.size() < totalQuestionCount) {
            //作答答案保存记录小于题目总数，表示有题目空白
            QZXTools.popCommonToast(getContext(), "还有题目未完成呢！", false);
            return;
        }

        //todo 检测题目答题的完整性，例如多选、填空以及问答题

      //  homework_commit.setEnabled(false);

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
                        QZXTools.logE("id=" + homeworkId + ";type=" + localTextAnswersBean.getQuestionType(), null);
                        homeworkCommitBean.setAnswerId(answerItem.getItemId());
                        //拍照出题   更正：拍照出题(byhand==1)/题库出题(byhand!=1)两种方式
                        if ("1".equals(byHand)) {
                            homeworkCommitBean.setAnswerContent(answerItem.getContent());
                            homeworkCommitBean.setBlanknum(answerItem.getBlanknum());
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
                     **/
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


        Map<String, String> mapParams = new LinkedHashMap<>();

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

        QZXTools.logE("提问提交mapParams："+new Gson().toJson(mapParams),null);

        /*  *
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         **/
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


                    Gson gson = new Gson();
                    Map<String, Object> data = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                    }.getType());
                    boolean success = (boolean) data.get("success");

                    QZXTools.logE("commit questions resultJson=" + resultJson+success, null);
                    if (success){
                        Message message = mHandler.obtainMessage();
                        message.what = Commit_Result_Show;
                        message.obj = data.get("msg");
                        mHandler.sendMessage(message);
                    }else {
                        Message message = mHandler.obtainMessage();
                        message.what = Cancel_Collect_Failed;
                        message.obj = data.get("msg");
                        mHandler.sendMessage(message);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //作业教师端已经结束是提交
    @Subscriber(tag = Constant.Homework_Commit_end, mode = ThreadMode.MAIN)
    public void SubmitQuestion(String anster) {
        Log.i("qin", "提交提问作业成功: " + anster);
        //提交作业
     //   commitHomeWork();
        //进入白班
        EventBus.getDefault().post("teacher_end", Constant.Homework_Commit_Success_Tijiao);
    }

    public void fromCameraCallback(String flag) {
        if (vpHomeWorkDetailAdapter!=null){
            vpHomeWorkDetailAdapter.fromCameraCallback(flag);
        }
    }

    /**
     * 添加订阅者   画板保存回调这里存粹保存整个画板位图，没有做其他处理，分辨率是平板分辨率，大小还可以(KB)
     */
    @Subscriber(tag = Constant.Subjective_Board_Callback, mode = ThreadMode.MAIN)
    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
        if (vpHomeWorkDetailAdapter!=null){
            vpHomeWorkDetailAdapter.fromBoardCallback(extraInfoBean);
        }
    }
}
