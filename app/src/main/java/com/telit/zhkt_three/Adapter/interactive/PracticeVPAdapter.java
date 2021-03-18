package com.telit.zhkt_three.Adapter.interactive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.CustomView.QuestionView.TotalQuestionView;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/26 9:22
 * <p>
 * 注意：这里的提交状态写死的为0：要做状态
 */
public class PracticeVPAdapter extends PagerAdapter {
    private List<QuestionInfo> mDatas;
    private Context mContext;

    private String status = "0";

    public void setStatus(String status) {
        this.status = status;
    }

    public PracticeVPAdapter(Context context, List<QuestionInfo> list) {
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
        View view = LayoutInflater.from(mContext).inflate(R.layout.rv_practice_item_layout, container, false);
        TotalQuestionView totalQuestionView = view.findViewById(R.id.item_practice_question);
        //额外设置练习类型
        totalQuestionView.setPracticeType(true);
        totalQuestionView.setQuestionInfo(mDatas.size(), position, status, false, false, mDatas.get(position));
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
