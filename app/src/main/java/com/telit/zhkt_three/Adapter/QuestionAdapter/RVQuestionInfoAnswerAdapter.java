package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 16:44
 * <p>
 * 注意：这个和拍照出题公用Adapter，所以这里判断是否是错题集入口分别进入拍照出题/题库出题
 */
public class RVQuestionInfoAnswerAdapter extends RecyclerView.Adapter<RVQuestionInfoAnswerAdapter.ViewHolder> {
    private static final String TAG = "RVQuestionTvAnswerAdapter";
    private Context mContext;

    //0未提交  1 已提交  2 已批阅
    private String taskStatus;

    private List<QuestionBank> questionInfoList;


    /**
     * 如果homeworkid为空字符串表示没有homeworkid，在QuestionInfo中存在homeworkid
     */
    public void setQuestionInfoList(List<QuestionBank> questionInfoList) {
        //这个是提真正的数据
        this.questionInfoList = questionInfoList;


    }

    /**
     * @param status          作业的状态

     */
    public RVQuestionInfoAnswerAdapter(Context context, String status) {
        mContext = context;
        taskStatus = status;



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

        viewHolder.item_practice_question.setQuestionInfo(questionInfoList.get(i), i, taskStatus, false);


    }
    @Override
    public int getItemCount() {
        return questionInfoList != null ? questionInfoList.size() : 0;
    }


    protected class ViewHolder extends RecyclerView.ViewHolder{

        private NewKnowledgeQuestionView item_practice_question;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_practice_question = itemView.findViewById(R.id.item_practice_question);
        }
    }


}
