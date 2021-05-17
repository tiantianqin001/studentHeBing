package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewFillBlankTwoAdapter;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewMulitChooseTwoAdapter;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewSingleChooseTwoAdapter;
import com.telit.zhkt_three.Adapter.NewKnowQuestion.NewSubjectTwoAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.List;

public class NewKnowledgeTwoAdapter extends RecyclerView.Adapter {

    private QuestionBank questionBank;
    private Context mContext;
    private String status;
    private int type;
    private NewSubjectTwoAdapter newSubjectAdapter;
    private final List<QuestionBank> questionBankList;

    public NewKnowledgeTwoAdapter(QuestionBank questionBank, Context context, String status, int type) {
        this.questionBank = questionBank;
        questionBankList = questionBank.getQuestionBanks();

        this.mContext = context;
        this.status = status;
        this.type = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (type == Constant.Single_Choose) {
            return new SingleChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        } else if (type == Constant.Fill_Blank) {
            return new FillBlankHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, null, false));
        } else if (questionBank.getQuestionChannelType() == Constant.Subject_Item) {
            return new SubjectItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        } else if (type == Constant.Judge_Item) {
            return new JudgeItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        } else if (type == Constant.Multi_Choose) {
            return new MultiChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view_new_knowledge, viewGroup, false));
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SingleChooseHolder) {
            ((SingleChooseHolder) viewHolder). Item_Bank_list_title_content.setVisibility(View.VISIBLE);
            //题目信息
            String ItemBankTitle = questionBankList.get(i).getQuestionText();
            ((SingleChooseHolder) viewHolder). Item_Bank_list_title_content.setHtml("(" + (i + 1) + ")" + ItemBankTitle,
                    new HtmlHttpImageGetter(((SingleChooseHolder) viewHolder).Item_Bank_list_title_content));
            ((SingleChooseHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewSingleChooseTwoAdapter newSingleChooseAdapter=new NewSingleChooseTwoAdapter(mContext,questionBankList.get(i),status);
            ((SingleChooseHolder) viewHolder).rv_new_knowledge.setAdapter(newSingleChooseAdapter);
        }else if (viewHolder instanceof JudgeItemHolder){
            ((JudgeItemHolder) viewHolder). Item_Bank_list_title_content.setVisibility(View.VISIBLE);
            //题目信息
            String ItemBankTitle = questionBankList.get(i).getQuestionText();
            ((JudgeItemHolder) viewHolder). Item_Bank_list_title_content.setHtml("(" + (i + 1) + ")" + ItemBankTitle,
                    new HtmlHttpImageGetter(((JudgeItemHolder) viewHolder).Item_Bank_list_title_content));
            ((JudgeItemHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewSingleChooseTwoAdapter newSingleChooseAdapter=new NewSingleChooseTwoAdapter(mContext,questionBankList.get(i),status);
            ((JudgeItemHolder) viewHolder).rv_new_knowledge.setAdapter(newSingleChooseAdapter);
        }else if (viewHolder instanceof FillBlankHolder){
            ((FillBlankHolder) viewHolder). Item_Bank_list_title_content.setVisibility(View.VISIBLE);
            //题目信息
            String ItemBankTitle = questionBankList.get(i).getQuestionText();
            ((FillBlankHolder) viewHolder). Item_Bank_list_title_content.setHtml("(" + (i + 1) + ")" + ItemBankTitle,
                    new HtmlHttpImageGetter(((FillBlankHolder) viewHolder).Item_Bank_list_title_content));

            ((FillBlankHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewFillBlankTwoAdapter newFillBlankAdapter=new NewFillBlankTwoAdapter(mContext,questionBankList.get(i),status);
            ((FillBlankHolder) viewHolder).rv_new_knowledge.setAdapter(newFillBlankAdapter);
        }else if (viewHolder instanceof MultiChooseHolder){
            //题目信息
            ((MultiChooseHolder) viewHolder). Item_Bank_list_title_content.setVisibility(View.VISIBLE);
            String ItemBankTitle = questionBankList.get(i).getQuestionText();
            ((MultiChooseHolder) viewHolder). Item_Bank_list_title_content.setHtml("(" + (i + 1) + ")" + ItemBankTitle,
                    new HtmlHttpImageGetter(((MultiChooseHolder) viewHolder).Item_Bank_list_title_content));

            ((MultiChooseHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            NewMulitChooseTwoAdapter newMulitChooseAdapter=new NewMulitChooseTwoAdapter(mContext,questionBankList.get(i),status);
            ((MultiChooseHolder) viewHolder).rv_new_knowledge.setAdapter(newMulitChooseAdapter);
        }else if (viewHolder instanceof SubjectItemHolder){
            //题目信息
            ((SubjectItemHolder) viewHolder). Item_Bank_list_title_content.setVisibility(View.VISIBLE);
            String ItemBankTitle = questionBankList.get(i).getQuestionText();
            ((SubjectItemHolder) viewHolder). Item_Bank_list_title_content.setHtml("(" + (i + 1) + ")" + ItemBankTitle,
                    new HtmlHttpImageGetter(((SubjectItemHolder) viewHolder).Item_Bank_list_title_content));

            ((SubjectItemHolder) viewHolder).rv_new_knowledge.setLayoutManager(new LinearLayoutManager(mContext));
            newSubjectAdapter = new NewSubjectTwoAdapter(mContext,questionBankList.get(i),status);
            ((SubjectItemHolder) viewHolder).rv_new_knowledge.setAdapter(newSubjectAdapter);
        }
    }

    @Override
    public int getItemCount() {
        QZXTools.logE("newKonw"+"......"+"getItemCount",null);

        return questionBankList.size();
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
        private HtmlTextView Item_Bank_list_title_content;
        public SingleChooseHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
            Item_Bank_list_title_content = itemView.findViewById(R.id.Item_Bank_list_title_content);
        }
    }

    public class MultiChooseHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        private HtmlTextView Item_Bank_list_title_content;
        public MultiChooseHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
            Item_Bank_list_title_content = itemView.findViewById(R.id.Item_Bank_list_title_content);
        }
    }
    public class SubjectItemHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        private HtmlTextView Item_Bank_list_title_content;
        public SubjectItemHolder(@NonNull View itemView) {
            super(itemView);

            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
            Item_Bank_list_title_content = itemView.findViewById(R.id.Item_Bank_list_title_content);
        }
    }

    //填空题
    public class FillBlankHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        private HtmlTextView Item_Bank_list_title_content;
        public FillBlankHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
            Item_Bank_list_title_content = itemView.findViewById(R.id.Item_Bank_list_title_content);
        }
    }

    public class JudgeItemHolder extends RecyclerView.ViewHolder {
        private final RecyclerView rv_new_knowledge;
        private HtmlTextView Item_Bank_list_title_content;
        public JudgeItemHolder(@NonNull View itemView) {
            super(itemView);
            rv_new_knowledge = itemView.findViewById(R.id.rv_new_knowledge);
            Item_Bank_list_title_content = itemView.findViewById(R.id.Item_Bank_list_title_content);
        }
    }
}
