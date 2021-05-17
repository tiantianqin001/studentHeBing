package com.telit.zhkt_three.Adapter.AfterHomeWork;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/1/18 18:47
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class HomeworkQuestionExportAdapter extends RecyclerView.Adapter<HomeworkQuestionExportAdapter.ViewHolder> {
    private Context context;
    private List<QuestionBank> list;

    public HomeworkQuestionExportAdapter(Context context, List<QuestionBank> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public HomeworkQuestionExportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_homework_question_export, parent, false);
        return new HomeworkQuestionExportAdapter.ViewHolder(inflate);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        QuestionBank questionBank = list.get(position);

        holder.tv_question.setText(getType(position,questionBank.getQuestionChannelType())+questionBank.getTitle());

        Drawable leftDrawable;
        if (questionBank.isChecked()){
            leftDrawable = context.getResources().getDrawable(R.mipmap.contact_checked_icon);
        }else {
            leftDrawable = context.getResources().getDrawable(R.mipmap.contact_unchecked_icon);
        }
        leftDrawable.setBounds(0, 0, leftDrawable.getMinimumWidth(), leftDrawable.getMinimumHeight());
        holder.tv_question.setCompoundDrawables(leftDrawable, null, null, null);

        holder.tv_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onCheckListener!=null){
                    onCheckListener.OnCheckListener(position);
                }
            }
        });

        holder.itemView.setId(position);
    }

    private String getType(int curPosition,int questionType){
        String type = "";
        switch (questionType) {
            case Constant.Single_Choose:
                type = (curPosition + 1) + ".【单选题】";
                break;
            case Constant.Multi_Choose:
                type = (curPosition + 1) + ".【多选题】";
                break;
            case Constant.Fill_Blank:
                type = (curPosition + 1) + ".【填空题】";
                break;
            case Constant.Subject_Item:
                type = (curPosition + 1) + ".【主观题】";
                break;
            case Constant.Linked_Line:
                type = (curPosition + 1) + ".【连线题】";
                break;
            case Constant.Judge_Item:
                type = (curPosition + 1) + ".【判断题】";
                break;
        }
        return type;
    }

    @Override
    public int getItemCount() {
        if(list == null) {
            return  0;
        }
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_question;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_question = itemView.findViewById(R.id.tv_question);
        }
    }

    private OnCheckListener onCheckListener;
    public void setOnCheckListener(OnCheckListener onCheckListener) {
        this.onCheckListener = onCheckListener;
    }
    public interface OnCheckListener {
        void OnCheckListener(int childPosition);
    }
}