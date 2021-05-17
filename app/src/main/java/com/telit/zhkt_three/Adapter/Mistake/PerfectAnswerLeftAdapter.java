package com.telit.zhkt_three.Adapter.Mistake;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.JavaBean.MistakesCollection.PerfectLeftBean;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2020/4/8 15:26
 */
public class PerfectAnswerLeftAdapter extends RecyclerView.Adapter<PerfectAnswerLeftAdapter.PerfectAnswerLeftViewHolder> {

    private Context context;
    private List<PerfectLeftBean> perfectLeftBeans;

    public PerfectAnswerLeftAdapter(Context context, List<PerfectLeftBean> perfectLeftBeans) {
        this.context = context;
        this.perfectLeftBeans = perfectLeftBeans;
    }

    @NonNull
    @Override
    public PerfectAnswerLeftViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new PerfectAnswerLeftViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.adapter_perfect_left_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PerfectAnswerLeftViewHolder perfectAnswerLeftViewHolder, int i) {
        int rank = perfectLeftBeans.get(i).getRank();
        switch (rank) {
            case 1:
                perfectAnswerLeftViewHolder.perfect_rank.setVisibility(View.GONE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setVisibility(View.VISIBLE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setImageResource(R.mipmap.num_one);

                perfectAnswerLeftViewHolder.perfect_up_index.setImageResource(R.mipmap.good_one);
                break;
            case 2:
                perfectAnswerLeftViewHolder.perfect_rank.setVisibility(View.GONE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setVisibility(View.VISIBLE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setImageResource(R.mipmap.num_two);

                perfectAnswerLeftViewHolder.perfect_up_index.setImageResource(R.mipmap.good_two);
                break;
            case 3:
                perfectAnswerLeftViewHolder.perfect_rank.setVisibility(View.GONE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setVisibility(View.VISIBLE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setImageResource(R.mipmap.num_three);

                perfectAnswerLeftViewHolder.perfect_up_index.setImageResource(R.mipmap.good_three);
                break;
            default:
                perfectAnswerLeftViewHolder.perfect_rank.setVisibility(View.VISIBLE);
                perfectAnswerLeftViewHolder.perfect_rank_img.setVisibility(View.GONE);
                perfectAnswerLeftViewHolder.perfect_rank.setText(rank+"");

                perfectAnswerLeftViewHolder.perfect_up_index.setImageResource(R.mipmap.good_other);
                break;
        }

        if (!TextUtils.isEmpty(perfectLeftBeans.get(i).getPhotoUrl())) {
            Glide.with(context).load(perfectLeftBeans.get(i).getPhotoUrl())
                    .placeholder(R.mipmap.icon_user)
                    .error(R.mipmap.icon_user)
                    .into(perfectAnswerLeftViewHolder.perfect_avatar);
        }

        perfectAnswerLeftViewHolder.perfect_name.setText(perfectLeftBeans.get(i).getName());

        perfectAnswerLeftViewHolder.perfect_score.setText("得分：" + perfectLeftBeans.get(i).getScore());

        perfectAnswerLeftViewHolder.perfect_name.setOnClickListener(new View.OnClickListener() {
            boolean flag=true;
            @Override
            public void onClick(View arg0) {
                if(flag){
                    flag = false;
                    perfectAnswerLeftViewHolder.perfect_name.setEllipsize(null); // 展开
                }else{
                    flag = true;
                    perfectAnswerLeftViewHolder.perfect_name.setEllipsize(TextUtils.TruncateAt.valueOf("END")); // 收缩
                }
                perfectAnswerLeftViewHolder.perfect_name.setSingleLine(flag);
            }
        });

    }

    @Override
    public int getItemCount() {
        return perfectLeftBeans != null ? perfectLeftBeans.size() : 0;
    }

    public class PerfectAnswerLeftViewHolder extends RecyclerView.ViewHolder {

        private TextView perfect_rank;
        private ImageView perfect_rank_img;
        private CircleImageView perfect_avatar;
        private TextView perfect_name;
        private TextView perfect_score;
        private ImageView perfect_up_index;

        public PerfectAnswerLeftViewHolder(@NonNull View itemView) {
            super(itemView);

            perfect_rank = itemView.findViewById(R.id.perfect_rank);
            perfect_rank_img = itemView.findViewById(R.id.perfect_rank_img);
            perfect_avatar = itemView.findViewById(R.id.perfect_avatar);
            perfect_name = itemView.findViewById(R.id.perfect_name);
            perfect_score = itemView.findViewById(R.id.perfect_score);
            perfect_up_index = itemView.findViewById(R.id.perfect_up_index);

        }
    }
}
