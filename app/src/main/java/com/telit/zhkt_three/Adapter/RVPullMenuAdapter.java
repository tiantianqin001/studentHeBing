package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/15 15:16
 *
 * 弃用啦.......
 */
public class RVPullMenuAdapter extends RecyclerView.Adapter<RVPullMenuAdapter.RVPullMenuViewHolder> {

    private Context mContext;
    private List<String> datas;

    public RVPullMenuAdapter(Context context, List<String> list) {
        mContext = context;
        datas = list;
    }

    @NonNull
    @Override
    public RVPullMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVPullMenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pull_rv_item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVPullMenuViewHolder rvPullMenuViewHolder, int i) {
        rvPullMenuViewHolder.textView.setText(datas.get(i));
        rvPullMenuViewHolder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = ((TextView) v).getText().toString().trim();
                if (clickInterface != null) {
                    clickInterface.spinnerClick(text);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class RVPullMenuViewHolder extends RecyclerView.ViewHolder {

        TextView textView;

        public RVPullMenuViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.pull_item_tv);
        }
    }

    private SpinnerClickInterface clickInterface;

    public void setSpinnerClick(SpinnerClickInterface spinnerClick) {
        this.clickInterface = spinnerClick;
    }

    public interface SpinnerClickInterface {
        void spinnerClick(String text);
    }
}
