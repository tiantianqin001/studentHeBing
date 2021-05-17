package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.interactive.BankPracticeVPAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CusomPater;
import com.telit.zhkt_three.CustomView.LazyViewPager;
import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.SubjectiveToDoView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.Gson.CollectQuestionByHandBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

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
 * 收藏题目详情页
 * <p>
 * 更正：拍照出题(byhand==1)/题库出题(byhand!=1)两种方式
 * status 0
 */
public class CollectQuestionDetailActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.homework_detail_vp)
    CusomPater homework_vp;
    @BindView(R.id.homework_back)
    ImageView homework_back;
    @BindView(R.id.homework_title)
    TextView homework_title;
    @BindView(R.id.homework_count)
    TextView homework_count;
    @BindView(R.id.layout_left_collect)
    LinearLayout layout_left;
    @BindView(R.id.layout_right_collect)
    LinearLayout layout_right;
    @BindView(R.id.tv_comment_teacher)
    TextView tv_comment_teacher;
    //家庭作业状态  0未提交  1 已提交  2 已批阅
    private String taskStatus;

    private String byHand;//1、图片出题2、题库出题

    private int totalQuestionCount;

    private int curPageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collect_question_detail);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        layout_left.setOnClickListener(this);
        layout_right.setOnClickListener(this);
        homework_back.setOnClickListener(this);

        Intent intent = getIntent();
        //如果存在说明从个人报告进入的
        //必须传入HomeWorkId以及作业当前状态taskStatus
        taskStatus = intent.getStringExtra("status");
        byHand = intent.getStringExtra("byHand");
        String title = intent.getStringExtra("title");
        //判断是不是作业还没有做
        //判断是作业的类型
        if (!TextUtils.isEmpty(title)) {
            homework_title.setText(title);
        }

        //如果是提交或者批阅的状态则不显示提交按钮
        if (taskStatus.equals(Constant.Review_Status) || taskStatus.equals(Constant.Commit_Status)) {
            //这里说明作业已经做完了
            tv_comment_teacher.setVisibility(View.VISIBLE);
        }

        curPageIndex = getIntent().getIntExtra("curPageIndex",0);
        List<QuestionBank> mData = intent.getParcelableArrayListExtra("questionBanks");
        if (mData!=null&&mData.size()>0){
            totalQuestionCount = mData.size();
            fetchNetHomeWorkDatas(mData);
        }

        homework_vp.setOnPageChangeListener(new LazyViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                curPageIndex = i;
                if (i >= (totalQuestionCount - 1)) {
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
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        QZXTools.setmToastNull();

        ZBVPermission.getInstance().recyclerAll();
        super.onDestroy();
    }

    /**
     * 设置数据
     *
     * @param questionBanks
     */
    private void fetchNetHomeWorkDatas(List<QuestionBank> questionBanks) {
        QZXTools.logE("curPageIndex:" + curPageIndex, null);
        QZXTools.logE("totalQuestionCount:" + totalQuestionCount, null);

        if (totalQuestionCount==1){
            layout_right.setVisibility(View.INVISIBLE);
            layout_left.setVisibility(View.INVISIBLE);
        }else {
            if (curPageIndex >= (totalQuestionCount - 1)) {
                layout_right.setVisibility(View.INVISIBLE);
                layout_left.setVisibility(View.VISIBLE);
            } else if (curPageIndex <= 0) {
                layout_left.setVisibility(View.INVISIBLE);
                layout_right.setVisibility(View.VISIBLE);
            } else {
                layout_left.setVisibility(View.VISIBLE);
                layout_right.setVisibility(View.VISIBLE);
            }
        }

        BankPracticeVPAdapter bankPracticeVPAdapter = new BankPracticeVPAdapter(
                CollectQuestionDetailActivity.this, questionBanks);
        bankPracticeVPAdapter.setStatus(taskStatus);
        homework_vp.setAdapter(bankPracticeVPAdapter);
        homework_vp.setCurrentItem(curPageIndex, true);

        bankPracticeVPAdapter.setOnCollectClickListener(new BankPracticeVPAdapter.OnCollectClickListener() {
            @Override
            public void OnCollectClickListener(NewKnowledgeQuestionView newKnowledgeQuestionView,QuestionBank questionBank, int curPosition) {
                if (ViewUtils.isFastClick(1000)){
                    if ("0".equals(questionBank.getIsCollect())){//收藏
                        collectYeOrNo(questionBank,"1",curPosition,newKnowledgeQuestionView);
                    }else {//取消收藏
                        collectYeOrNo(questionBank,"0",curPosition,newKnowledgeQuestionView);
                    }
                }
            }
        });

        if (questionBanks!=null&&questionBanks.size()>0){
            if (!TextUtils.isEmpty(questionBanks.get(0).getComment())){
                tv_comment_teacher.setText("老师批改:  "+questionBanks.get(0).getComment());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.homework_back:
                finish();
                break;
            case R.id.layout_left_collect:
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
            case R.id.layout_right_collect:
                curPageIndex++;
                if (curPageIndex <= totalQuestionCount - 1) {
                    if (curPageIndex == totalQuestionCount - 1) {
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
        String url = UrlUtils.BaseUrl + UrlUtils.CollectQuestionYesOrNo;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("collectId", questionBank.getCollectId()+"");
        mapParams.put("questionId", questionBank.getQuestionId()+"");
        mapParams.put("homeworkId", questionBank.getHomeworkId());
        mapParams.put("subjectId", questionBank.getSubjectId()+"");
        mapParams.put("studentId", UserUtils.getUserId());
        mapParams.put("title", questionBank.getHomeworkTitle()+"-"+getQuestionChannelTypeName(questionBank)+"-第"+(curPosition+1)+"题");
        mapParams.put("option", option);


        QZXTools.logE("param:"+new Gson().toJson(mapParams),null);

        /**
         * post传参数时，不管是int类型还是布尔类型统一传入字符串的样式即可
         * */
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.popToast(CollectQuestionDetailActivity.this, "当前网络不佳....", false);
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
                               }

                               questionBank.setIsCollect(option);
                               newKnowledgeQuestionView.setCollect(option);

                               EventBus.getDefault().post("Question_Collect_Success", Constant.Question_Collect_Success);
                           }
                       }
                   });
                } else {
                    QZXTools.popToast(CollectQuestionDetailActivity.this, "没有相关资源！", false);
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
