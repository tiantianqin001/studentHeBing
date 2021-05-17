package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.R;

import java.util.ArrayList;

public class SubjectImageAdapter extends RecyclerView.Adapter {

    private Context mContent;
    private ArrayList<String> imgFilePathList;
    private  ImageView subjective_img_three;


    private  ImageView subjective_del_three;

    public SubjectImageAdapter(Context mContent, ArrayList<String> imgFilePathList){

        this.mContent = mContent;
        this.imgFilePathList = imgFilePathList;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       // View view =  LayoutInflater.from(mContent).inflate(R.layout.item_subject_image, viewGroup, false);
        View view = View.inflate(mContent, R.layout.item_subject_image,null );

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        viewHolder.setIsRecyclable(false);
        Glide.with(mContent)
                .load(imgFilePathList.get(i))
                .into(subjective_img_three);


        subjective_del_three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //知道点击的是哪个作业id

            }
        });
    }

    /**
     * 重写 避免滑动过程界面混乱
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return imgFilePathList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{


        public MyHolder(@NonNull View itemView) {
            super(itemView);
            subjective_img_three = itemView.findViewById(R.id.subjective_img_three);
            subjective_del_three = itemView.findViewById(R.id.subjective_del_three);
        }
    }
}
