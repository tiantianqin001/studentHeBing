package com.telit.zhkt_three.Adapter.NewKnowQuestion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewFillBlankTwoAdapter extends RecyclerView.Adapter<NewFillBlankTwoAdapter.ViewHolder> {
    private Context mContext;
    private QuestionBank questionBank;
    private String status;

    private List<Integer> quints = new ArrayList<>();

    public NewFillBlankTwoAdapter(Context mContext, QuestionBank questionBank, String status) {

        this.mContext = mContext;
        this.questionBank = questionBank;
        this.status = status;
        String ItemBankTitle = questionBank.getQuestionText();
        //使用"^__\\d+__$"不行
        String reg = "__\\d+__";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(ItemBankTitle);
        int i = -1;

        //根据正则判断有几道题
        while (matcher.find()) {
            i++;
            quints.add(i);
        }
        if (status.equals(Constant.Todo_Status) || status.equals(Constant.Retry_Status)) {

        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fill_blank_option_complete_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fill_blank_option.setText(String.valueOf(quints.get(i) + 1));
    }
    @Override
    public int getItemCount() {
        return quints.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fill_blank_option;
        public EditText fill_blank_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fill_blank_option = itemView.findViewById(R.id.fill_blank_option);
            fill_blank_content = itemView.findViewById(R.id.fill_blank_content);
        }
    }
}
