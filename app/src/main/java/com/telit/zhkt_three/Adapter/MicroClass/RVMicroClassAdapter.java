package com.telit.zhkt_three.Adapter.MicroClass;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telit.zhkt_three.Adapter.ClassRecord.RVClassRecordGridAdapter;
import com.telit.zhkt_three.JavaBean.ClassRecord.ClassRecord;
import com.telit.zhkt_three.JavaBean.MicroClass.OrderByDateMicBean;
import com.telit.zhkt_three.JavaBean.PreView.Disk;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/17 17:15
 * <p>
 * 微课的最外层Adapter与课堂记录的一致
 * todo 在RecyclerView中嵌套RecyclerView刷新问题
 */
public class RVMicroClassAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOT_TYPE = 1;

    //底部是否可见
    private boolean isFootVisible = false;

    public boolean isFootVisible() {
        return isFootVisible;
    }

    public void setFootVisible(boolean footVisible) {
        isFootVisible = footVisible;
    }

    //是否没有可加载的数据
    private boolean isAllEnd = false;

    public boolean isAllEnd() {
        return isAllEnd;
    }

    public void setAllEnd(boolean allEnd) {
        isAllEnd = allEnd;
    }

    private Context mContext;
    private List<OrderByDateMicBean> mDatas;

    public RVMicroClassAdapter(Context context, List<OrderByDateMicBean> orderByDateMicBeans) {
        mContext = context;
        mDatas = orderByDateMicBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //通用一套layout布局：同PreView
        if (viewType == FOOT_TYPE) {
            return new FootViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_load_more_layout, viewGroup, false));
        } else {
            return new RVClassRecordViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_item_preview_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RVClassRecordViewHolder) {
            RVClassRecordViewHolder rvClassRecordViewHolder = (RVClassRecordViewHolder) viewHolder;
            rvClassRecordViewHolder.preview_date_text.setText(mDatas.get(i).getSameDate());

            RVMicroClassGridAdapter rvMicroClassGridAdapter = new RVMicroClassGridAdapter(mContext, mDatas.get(i).getActualMicBeans());
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext, 3);
            rvClassRecordViewHolder.preview_grid_rv.setLayoutManager(gridLayoutManager);
            rvClassRecordViewHolder.preview_grid_rv.setAdapter(rvMicroClassGridAdapter);
        } else if (viewHolder instanceof FootViewHolder) {
            FootViewHolder footHolder = (FootViewHolder) viewHolder;
            if (isAllEnd) {
                footHolder.loadMoreTv.setVisibility(View.VISIBLE);
                footHolder.loadMoreProgress.setVisibility(View.GONE);
                footHolder.loadMoreTv.setText("---已经是底线了---");
            } else {
                if (isFootVisible) {
                    footHolder.loadMoreTv.setVisibility(View.VISIBLE);
                    footHolder.loadMoreProgress.setVisibility(View.VISIBLE);
                    footHolder.loadMoreTv.setText("加载中...");
                } else {
                    footHolder.loadMoreTv.setVisibility(View.GONE);
                    footHolder.loadMoreProgress.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() + 1 : 0;
    }
    /**
     * 尾部设置有问题
     */
    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            //最后一个是footView
            return FOOT_TYPE;
        }
        return super.getItemViewType(position);
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {

        TextView loadMoreTv;
        ProgressBar loadMoreProgress;

        public FootViewHolder(View itemView) {
            super(itemView);
            loadMoreProgress = itemView.findViewById(R.id.load_more_progress);
            loadMoreTv = itemView.findViewById(R.id.load_more_text);
        }
    }

    public class RVClassRecordViewHolder extends RecyclerView.ViewHolder {

        private TextView preview_date_text;
        private RecyclerView preview_grid_rv;

        public RVClassRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            preview_date_text = itemView.findViewById(R.id.preview_date_text);
            preview_grid_rv = itemView.findViewById(R.id.preview_grid_rv);
        }
    }
}
