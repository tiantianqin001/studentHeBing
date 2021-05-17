package com.telit.zhkt_three.Adapter.AfterHomeWork;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.JavaBean.AfterHomework.LearnResource;
import com.telit.zhkt_three.MediaTools.OfficeOnLineActivity;
import com.telit.zhkt_three.MediaTools.audio.AudioPlayActivity;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.FormatUtils;
import com.telit.zhkt_three.Utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/4 15:18
 * <p>
 * 学习资源的适配器
 * <p>
 * 如果采用R.layout.rv_after_homework_item_layout_two，嵌套了一层线性RecyclerView的话XRecyclerView的下拉显示有问题？
 */
public class LearnResourceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<LearnResource> datas;

    public LearnResourceAdapter(Context context, List<LearnResource> list) {
        mContext = context;
        datas = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.learn_resource_item_layout, viewGroup, false));
    }

    /**
     * 从前到后没有问题，但是回拉即从后往前就会显示出bug
     * 解决方式：预先处理Datas而不是临时判断
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof MyViewHolder) {
            MyViewHolder holder = (MyViewHolder) viewHolder;
            LearnResource learnResource = datas.get(i);

            holder.tv_name.setText(learnResource.getTitle()+"."+learnResource.getFileFormat());

            holder.tv_date.setText(learnResource.getAddTime());

            holder.tv_size.setText(FormatUtils.FormatFileSize(learnResource.getFileSize()));

           /* if (isImage(learnResource.getFileFormat().toLowerCase())){
                Glide.with(mContext).load(learnResource.getPreviewUrl()).error(R.mipmap.learn_resource_image_icon).placeholder(R.mipmap.learn_resource_image_icon).into(holder.img_tag);
            }else {
                if (getResourceId(learnResource.getFileFormat())!=0){
                    holder.img_tag.setImageResource(getResourceId(learnResource.getFileFormat()));
                }
            }*/

            if (getResourceId(learnResource.getFileFormat())!=0){
                holder.img_tag.setImageResource(getResourceId(learnResource.getFileFormat()));
            }

            holder.item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ViewUtils.isFastClick(1000)){
                        onclick(learnResource.getPreviewUrl(),learnResource.getFileFormat(),learnResource.getFileName()+"."+learnResource.getFileFormat());
                    }
                }
            });
        }
    }

    private boolean isImage(String fileFormat){
        if (fileFormat.equals("jpg")||fileFormat.equals("png")||fileFormat.equals("gif")
                ||fileFormat.equals("jpeg")){
            return true;
        } else{
            return false;
        }
    }

    /**
     *
     *
     * @param url
     * @param fileFormat
     * @param title
     */
    private void onclick(String url,String fileFormat,String title) {
        switch (fileFormat.toLowerCase()) {
            case "jpg":
            case "png":
            case "gif":
            case "jpeg":
                Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                ArrayList<String> imgFilePathList = new ArrayList<>();
                imgFilePathList.add(url);
                intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                intent_img.putExtra("curImgIndex", 0);
                intent_img.putExtra("flag", "1");
                mContext.startActivity(intent_img);
                break;
            case "doc":
            case "docx":
            case "ppt":
            case "pptx":
            case "xlsx":
            case "xls":
            case "txt":
            case "pdf":
            case "zip":
            case "rar":
                Intent intent_office = new Intent(mContext, OfficeOnLineActivity.class);
                intent_office.putExtra("url", url);
                intent_office.putExtra("title", title);
                mContext.startActivity(intent_office);
                break;
            case "mp4":
            case "flv":
            case "avi":
                Intent intent_video = new Intent(mContext, VideoPlayerActivity.class);
                intent_video.putExtra("VideoFilePath", url);
                intent_video.putExtra("VideoTitle", title);
                mContext.startActivity(intent_video);
                break;
            case "mp3":
                Intent intent = new Intent(mContext, AudioPlayActivity.class);
                intent.putExtra("AudioFilePath", url);
                mContext.startActivity(intent);
                break;
        }
    }

        private int getResourceId (String fileFormat){
            int resourceId = 0;
            switch (fileFormat.toLowerCase()) {
                case "doc":
                case "docx":
                    resourceId = R.mipmap.learn_resource_doc_icon;
                    break;
                case "ppt":
                case "pptx":
                    resourceId = R.mipmap.learn_resource_ppt_icon;
                    break;
                case "xlsx":
                case "xls":
                    resourceId = R.mipmap.learn_resource_xls_icon;
                    break;
                case "mp4":
                case "flv":
                case "avi":
                    resourceId = R.mipmap.learn_resource_video_icon;
                    break;
                case "mp3":
                    resourceId = R.mipmap.learn_resource_mp3_icon;
                    break;
                case "zip":
                case "rar":
                    resourceId = R.mipmap.learn_resource_zip_icon;
                    break;
                case "pdf":
                    resourceId = R.mipmap.learn_resource_pdf_icon;
                    break;
                case "png":
                case "jpg":
                case "jpeg":
                case "gif":
                    resourceId = R.mipmap.learn_resource_image_icon;
                    break;
            }
            return resourceId;
        }

        @Override
        public int getItemCount () {
            return datas != null ? datas.size() : 0;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            private RelativeLayout item_layout;
            private ImageView img_tag;
            private TextView tv_name;
            private TextView tv_date;
            private TextView tv_size;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                item_layout = itemView.findViewById(R.id.item_layout);
                img_tag = itemView.findViewById(R.id.img_tag);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_date = itemView.findViewById(R.id.tv_date);
                tv_size = itemView.findViewById(R.id.tv_size);
            }
        }
}
