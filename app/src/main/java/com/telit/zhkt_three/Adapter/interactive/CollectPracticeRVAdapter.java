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

import com.telit.zhkt_three.Activity.ClassRecord.RecordPracticeDoneActivity;
import com.telit.zhkt_three.JavaBean.InterActive.CollectInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.File;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/23 16:03
 * <p>
 * 依据文件名的后缀判断文件类型就可以了
 */
public class CollectPracticeRVAdapter extends RecyclerView.Adapter<CollectPracticeRVAdapter.CollectPracticeRVViewHolder> {

    private Context mContext;
    private List<CollectInfo> mList;

    public CollectPracticeRVAdapter(Context context, List<CollectInfo> files) {
        mContext = context;
        mList = files;
    }

    @NonNull
    @Override
    public CollectPracticeRVViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new CollectPracticeRVViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.adapter_item_collect_practice, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectPracticeRVViewHolder collectPracticeRVViewHolder, int i) {
        collectPracticeRVViewHolder.collect_practice_name.setText(mList.get(i).getCollectName());
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }

    public class CollectPracticeRVViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView collect_practice_name;
        private TextView collect_practice_look;

        public CollectPracticeRVViewHolder(@NonNull View itemView) {
            super(itemView);
            collect_practice_name = itemView.findViewById(R.id.collect_practice_name);
            collect_practice_look = itemView.findViewById(R.id.collect_practice_look);

            collect_practice_look.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.collect_practice_look:
                    Intent intent_practice = new Intent(mContext, RecordPracticeDoneActivity.class);
                    intent_practice.putExtra("practice_id", mList.get(getLayoutPosition() - 1).getCollectId());
                    mContext.startActivity(intent_practice);
                    break;
            }
        }
    }
}
