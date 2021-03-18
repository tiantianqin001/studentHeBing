package com.telit.zhkt_three.Adapter.interactive;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussBean;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/24 20:01
 * <p>
 * 图片是全路径
 */
public class DiscussCommunicationRVAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiscussBean> mMessages;
    private Context mContext;

    private static final int FromMe = 1;
    private static final int TYPE_TEXT = 0;
    private static final int TYPE_IMG = 1;

    /**
     * 头像Map依据ID查询
     */
    private HashMap<String, String> avatarMap;

    public void setAvatarMap(HashMap<String, String> avatarMap) {
        this.avatarMap = avatarMap;
    }

    public DiscussCommunicationRVAdapter(Context context, List<DiscussBean> list) {
        mContext = context;
        mMessages = list;
    }

    /**
     * 注意这个是viewType
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        if (viewType == FromMe) {
            QZXTools.logE("my", null);
            //我自己发送的
            viewHolder = new DiscussCommunicationRVRightViewHolder
                    (LayoutInflater.from(mContext).inflate(R.layout.adapter_rv_communication_right_layout, viewGroup, false));
        } else {
            QZXTools.logE("other", null);
            //他人发送的
            viewHolder = new DiscussCommunicationRVLeftViewHolder
                    (LayoutInflater.from(mContext).inflate(R.layout.adapter_rv_communication_left_layout, viewGroup, false));
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof DiscussCommunicationRVRightViewHolder) {
            //自己发送的
            DiscussCommunicationRVRightViewHolder from_me_viewHolder = (DiscussCommunicationRVRightViewHolder) viewHolder;
            if (avatarMap != null) {
                Glide.with(mContext).load(avatarMap.get(mMessages.get(i).getStudentId()))
                        .placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(from_me_viewHolder.discuss_right_avatar);
            }
            if (mMessages.get(i).getType() == TYPE_TEXT) {
                //文本内容的消息
                from_me_viewHolder.discuss_right_pic.setVisibility(View.GONE);
                from_me_viewHolder.discuss_right_content.setVisibility(View.VISIBLE);

                from_me_viewHolder.discuss_right_content.setText(mMessages.get(i).getContent());
            } else if (mMessages.get(i).getType() == TYPE_IMG) {
                //图片消息---缩略图，点击查看大图
                from_me_viewHolder.discuss_right_content.setVisibility(View.GONE);
                from_me_viewHolder.discuss_right_pic.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(mMessages.get(i).getThumbnail())
                        .placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(from_me_viewHolder.discuss_right_pic);
            }
            //本人名字不用改了"我"

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            Date date = new Date(mMessages.get(i).getTime());
            String time = simpleDateFormat.format(date);
           // from_me_viewHolder.discuss_right_time.setText(QZXTools.DateOrTimeStrShow(mMessages.get(i).getTime()));
            from_me_viewHolder.discuss_right_time.setText(time);
        } else if (viewHolder instanceof DiscussCommunicationRVLeftViewHolder) {
            //他人发送来的
            DiscussCommunicationRVLeftViewHolder from_other_viewHolder = (DiscussCommunicationRVLeftViewHolder) viewHolder;

            if (avatarMap != null) {
                Glide.with(mContext).load(avatarMap.get(mMessages.get(i).getStudentId()))
                        .placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(from_other_viewHolder.discuss_left_avatar);
            }
            if (mMessages.get(i).getType() == TYPE_TEXT) {
                //文本内容的消息
                from_other_viewHolder.discuss_left_pic.setVisibility(View.GONE);
                from_other_viewHolder.discuss_left_content.setVisibility(View.VISIBLE);

                from_other_viewHolder.discuss_left_content.setText(mMessages.get(i).getContent());
            } else if (mMessages.get(i).getType() == TYPE_IMG) {
                //图片消息---缩略图，点击查看大图
                from_other_viewHolder.discuss_left_content.setVisibility(View.GONE);
                from_other_viewHolder.discuss_left_pic.setVisibility(View.VISIBLE);

                Glide.with(mContext).load(mMessages.get(i).getThumbnail())
                        .placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(from_other_viewHolder.discuss_left_pic);
            }

            from_other_viewHolder.discuss_left_name.setText(mMessages.get(i).getStudentName());

//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
//            String time = simpleDateFormat.format(new Date(mMessages.get(i).getTime()));
            from_other_viewHolder.discuss_left_time.setText(QZXTools.DateOrTimeStrShow(mMessages.get(i).getTime()));
        }
    }

    @Override
    public int getItemCount() {
        return mMessages != null ? mMessages.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        //依据学生ID来判断是不是自己发送的
        if (mMessages.get(position).getStudentId().equals(UserUtils.getUserId())) {
            return FromMe;
        }
        return super.getItemViewType(position);
    }

    /**
     * 左侧的对话栏
     */
    public class DiscussCommunicationRVLeftViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView discuss_left_avatar;
        private TextView discuss_left_name;
        private TextView discuss_left_content;
        private ImageView discuss_left_pic;
        private TextView discuss_left_time;

        public DiscussCommunicationRVLeftViewHolder(@NonNull View itemView) {
            super(itemView);
            discuss_left_avatar = itemView.findViewById(R.id.discuss_left_avatar);
            discuss_left_name = itemView.findViewById(R.id.discuss_left_name);
            discuss_left_content = itemView.findViewById(R.id.discuss_left_content);
            discuss_left_pic = itemView.findViewById(R.id.discuss_left_pic);
            discuss_left_time = itemView.findViewById(R.id.discuss_left_time);

            discuss_left_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showphotoView(mMessages.get(getLayoutPosition()).getContent(), 0);
                }
            });
        }
    }

    /**
     * 右侧的对话栏
     */
    public class DiscussCommunicationRVRightViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView discuss_right_avatar;
        private TextView discuss_right_content;
        private ImageView discuss_right_pic;
        private TextView discuss_right_time;

        public DiscussCommunicationRVRightViewHolder(@NonNull View itemView) {
            super(itemView);
            discuss_right_avatar = itemView.findViewById(R.id.discuss_right_avatar);
            discuss_right_content = itemView.findViewById(R.id.discuss_right_content);
            discuss_right_pic = itemView.findViewById(R.id.discuss_right_pic);
            discuss_right_time = itemView.findViewById(R.id.discuss_right_time);

            discuss_right_pic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showphotoView(mMessages.get(getLayoutPosition()).getContent(), 0);
                }
            });
        }
    }

    private void showphotoView(String imgPath, int index) {
        Intent intent = new Intent(mContext, ImageLookActivity.class);
        ArrayList<String> imgFilePathList = new ArrayList<>();
        imgFilePathList.add(imgPath);
        intent.putStringArrayListExtra("imgResources", imgFilePathList);
        intent.putExtra("curImgIndex", index);
        mContext.startActivity(intent);
    }
}
