package com.telit.zhkt_three.Adapter.AfterHomeWork;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.AfterHomeWork.CollectQuestionDetailActivity;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/4 15:18
 * <p>
 * 收藏题目的适配器
 * <p>
 * 如果采用R.layout.rv_after_homework_item_layout_two，嵌套了一层线性RecyclerView的话XRecyclerView的下拉显示有问题？
 */
public class CollectQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<QuestionBank> datas;

    public CollectQuestionAdapter(Context context, List<QuestionBank> list) {
        mContext = context;
        datas = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CollectQuestionViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.collect_question_item_layout, viewGroup, false));
    }

    /**
     * 从前到后没有问题，但是回拉即从后往前就会显示出bug
     * 解决方式：预先处理Datas而不是临时判断
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof CollectQuestionViewHolder) {
            CollectQuestionViewHolder collectQuestionViewHolder = (CollectQuestionViewHolder) viewHolder;
            QuestionBank collectQuestionBean = datas.get(i);

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("收藏日期：");
            String endDate = collectQuestionBean.getCreateTime();
            endDate = endDate.replace("-", "/");
            stringBuilder.append(endDate);
            collectQuestionViewHolder.after_homework_tv_commit_date.setText(stringBuilder.toString());

            String subject = collectQuestionBean.getSubjectId();
            if (!TextUtils.isEmpty(subject)) {
                collectQuestionViewHolder.after_homework_img_subject.setImageResource(
                        UserUtils.getSubjectIcon(Integer.parseInt(subject)));
            }

            collectQuestionViewHolder.after_homework_tv_desc.setText(collectQuestionBean.getCollectTitle());

            collectQuestionViewHolder.after_homework_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (QZXTools.canClick()) {
                        /**
                         * 进入收藏详情做作业
                         *   作业的点击事件
                         * 以前是区分是否todo来进入报告，暂时有问题改为都进入作业详情，然后可以查看报告
                         * */
                        Intent intent = new Intent(mContext, CollectQuestionDetailActivity.class);
                        intent.putExtra("byHand", "2");
                        intent.putExtra("status", "1");
                        intent.putExtra("curPageIndex", i);
                        intent.putParcelableArrayListExtra("questionBanks", (ArrayList<? extends Parcelable>) datas);
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public class CollectQuestionViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout after_homework_item_layout;
        private ImageView after_homework_img_subject;
        private TextView after_homework_tv_desc;
        private TextView after_homework_tv_commit_date;

        public CollectQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            after_homework_item_layout = itemView.findViewById(R.id.after_homework_item_layout);
            after_homework_img_subject = itemView.findViewById(R.id.after_homework_img_subject);
            after_homework_tv_desc = itemView.findViewById(R.id.after_homework_tv_desc);
            after_homework_tv_commit_date = itemView.findViewById(R.id.after_homework_tv_commit_date);
        }
    }
}
