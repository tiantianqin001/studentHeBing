package com.telit.zhkt_three.Adapter.Mistake;

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

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.JavaBean.MistakesCollection.PerfectRightBean;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.R;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2020/4/8 15:26
 */
public class PerfectAnswerRightAdapter extends RecyclerView.Adapter<PerfectAnswerRightAdapter.PerfectAnswerRightViewHolder> {

    private Context context;
    private List<PerfectRightBean> perfectRightBeans;

    public PerfectAnswerRightAdapter(Context context, List<PerfectRightBean> perfectRightBeans) {
        this.context = context;
        this.perfectRightBeans = perfectRightBeans;
    }

    @NonNull
    @Override
    public PerfectAnswerRightViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PerfectAnswerRightViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_perfect_right_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PerfectAnswerRightViewHolder perfectAnswerRightViewHolder, int i) {
        if (!TextUtils.isEmpty(perfectRightBeans.get(i).getPhotoUrl())) {
            Glide.with(context).load(perfectRightBeans.get(i).getPhotoUrl())
                    .placeholder(R.mipmap.icon_user)
                    .error(R.mipmap.icon_user)
                    .into(perfectAnswerRightViewHolder.perfect_avatar);
        }

        //文本答案
        if (TextUtils.isEmpty(perfectRightBeans.get(i).getTxtAnswer())) {
            perfectAnswerRightViewHolder.perfect_txt_relative.setVisibility(View.GONE);
        } else {
            perfectAnswerRightViewHolder.perfect_txt_relative.setVisibility(View.VISIBLE);
            perfectAnswerRightViewHolder.perfect_answer_txt_text.setText(perfectRightBeans.get(i).getTxtAnswer());
        }

        //图片答案
        List<String> imgList = perfectRightBeans.get(i).getImgLists();
        if (imgList == null || imgList.size() <= 0) {
            perfectAnswerRightViewHolder.perfect_img_relative.setVisibility(View.GONE);
        } else {
            perfectAnswerRightViewHolder.perfect_img_relative.setVisibility(View.VISIBLE);
            for (int j = 0; j < imgList.size(); j++) {
                switch (j) {
                    case 0:
                        if (!TextUtils.isEmpty(imgList.get(j))) {
                            perfectAnswerRightViewHolder.perfect_answer_pic_one.setVisibility(View.VISIBLE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_two.setVisibility(View.GONE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_three.setVisibility(View.GONE);
                            Glide.with(context).load(imgList.get(j))
                                    .placeholder(R.mipmap.icon_user)
                                    .error(R.mipmap.icon_user)
                                    .into(perfectAnswerRightViewHolder.perfect_answer_pic_one);
                        } else {
                            perfectAnswerRightViewHolder.perfect_answer_pic_one.setVisibility(View.GONE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_two.setVisibility(View.GONE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_three.setVisibility(View.GONE);
                        }
                        break;
                    case 1:
                        if (!TextUtils.isEmpty(imgList.get(j))) {
                            perfectAnswerRightViewHolder.perfect_answer_pic_one.setVisibility(View.VISIBLE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_two.setVisibility(View.VISIBLE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_three.setVisibility(View.GONE);
                            Glide.with(context).load(imgList.get(j))
                                    .placeholder(R.mipmap.icon_user)
                                    .error(R.mipmap.icon_user)
                                    .into(perfectAnswerRightViewHolder.perfect_answer_pic_two);
                        } else {
                            perfectAnswerRightViewHolder.perfect_answer_pic_two.setVisibility(View.GONE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_three.setVisibility(View.GONE);
                        }
                        break;
                    case 2:
                        if (!TextUtils.isEmpty(imgList.get(j))) {
                            perfectAnswerRightViewHolder.perfect_answer_pic_one.setVisibility(View.VISIBLE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_two.setVisibility(View.VISIBLE);
                            perfectAnswerRightViewHolder.perfect_answer_pic_three.setVisibility(View.VISIBLE);
                            Glide.with(context).load(imgList.get(j))
                                    .placeholder(R.mipmap.icon_user)
                                    .error(R.mipmap.icon_user)
                                    .into(perfectAnswerRightViewHolder.perfect_answer_pic_three);
                        } else {
                            perfectAnswerRightViewHolder.perfect_answer_pic_three.setVisibility(View.GONE);
                        }
                        break;
                }
            }

            perfectAnswerRightViewHolder.perfect_answer_pic_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageLookActivity.class);
                    intent.putStringArrayListExtra("imgResources", (ArrayList<String>) imgList);
                    intent.putExtra("NeedComment", false);
                    intent.putExtra("curImgIndex", 0);
                    context.startActivity(intent);
                }
            });
            perfectAnswerRightViewHolder.perfect_answer_pic_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageLookActivity.class);
                    intent.putStringArrayListExtra("imgResources", (ArrayList<String>) imgList);
                    intent.putExtra("NeedComment", false);
                    intent.putExtra("curImgIndex", 1);
                    context.startActivity(intent);
                }
            });
            perfectAnswerRightViewHolder.perfect_answer_pic_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageLookActivity.class);
                    intent.putStringArrayListExtra("imgResources", (ArrayList<String>) imgList);
                    intent.putExtra("NeedComment", false);
                    intent.putExtra("curImgIndex", 2);
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return perfectRightBeans != null ? perfectRightBeans.size() : 0;
    }

    public class PerfectAnswerRightViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView perfect_avatar;
        private ImageView perfect_answer_pic_one;
        private ImageView perfect_answer_pic_two;
        private ImageView perfect_answer_pic_three;
        private TextView perfect_answer_txt_text;

        private RelativeLayout perfect_img_relative;
        private RelativeLayout perfect_txt_relative;

        public PerfectAnswerRightViewHolder(@NonNull View itemView) {
            super(itemView);
            perfect_avatar = itemView.findViewById(R.id.perfect_avatar);
            perfect_answer_pic_one = itemView.findViewById(R.id.perfect_answer_pic_one);
            perfect_answer_pic_two = itemView.findViewById(R.id.perfect_answer_pic_two);
            perfect_answer_pic_three = itemView.findViewById(R.id.perfect_answer_pic_three);
            perfect_answer_txt_text = itemView.findViewById(R.id.perfect_answer_txt_text);

            perfect_img_relative = itemView.findViewById(R.id.perfect_img_relative);
            perfect_txt_relative = itemView.findViewById(R.id.perfect_txt_relative);
        }
    }
}
