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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.telit.zhkt_three.CustomView.CornerTransform;
import com.telit.zhkt_three.JavaBean.Gson.TypicalAnswers;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.DensityUtils;
import com.telit.zhkt_three.Utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/4 15:18
 * <p>
 * 典型答题的适配器
 * <p>
 * 如果采用R.layout.rv_after_homework_item_layout_two，嵌套了一层线性RecyclerView的话XRecyclerView的下拉显示有问题？
 */
public class TypicalAnswersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<TypicalAnswers> datas;
    private String type;//1、优秀作答 2、典型错误

    public TypicalAnswersAdapter(Context context, List<TypicalAnswers> list) {
        mContext = context;
        datas = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.typical_answers_item_layout, viewGroup, false));
    }

    /**
     * 从前到后没有问题，但是回拉即从后往前就会显示出bug
     * 解决方式：预先处理Datas而不是临时判断
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            TypicalAnswers typicalAnswers = datas.get(i);

           /* if (isImage(learnResource.getFileFormat().toLowerCase())){
                Glide.with(mContext).load(learnResource.getPreviewUrl()).error(R.mipmap.learn_resource_image_icon).placeholder(R.mipmap.learn_resource_image_icon).into(holder.img_tag);
            }else {
                if (getResourceId(learnResource.getFileFormat())!=0){
                    holder.img_tag.setImageResource(getResourceId(learnResource.getFileFormat()));
                }
            }*/

            if (TextUtils.isEmpty(type)){
                holder.iv_tag.setVisibility(View.GONE);
            }else {
                holder.iv_tag.setVisibility(View.VISIBLE);

                if ("1".equals(type)){
                    holder.iv_tag.setImageResource(R.mipmap.perfect_answers_icon);
                }else if ("2".equals(type)){
                    holder.iv_tag.setImageResource(R.mipmap.typical_mistake_icon);
                }
            }

            holder.tv_name.setText(typicalAnswers.getStudentName()+"（1/"+typicalAnswers.getAttachmentArr().size()+"）");

            if (typicalAnswers.getAttachmentArr()!=null&&typicalAnswers.getAttachmentArr().size()>0){
                Glide.with(mContext).load(typicalAnswers.getAttachmentArr().get(0)).
                        diskCacheStrategy(DiskCacheStrategy.NONE)
                        .transform(new CornerTransform(mContext, DensityUtils.dip2px(mContext,5))).into(holder.img_answer);
            }

            holder.item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (typicalAnswers.getAttachmentArr()!=null&&typicalAnswers.getAttachmentArr().size()>0){
                        if (ViewUtils.isFastClick(1000)){
                            Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                            intent_img.putStringArrayListExtra("imgResources", (ArrayList<String>) typicalAnswers.getAttachmentArr());
                            intent_img.putExtra("curImgIndex", 0);
                            intent_img.putExtra("flag", "1");
                            intent_img.putExtra("type", type);
                            mContext.startActivity(intent_img);
                        }
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount () {
        return datas != null ? datas.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private RelativeLayout item_layout;
        private ImageView img_answer;
        private ImageView iv_tag;
        private TextView tv_name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_layout = itemView.findViewById(R.id.item_layout);
            img_answer = itemView.findViewById(R.id.img_answer);
            iv_tag = itemView.findViewById(R.id.iv_tag);
            tv_name = itemView.findViewById(R.id.tv_name);
        }
    }

    public void setDatas(List<TypicalAnswers> datas,String type){
        this.datas = datas;
        this.type = type;
        notifyDataSetChanged();
    }
}
