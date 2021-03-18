package com.telit.zhkt_three.Adapter.ClassRecord;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.ClassRecord.RecordDiscussShowActivity;
import com.telit.zhkt_three.Activity.ClassRecord.RecordPracticeDoneActivity;
import com.telit.zhkt_three.Activity.ClassRecord.RecordVoteResultActivity;
import com.telit.zhkt_three.JavaBean.ClassRecord.ClassRecord;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/10 8:47
 * <p>
 * 课堂记录类型：随堂练习、投票、分组讨论以及抢答
 */
public class RVClassRecordGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ClassRecord> mDatas;

    public RVClassRecordGridAdapter(Context context, List<ClassRecord> list) {
        mContext = context;
        mDatas = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVClassRecordGridViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.rv_item_class_record_grid_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RVClassRecordGridViewHolder) {
            RVClassRecordGridViewHolder rvClassRecordGridViewHolder = (RVClassRecordGridViewHolder) viewHolder;
            String type = mDatas.get(i).getType();
            switch (type) {
                case "2":
                    //分组讨论
                    rvClassRecordGridViewHolder.class_record_grid_layout.setBackgroundResource(R.mipmap.taolun);
                    break;
                case "3":
                    //随堂练习
                    rvClassRecordGridViewHolder.class_record_grid_layout.setBackgroundResource(R.mipmap.lianxi);
                    break;
                case "1":
                    //投票
                    rvClassRecordGridViewHolder.class_record_grid_layout.setBackgroundResource(R.mipmap.toupiao);
                    break;
                case "0":
                    //抢答
                    rvClassRecordGridViewHolder.class_record_grid_layout.setBackgroundResource(R.mipmap.qiangda);
                    break;
            }

            rvClassRecordGridViewHolder.class_record_grid_title.setText(mDatas.get(i).getTitle());

            rvClassRecordGridViewHolder.class_record_grid_time.setText(mDatas.get(i).getDate());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    /**
     * 视频、音频和图片
     */
    public class RVClassRecordGridViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //换背景色
        private RelativeLayout class_record_grid_layout;
        private TextView class_record_grid_time;
        private TextView class_record_grid_title;

        public RVClassRecordGridViewHolder(@NonNull View itemView) {
            super(itemView);
            class_record_grid_layout = itemView.findViewById(R.id.class_record_grid_layout);
            class_record_grid_time = itemView.findViewById(R.id.class_record_grid_time);
            class_record_grid_title = itemView.findViewById(R.id.class_record_grid_title);

            class_record_grid_layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.class_record_grid_layout:
                    switch (mDatas.get(getLayoutPosition()).getType()) {
                        case "1":
                            //投票
                            Intent intent_vote = new Intent(mContext, RecordVoteResultActivity.class);
                            intent_vote.putExtra("vote_id", mDatas.get(getLayoutPosition()).getId());
                            mContext.startActivity(intent_vote);
                            break;
                        case "2":
                            //分组讨论
                            Intent intent_discuss = new Intent(mContext, RecordDiscussShowActivity.class);
                            intent_discuss.putExtra("discuss_id", mDatas.get(getLayoutPosition()).getId());
                            mContext.startActivity(intent_discuss);
                            break;
                        case "3":
                            //随堂练习
                            Intent intent_practice = new Intent(mContext, RecordPracticeDoneActivity.class);
                            intent_practice.putExtra("practice_id", mDatas.get(getLayoutPosition()).getId());
                            mContext.startActivity(intent_practice);
                            break;
                    }
                    break;
            }
        }
    }
}
