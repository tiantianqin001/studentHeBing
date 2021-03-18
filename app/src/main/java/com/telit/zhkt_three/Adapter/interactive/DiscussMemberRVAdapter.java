package com.telit.zhkt_three.Adapter.interactive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussListBeanTwo;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/23 16:03
 * <p>
 * 依据文件名的后缀判断文件类型就可以了
 */
public class DiscussMemberRVAdapter extends RecyclerView.Adapter<DiscussMemberRVAdapter.DiscussMemberRVViewHolder> {

    private Context mContext;
    private List<DiscussListBeanTwo> mDatas;

    public DiscussMemberRVAdapter(Context context, List<DiscussListBeanTwo> list) {
        mContext = context;
        mDatas = list;
    }

    @NonNull
    @Override
    public DiscussMemberRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DiscussMemberRVViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_item_discuss_member, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussMemberRVViewHolder discussMemberRVViewHolder, int i) {

        Glide.with(mContext).load(mDatas.get(i).getPhoto()).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user)
                .into(discussMemberRVViewHolder.discuss_member_item_avatar);
        discussMemberRVViewHolder.discuss_member_item_name.setText(mDatas.get(i).getUserName());
        if (TextUtils.isEmpty(mDatas.get(i).getClassName())) {
            discussMemberRVViewHolder.discuss_member_item_class.setText("");
        } else {
            discussMemberRVViewHolder.discuss_member_item_class.setText(mDatas.get(i).getClassName());
        }

        if (TextUtils.isEmpty(mDatas.get(i).getSpeakTime())) {
            discussMemberRVViewHolder.discuss_member_item_time.setText("");
        } else {
            discussMemberRVViewHolder.discuss_member_item_time.setText(mDatas.get(i).getSpeakTime());
        }

        //userid等于groupleader即表示其为记录员（组长）
        String groupLeader = mDatas.get(i).getGroupLeader();
        if (!TextUtils.isEmpty(groupLeader) && groupLeader.equals(mDatas.get(i).getUserId())) {
            //是组长
            discussMemberRVViewHolder.discuss_member_item_is_leader.setVisibility(View.VISIBLE);
        } else {
            discussMemberRVViewHolder.discuss_member_item_is_leader.setVisibility(View.INVISIBLE);
        }

        discussMemberRVViewHolder.discuss_member_item_class.setText(mDatas.get(i).getClassName());
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    public class DiscussMemberRVViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView discuss_member_item_avatar;
        private TextView discuss_member_item_name;
        private TextView discuss_member_item_class;
        private TextView discuss_member_item_is_leader;
        private TextView discuss_member_item_time;

        public DiscussMemberRVViewHolder(@NonNull View itemView) {
            super(itemView);
            discuss_member_item_avatar = itemView.findViewById(R.id.discuss_member_item_avatar);
            discuss_member_item_name = itemView.findViewById(R.id.discuss_member_item_name);
            discuss_member_item_class = itemView.findViewById(R.id.discuss_member_item_class);
            discuss_member_item_is_leader = itemView.findViewById(R.id.discuss_member_item_is_leader);
            discuss_member_item_time = itemView.findViewById(R.id.discuss_member_item_time);
        }
    }
}
