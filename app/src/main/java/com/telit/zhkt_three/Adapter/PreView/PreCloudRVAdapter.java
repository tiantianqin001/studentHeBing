package com.telit.zhkt_three.Adapter.PreView;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.PreView.PreViewDisplayBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/20 15:28
 */
public class PreCloudRVAdapter extends RecyclerView.Adapter<PreCloudRVAdapter.PreCloudViewHolder> {
    private Context mContext;
    private List<PreViewDisplayBean> mDatas;

    private String flag;//1、班级分享 2、收藏资源

    public PreCloudRVAdapter(Context context, List<PreViewDisplayBean> preViewDiaplayBeans,String flag) {
        mContext = context;
        mDatas = preViewDiaplayBeans;
        this.flag = flag;
    }

    @NonNull
    @Override
    public PreCloudViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PreCloudViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_pre_cloud, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PreCloudViewHolder preCloudViewHolder, int position) {
        preCloudViewHolder.setIsRecyclable(false);

        //名称
        String fileName = mDatas.get(position).getFileName();
        if (!TextUtils.isEmpty(fileName)) {
            preCloudViewHolder.pre_item_cloud_tv_topic.setText(fileName);
        }

        //日期
        if ("1".equals(flag)){
            if (!TextUtils.isEmpty(mDatas.get(position).getCreateDate())) {
                preCloudViewHolder.pre_item_cloud_tv_date.setText(mDatas.get(position).getCreateDate());
            }
        }else if ("2".equals(flag)){
            if (!TextUtils.isEmpty(mDatas.get(position).getCollectionTime())) {
                preCloudViewHolder.pre_item_cloud_tv_date.setText(mDatas.get(position).getCollectionTime());
            }
        }

        //评分（5分制）给的是十分制的
        float avgStar = mDatas.get(position).getAvgStar() / 2f;
        preCloudViewHolder.pre_item_cloud_ratingBar.setRating(avgStar);

        //0 文件 1文件夹
        int type = mDatas.get(position).getType();
        if (type == 0) {
            preCloudViewHolder.pre_item_cloud_ratingBar.setVisibility(View.VISIBLE);
            //如果已经评价过就不在评价

            preCloudViewHolder.pre_item_cloud_tv_go_study.setVisibility(View.VISIBLE);
            if (mDatas.get(position).getStatus().equals("0")){
                preCloudViewHolder.pre_item_cloud_tv_go_study.setText("去学习");
            }else {
                preCloudViewHolder.pre_item_cloud_tv_go_study.setText("已学习");
            }
            String format = mDatas.get(position).getFileFormat().toLowerCase();
            preCloudViewHolder.pre_item_cloud_icon.setVisibility(View.VISIBLE);
            preCloudViewHolder.pre_item_cloud_img_video.setVisibility(View.GONE);
            if (format.equals("mp4") || format.equals("avi")) {
                //显示视频图标
                preCloudViewHolder.pre_item_cloud_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_video_bg);
                preCloudViewHolder.pre_item_cloud_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.video_text_color));
                preCloudViewHolder.pre_item_cloud_tv_colorType.setText("视频");
                preCloudViewHolder.pre_item_cloud_img_video.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(mDatas.get(position).getThumbnail())) {
                    Glide.with(mContext).load(mDatas.get(position).getThumbnail())
                            .placeholder(R.mipmap.image_video_default)
                            .error(R.mipmap.image_video_default)
                            .into(preCloudViewHolder.pre_item_cloud_imgBg);
                } else {
                    preCloudViewHolder.pre_item_cloud_imgBg.setImageResource(R.mipmap.image_video_default);
                }
                preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.video);
            } else if (format.equals("mp3")) {
                preCloudViewHolder.pre_item_cloud_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_audio_bg);
                preCloudViewHolder.pre_item_cloud_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.audio_text_color));
                preCloudViewHolder.pre_item_cloud_tv_colorType.setText("音频");
                preCloudViewHolder.pre_item_cloud_imgBg.setImageResource(R.mipmap.audio_bg);
                preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.voice);
            } else if (format.equals("jpg") || format.equals("png") || format.equals("gif")) {
                preCloudViewHolder.pre_item_cloud_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_pic_bg);
                preCloudViewHolder.pre_item_cloud_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.picture_text_color));
                preCloudViewHolder.pre_item_cloud_tv_colorType.setText("图片");
                preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.picture);
                //图片直接用预览地址
                Glide.with(mContext).load(mDatas.get(position).getPreviewUrl())
                        .placeholder(R.mipmap.pic_bg)
                        .error(R.mipmap.pic_bg)
                        .into(preCloudViewHolder.pre_item_cloud_imgBg);
            } else {
                preCloudViewHolder.pre_item_cloud_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_homework_bg);
                preCloudViewHolder.pre_item_cloud_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.homework_text_color));
                preCloudViewHolder.pre_item_cloud_imgBg.setImageResource(R.mipmap.homework_bg);
                if (format.equals("pptx") || format.equals("ppt")) {
                    preCloudViewHolder.pre_item_cloud_tv_colorType.setText("ppt");
                    preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.ppt);
                } else if (format.equals("doc") || format.equals("docx") || format.equals("txt")) {
                    preCloudViewHolder.pre_item_cloud_tv_colorType.setText("word");
                    preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.word);
                } else if (format.equals("xls") || format.equals("xlsx")) {
                    preCloudViewHolder.pre_item_cloud_tv_colorType.setText("excel");
                    preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.excel);
                } else if (format.equals("pdf")) {
                    preCloudViewHolder.pre_item_cloud_tv_colorType.setText("pdf");
                    preCloudViewHolder.pre_item_cloud_icon.setImageResource(R.mipmap.pdf);
                } else if (format.equals("swf")) {
                    preCloudViewHolder.pre_item_cloud_icon.setVisibility(View.GONE);
                    preCloudViewHolder.pre_item_cloud_tv_colorType.setText("flash");
                }
            }
        } else {
            preCloudViewHolder.pre_item_cloud_img_video.setVisibility(View.GONE);
            preCloudViewHolder.pre_item_cloud_ratingBar.setVisibility(View.GONE);
            //文件夹 homework_bg
            preCloudViewHolder.pre_item_cloud_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_homework_bg);
            preCloudViewHolder.pre_item_cloud_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.homework_text_color));
            preCloudViewHolder.pre_item_cloud_tv_colorType.setText("文件夹");
            preCloudViewHolder.pre_item_cloud_imgBg.setImageResource(R.mipmap.homework_bg);
            preCloudViewHolder.pre_item_cloud_icon.setVisibility(View.GONE);
        }

        if ("1".equals(mDatas.get(position).getCollectionState())){
            preCloudViewHolder.iv_collection.setImageResource(R.mipmap.collect_red_icon);
        }else {
            preCloudViewHolder.iv_collection.setImageResource(R.mipmap.collect_gray_icon);
        }

        //收藏的点击
        preCloudViewHolder.iv_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemCollectionClickListener!=null){
                    onItemCollectionClickListener.onItemCollectionClickListener(preCloudViewHolder,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public class PreCloudViewHolder extends RecyclerView.ViewHolder {

        private ImageView pre_item_cloud_imgBg;
        private ImageView pre_item_cloud_icon;
        private TextView pre_item_cloud_tv_date;
        private ImageView pre_item_cloud_img_video;
        private RatingBar pre_item_cloud_ratingBar;
        private TextView pre_item_cloud_tv_topic;
        private TextView pre_item_cloud_tv_colorType;
        private TextView pre_item_cloud_tv_go_study;
        private ImageView iv_collection;

        public PreCloudViewHolder(@NonNull View itemView) {
            super(itemView);
            pre_item_cloud_imgBg = itemView.findViewById(R.id.pre_item_cloud_imgBg);
            pre_item_cloud_icon = itemView.findViewById(R.id.pre_item_cloud_icon);
            pre_item_cloud_tv_date = itemView.findViewById(R.id.pre_item_cloud_tv_date);
            pre_item_cloud_img_video = itemView.findViewById(R.id.pre_item_cloud_img_video);
            pre_item_cloud_ratingBar = itemView.findViewById(R.id.pre_item_cloud_ratingBar);
            pre_item_cloud_tv_topic = itemView.findViewById(R.id.pre_item_cloud_tv_topic);
            pre_item_cloud_tv_colorType = itemView.findViewById(R.id.pre_item_cloud_tv_colorType);
            pre_item_cloud_tv_go_study = itemView.findViewById(R.id.pre_item_cloud_tv_go_study);
            iv_collection = itemView.findViewById(R.id.iv_collection);

            //点击ItemView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //下载或者预览 视频/音频/图片/office/txt/文件夹
                    EventBus.getDefault().post(mDatas.get(getLayoutPosition()), Constant.CLICK_CLOUD_ITEM);
                }
            });

            //去学习查看评价的点击事件
            pre_item_cloud_tv_go_study.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //查看评价
                    PreViewDisplayBean preViewDisplayBean = mDatas.get(getLayoutPosition());
                    preViewDisplayBean.setCurPosition(getLayoutPosition());

                    EventBus.getDefault().post(preViewDisplayBean, Constant.click_cloud_item_ping_jia);
                }
            });
        }
    }

    public interface OnItemCollectionClickListener {
        void onItemCollectionClickListener(PreCloudViewHolder holder, int position);
    }

    private OnItemCollectionClickListener onItemCollectionClickListener;

    public void setOnItemCollectionClickListener(OnItemCollectionClickListener listener) {
        this.onItemCollectionClickListener = listener;
    }

    public List<PreViewDisplayBean> getmDatas() {
        return mDatas;
    }

    public void setmDatas(List<PreViewDisplayBean> mDatas) {
        this.mDatas = mDatas;
        notifyDataSetChanged();
    }
}
