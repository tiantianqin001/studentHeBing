package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionTvAnswerAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.MulitBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import java.util.ArrayList;
import java.util.List;

public class JudgeImageNewView extends LinearLayout {
    private static final String TAG = "MulipleChoiseView";
    private Context mContext;

    private final TextView practice_head_index;
    private final RelativeLayout option_do_tv_one;
    private final RelativeLayout option_do_tv_two;
    private final LinearLayout ll_current_quint_show;
    private final ImageView iv_current_quint_show;

    private String taskStatus;
    private LocalTextAnswersBean linkLocal;

    public JudgeImageNewView(Context context) {
        this(context, null);
    }

    public JudgeImageNewView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JudgeImageNewView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;


        View itemView = LayoutInflater.from(context).inflate(R.layout.judgeselect_two_image_layout,
                this, true);


        practice_head_index = itemView.findViewById(R.id.practice_head_index);
        option_do_tv_one = itemView.findViewById(R.id.option_do_tv_one);
        option_do_tv_two = itemView.findViewById(R.id.option_do_tv_two);
        ll_current_quint_show = itemView.findViewById(R.id.ll_current_quint_show);
        iv_current_quint_show = itemView.findViewById(R.id.iv_current_quint_show);

    }

    public void setTaskStatus(String taskStatus) {

        this.taskStatus = taskStatus;
    }

    //设置数据
    public void setViewData(List<QuestionInfo.SelectBean> selectBeans, List<QuestionInfo> questionInfoList, int i,
                            String homeworkId, int homeWorkType) {
        practice_head_index.setText("第" + (i + 1) + "题 共" + questionInfoList.size() + "题");

        //0未提交  1 已提交  2 已批阅
        if (taskStatus.equals(Constant.Todo_Status) || taskStatus.equals(Constant.Save_Status)) {

            if (taskStatus.equals(Constant.Todo_Status)){
                //答案的回显
                //只有是作业显示  互动不获取
                if (homeWorkType == 1){
                    linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
                }

                Log.i(TAG, "onBindViewHolder: " + linkLocal);
                if (linkLocal != null) {
                    if (linkLocal.getAnswerContent().equals("0")) {
                        option_do_tv_one.setSelected(true);
                    } else {
                        option_do_tv_two.setSelected(true);
                    }
                }
            }else if (taskStatus.equals(Constant.Save_Status)){
                if (questionInfoList!=null && questionInfoList.size()>0){
                    List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();
                    if (ownList != null && ownList.size() > 0) {
                        WorkOwnResult workOwnResult = questionInfoList.get(i).getOwnList().get(0);
                        String answerContent = workOwnResult.getAnswerContent();
                        if (answerContent.equals("0")) {
                            option_do_tv_two.setSelected(true);
                        } else {
                            option_do_tv_one.setSelected(true);
                        }
                    }

                    //数据的保存
                    if (questionInfoList!=null && questionInfoList.size()>0){
                        WorkOwnResult workOwnResult = questionInfoList.get(i).getOwnList().get(0);
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(questionInfoList.get(i).getHomeworkId());
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(workOwnResult.getAnswerContent());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                    }

                }


            }

            //这个是正确
            option_do_tv_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    option_do_tv_two.setSelected(false);
                    if (option_do_tv_one.isSelected()) {
                        option_do_tv_one.setSelected(false);
                        MyApplication.getInstance().getDaoSession()
                                .getLocalTextAnswersBeanDao().deleteByKey(questionInfoList.get(i).getId());
                    } else {
                        option_do_tv_one.setSelected(true);

                        //只有作业的时候保存答案 提问也要保留答案
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(homeworkId);
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(1 + "");
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession()
                                .getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                    }
                }
            });
        //这个是错误
            option_do_tv_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    option_do_tv_one.setSelected(false);
                    if (option_do_tv_two.isSelected()) {
                        option_do_tv_two.setSelected(false);
                        MyApplication.getInstance().getDaoSession()
                                .getLocalTextAnswersBeanDao().deleteByKey(questionInfoList.get(i).getId());
                    } else {
                        option_do_tv_two.setSelected(true);

                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(homeworkId);
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(0 + "");
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession()
                                .getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                    }
                }
            });
        } else {
            ll_current_quint_show.setVisibility(VISIBLE);
            //设置已经提交了答案的显示
            List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();
            if (ownList != null && ownList.size() > 0) {
                WorkOwnResult workOwnResult = questionInfoList.get(i).getOwnList().get(0);
                String answerContent = workOwnResult.getAnswerContent();
                if (answerContent.equals("0")) {
                    //当前学生选择的正确

                    option_do_tv_two.setSelected(true);
                    option_do_tv_one.setSelected(false);
                } else {
                    //当前学生选择的是错误
                    option_do_tv_one.setSelected(true);
                    option_do_tv_two.setSelected(false);
                }
            }
            //设置正确答案
            String answer = questionInfoList.get(i).getAnswer();
            if (answer.equals("0")) {
                Glide.with(mContext).load(R.mipmap.check_err_ome).into(iv_current_quint_show);
            } else {
                Glide.with(mContext).load(R.mipmap.check_current_two).into(iv_current_quint_show);

            }
            //

        }
    }
}
