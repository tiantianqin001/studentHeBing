package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewFillBlankAdapter;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewMulitChooseAdapter;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewSingleChooseAdapter;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewSubjectAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

public class NewKnowledgeAdapter extends RecyclerView.Adapter {

    private QuestionBank questionBank;
    private Context mContext;
    private String status;
    private NewSubjectAdapter newSubjectAdapter;

    public NewKnowledgeAdapter(QuestionBank questionBank, Context context, String status) {
        this.questionBank = questionBank;

        this.mContext = context;
        this.status = status;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (questionBank.getQuestionChannelType() == Constant.Single_Choose) {
            return new SingleChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        } else if (questionBank.getQuestionChannelType() == Constant.Fill_Blank) {
            return new FillBlankHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, null, false));
        } else if (questionBank.getQuestionChannelType() == Constant.Subject_Item) {
            return new SubjectItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        } else if (questionBank.getQuestionChannelType() == Constant.Judge_Item) {
            return new JudgeItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        } else if (questionBank.getQuestionChannelType() == Constant.Multi_Choose) {
            return new MultiChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SingleChooseHolder) {
            ((SingleChooseHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewSingleChooseAdapter newSingleChooseAdapter=new NewSingleChooseAdapter(mContext,questionBank,status);
            ((SingleChooseHolder) viewHolder).rv_new_knowledge.setAdapter(newSingleChooseAdapter);
        }else if (viewHolder instanceof JudgeItemHolder){
            ((JudgeItemHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewSingleChooseAdapter newSingleChooseAdapter=new NewSingleChooseAdapter(mContext,questionBank,status);
            ((JudgeItemHolder) viewHolder).rv_new_knowledge.setAdapter(newSingleChooseAdapter);
        }else if (viewHolder instanceof FillBlankHolder){
            ((FillBlankHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewFillBlankAdapter newFillBlankAdapter=new NewFillBlankAdapter(mContext,questionBank,status);
            ((FillBlankHolder) viewHolder).rv_new_knowledge.setAdapter(newFillBlankAdapter);
        }else if (viewHolder instanceof MultiChooseHolder){
            ((MultiChooseHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewMulitChooseAdapter newMulitChooseAdapter=new NewMulitChooseAdapter(mContext,questionBank,status);
            ((MultiChooseHolder) viewHolder).rv_new_knowledge.setAdapter(newMulitChooseAdapter);
        }else if (viewHolder instanceof SubjectItemHolder){
            ((SubjectItemHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            newSubjectAdapter = new NewSubjectAdapter(mContext,questionBank,status);
            ((SubjectItemHolder) viewHolder).rv_new_knowledge.setAdapter(newSubjectAdapter);
        }

    }

    @Override
    public int getItemCount() {
        QZXTools.logE("newKonw"+"......"+"getItemCount",null);

        return 1;
    }

    public void fromCameraCallback(String flag) {
        if (newSubjectAdapter!=null){
            newSubjectAdapter.fromCameraCallback(flag);
        }

    }

    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
        if (newSubjectAdapter!=null){
            newSubjectAdapter.fromBoardCallback(extraInfoBean);
        }

    }

    public class SingleChooseHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        public SingleChooseHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
        }
    }

    public class MultiChooseHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        public MultiChooseHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
        }
    }
    public class SubjectItemHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;

        public SubjectItemHolder(@NonNull View itemView) {
            super(itemView);

            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
        }
    }

    //填空题
    public class FillBlankHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        public FillBlankHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
        }
    }

    public class JudgeItemHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        public JudgeItemHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
        }
    }
}
