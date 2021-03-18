package com.telit.zhkt_three.Adapter.interactive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/23 16:03
 * <p>
 * 依据文件名的后缀判断文件类型就可以了
 */
public class JoinTopicRVAdapter extends RecyclerView.Adapter<JoinTopicRVAdapter.JoinTopicRVViewHolder> {

    private Context mContext;
    private List<String> mList;

    public JoinTopicRVAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public JoinTopicRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new JoinTopicRVViewHolder(LayoutInflater.from(mContext).inflate(R.layout.adapter_item_join_topic, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinTopicRVViewHolder receiveFileRVViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public class JoinTopicRVViewHolder extends RecyclerView.ViewHolder {

        private TextView join_topic_content;
        private TextView join_topic_btn;

        public JoinTopicRVViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.findViewById(R.id.join_topic_content);
            itemView.findViewById(R.id.join_topic_btn);
        }
    }
}
