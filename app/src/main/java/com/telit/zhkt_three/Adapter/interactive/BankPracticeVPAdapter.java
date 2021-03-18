package com.telit.zhkt_three.Adapter.interactive;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/26 9:22
 * <p>
 * 题库的提交VP
 * <p>
 * todo 如何判断一次作业中学生全部作答了所有的题目，即该连线的连线，该选择的选择，该填空的填空，不空白
 */
public class BankPracticeVPAdapter extends PagerAdapter{
    private List<QuestionBank> mDatas;
    private Activity mContext;

    private String status = "0";

    public void setStatus(String status) {
        this.status = status;
    }

    public BankPracticeVPAdapter(Activity context, List<QuestionBank> list) {
        mDatas = list;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_bank_practice_item_layout, container, false);
        NewKnowledgeQuestionView newKnowledgeQuestionView = view.findViewById(R.id.item_practice_question);

        newKnowledgeQuestionView.setQuestionInfo(mDatas.get(position), position, status, false);
        newKnowledgeQuestionView.setOnCollectClickListener(new NewKnowledgeQuestionView.OnCollectClickListener() {
            @Override
            public void OnCollectClickListener(QuestionBank questionBank, int curPosition) {
                if (onCollectClickListener!=null){
                    onCollectClickListener.OnCollectClickListener(newKnowledgeQuestionView,questionBank,curPosition);
                }
            }
        });

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    private OnCollectClickListener onCollectClickListener;

    public interface OnCollectClickListener {
        void OnCollectClickListener(NewKnowledgeQuestionView newKnowledgeQuestionView,QuestionBank questionBank,int curPosition);
    }

    public void setOnCollectClickListener(OnCollectClickListener onCollectClickListener) {
        this.onCollectClickListener = onCollectClickListener;
    }
}
