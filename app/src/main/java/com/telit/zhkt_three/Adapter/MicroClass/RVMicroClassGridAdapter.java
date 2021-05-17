package com.telit.zhkt_three.Adapter.MicroClass;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.JavaBean.MicroClass.ActualMicBean;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/10 8:47
 * <p>
 * 课堂记录类型：随堂练习、投票、分组讨论以及抢答
 */
public class RVMicroClassGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<ActualMicBean> mDatas;

    public RVMicroClassGridAdapter(Context context, List<ActualMicBean> list) {
        mContext = context;
        mDatas = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVClassRecordGridViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_micro_rv, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RVClassRecordGridViewHolder) {
            RVClassRecordGridViewHolder rvClassRecordGridViewHolder = (RVClassRecordGridViewHolder) viewHolder;

            Glide.with(mContext).load(mDatas.get(i).getThumbnail())
                    .placeholder(R.mipmap.image_video_default)
                    .error(R.mipmap.image_video_default)
                    .into(rvClassRecordGridViewHolder.micro_class_item_imgBg);

            rvClassRecordGridViewHolder.micro_class_item_tv_date.setText(mDatas.get(i).getFileName());

            rvClassRecordGridViewHolder.micro_class_item_tv_size.setText(QZXTools.transformBytes(mDatas.get(i).getSize()));

            rvClassRecordGridViewHolder.micro_class_item_tv_topic.setText(mDatas.get(i).getResourceName());

            rvClassRecordGridViewHolder.micro_class_item_ratingBar.setRating(mDatas.get(i).getScore()/2f);

        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    /**
     * 视频、音频和图片
     */
    public class RVClassRecordGridViewHolder extends RecyclerView.ViewHolder {

        private ImageView micro_class_item_imgBg;
        private TextView micro_class_item_tv_date;
        private TextView micro_class_item_tv_topic;
        private RatingBar micro_class_item_ratingBar;
        private TextView micro_class_item_tv_size;

        public RVClassRecordGridViewHolder(@NonNull View itemView) {
            super(itemView);
            micro_class_item_imgBg = itemView.findViewById(R.id.micro_class_item_imgBg);
            micro_class_item_tv_date = itemView.findViewById(R.id.micro_class_item_tv_date);
            micro_class_item_tv_size = itemView.findViewById(R.id.micro_class_item_tv_size);
            micro_class_item_tv_topic = itemView.findViewById(R.id.micro_class_item_tv_topic);
            micro_class_item_ratingBar = itemView.findViewById(R.id.micro_class_item_ratingBar);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent_video = new Intent(mContext, VideoPlayerActivity.class);
                    intent_video.putExtra("VideoFilePath", mDatas.get(getLayoutPosition()).getPreviewUrl());
                    intent_video.putExtra("VideoTitle", mDatas.get(getLayoutPosition()).getFileName());
                    intent_video.putExtra("VideoThumbnail", mDatas.get(getLayoutPosition()).getThumbnail());
                    intent_video.putExtra("currentVideo",mDatas.get(getLayoutPosition()).getCreateDate());
                    mContext.startActivity(intent_video);
                }
            });
        }
    }
}
