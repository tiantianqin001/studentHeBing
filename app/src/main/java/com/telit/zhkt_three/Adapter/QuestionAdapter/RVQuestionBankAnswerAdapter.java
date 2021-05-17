package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.SubjectBean;
import com.telit.zhkt_three.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 16:44
 * <p>
 * 注意：这个和拍照出题公用Adapter，所以这里判断是否是错题集入口分别进入拍照出题/题库出题
 */
public class RVQuestionBankAnswerAdapter extends RecyclerView.Adapter<RVQuestionBankAnswerAdapter.ViewHolder> {
    private static final String TAG = "RVQuestionTvAnswerAdapter";
    private Context mContext;

    //0未提交  1 已提交  2 已批阅
    private String taskStatus;

    //是否是错题集展示界面
    private boolean isMistakesShown;
    //0 互动  1作业
    private int types;

    //输入表情前的光标位置
    private int cursorPos;
    //输入表情前EditText中的文本
    private String inputAfterText;
    //是否重置了EditText的内容
    private boolean resetText;
    private int layoutPosition;

    /**
     * 是否是图片出题模式即随堂练习模式
     */
    private boolean isImageTask;

    private List<QuestionInfo> questionInfoList;
    private String difficulty;
    private String subject;

    private List<SubjectBean> subjectBeanList = new ArrayList<>();

    /**
     * 主观题题目ID
     */
    public static String subjQuestionId;


    private QuestionInfo subjectQuestionInfo;

    /**
     * 如果homeworkid为空字符串表示没有homeworkid，在QuestionInfo中存在homeworkid
     */
    public void setQuestionInfoList(List<QuestionInfo> questionInfoList, String difficulty, String subject) {
        //这个是提真正的数据
        this.questionInfoList = questionInfoList;


        this.difficulty = difficulty;
        this.subject = subject;
    }

    /**
     * @param status          作业的状态
     * @param isImageQuestion 是否图片出题模式

     */
    public RVQuestionBankAnswerAdapter(Context context, String status, boolean isImageQuestion) {
        mContext = context;
        taskStatus = status;
        isImageTask = isImageQuestion;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_bank_practice_quint_item_layout, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        //错题集数据类型不一样，要转换一下

        QuestionBank questionBank = new QuestionBank();
        questionBank.setHomeworkId(questionInfoList.get(i).getHomeworkId());
        questionBank.setQuestionId(Integer.parseInt(questionInfoList.get(i).getId()));
        questionBank.setQuestionChannelType(questionInfoList.get(i).getQuestionType());
        questionBank.setQuestionText(questionInfoList.get(i).getQuestionContent());
        questionBank.setAnswerOptions(questionInfoList.get(i).getAnswerOption());
        questionBank.setAnswerText(questionInfoList.get(i).getAnswer());
        questionBank.setAnswer(questionInfoList.get(i).getAnswerImg());
        questionBank.setExplanation(questionInfoList.get(i).getAnalysis());
        questionBank.setOwnList(questionInfoList.get(i).getOwnList());
        questionBank.setScore(questionInfoList.get(i).getScore());
        questionBank.setOwnscore(questionInfoList.get(i).getOwnscore());
        questionBank.setQuestionBanks(questionInfoList.get(i).getQuestionBanks());
        //传入主观题图片
        //questionBank.setImgFile(questionInfoList.get(i).getImgFile());
        //因为之前根据List是否存在判断是否有子List的
        questionBank.setList(questionInfoList.get(i).getqBankList());
        questionBank.setShownAnswer(true);


        viewHolder.item_practice_question.setQuestionInfo(questionBank, i, taskStatus, true);
        Bundle bundle = new Bundle();
        bundle.putString("xd", "1");
        bundle.putString("subject", questionInfoList.get(i).getChid() + "");
        bundle.putString("difficulty", difficulty);
        //types 0 是互动，  1是作业
        bundle.putInt("types",types);
        bundle.putInt("questionType", questionInfoList.get(i).getQuestionType());
        if (!TextUtils.isEmpty(questionInfoList.get(i).getKnowledge())) {
            bundle.putString("knowledge_json", questionInfoList.get(i).getKnowledge());
        }
        viewHolder.item_practice_question.setBundle(bundle);

    }




    @Override
    public int getItemCount() {
        return questionInfoList != null ? questionInfoList.size() : 0;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{

        private  NewKnowledgeQuestionView item_practice_question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_practice_question = itemView.findViewById(R.id.item_practice_question);
        }
    }


}
