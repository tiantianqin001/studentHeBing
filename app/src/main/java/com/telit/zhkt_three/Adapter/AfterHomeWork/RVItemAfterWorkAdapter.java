package com.telit.zhkt_three.Adapter.AfterHomeWork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.HomeWork.HomeWorkDetailActivity;
import com.telit.zhkt_three.JavaBean.AfterHomework.AfterHomeworkBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/7/1 15:39
 */
public class RVItemAfterWorkAdapter extends RecyclerView.Adapter<RVItemAfterWorkAdapter.RVItemAfterWorkViewHolder> {

    private Context mContext;
    private List<AfterHomeworkBean> mDatas;

    public RVItemAfterWorkAdapter(Context context, List<AfterHomeworkBean> list) {
        mContext = context;
        mDatas = list;
    }

    @NonNull
    @Override
    public RVItemAfterWorkViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVItemAfterWorkViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_after_homework_sub_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVItemAfterWorkViewHolder rvItemAfterWorkViewHolder, int i) {
        StringBuilder stringBuilder = new StringBuilder();

        if (mDatas.get(i).getStatus().equals("0")) {
            rvItemAfterWorkViewHolder.after_homework_tv_commit_date.setTextColor(0xFFFF7200);
            rvItemAfterWorkViewHolder.after_homework_tv_enter.setText("去完成");
            rvItemAfterWorkViewHolder.after_homework_tv_enter.setTextColor(0xFF008AFF);
            rvItemAfterWorkViewHolder.after_homework_img_enter.setImageResource(R.mipmap.todo_arrow);

            stringBuilder.append("提交截止日期：");
        } else {
            rvItemAfterWorkViewHolder.after_homework_tv_commit_date.setTextColor(0xFF93A5B9);
            rvItemAfterWorkViewHolder.after_homework_tv_enter.setText("查看报告");
            rvItemAfterWorkViewHolder.after_homework_tv_enter.setTextColor(0xFF93A5B9);
            rvItemAfterWorkViewHolder.after_homework_img_enter.setImageResource(R.mipmap.complete_arrow);

            stringBuilder.append("作业完成日期：");
        }

        String subject = mDatas.get(i).getSubjectId();
        if (!TextUtils.isEmpty(subject)) {
            rvItemAfterWorkViewHolder.after_homework_img_subject.setImageResource(
                    UserUtils.getSubjectIcon(Integer.parseInt(subject)));
        }

        rvItemAfterWorkViewHolder.after_homework_tv_desc.setText(mDatas.get(i).getName());

        String endDate = mDatas.get(i).getEndDate();
        endDate = endDate.replace("-", "/");
        stringBuilder.append(endDate);
        rvItemAfterWorkViewHolder.after_homework_tv_commit_date.setText(stringBuilder.toString());
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public class RVItemAfterWorkViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout after_homework_item_layout;
        private ImageView after_homework_img_subject;
        private TextView after_homework_tv_desc;
        private TextView after_homework_tv_commit_date;
        private TextView after_homework_tv_enter;
        private ImageView after_homework_img_enter;

        public RVItemAfterWorkViewHolder(@NonNull View view) {
            super(view);
            after_homework_item_layout = view.findViewById(R.id.after_homework_item_layout);
            after_homework_img_subject = view.findViewById(R.id.after_homework_img_subject);
            after_homework_tv_desc = view.findViewById(R.id.after_homework_tv_desc);
            after_homework_tv_commit_date = view.findViewById(R.id.after_homework_tv_commit_date);
            after_homework_tv_enter = view.findViewById(R.id.after_homework_tv_enter);
            after_homework_img_enter = view.findViewById(R.id.after_homework_img_enter);

            after_homework_item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (QZXTools.canClick()) {
                        /**
                         * 进入作业详情做作业
                         *
                         * 以前是区分是否todo来进入报告，暂时有问题改为都进入作业详情，然后可以查看报告
                         * */
                        Intent intent = new Intent(mContext, HomeWorkDetailActivity.class);
                        intent.putExtra("homeworkId", mDatas.get(getAdapterPosition()).getId());
                        intent.putExtra("status", mDatas.get(getAdapterPosition()).getStatus());
                        intent.putExtra("showAnswerDate",mDatas.get(getAdapterPosition()).getShowAnswerDate());
                        mContext.startActivity(intent);
                    }
                }
            });
        }
    }
}
