package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.QuestionView.KnowledgeQuestionView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.R;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/20 10:04
 * <p>
 * 如果适配器中是MATCH_PARENT那么就会单独一个Item占据一个屏幕
 */
public class RVQuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int FOOT_TYPE = 1;
    private Context mContext;
    private List<QuestionBank> datas;

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

    public RVQuestionAdapter(Context context, List<QuestionBank> list) {
        mContext = context;
        datas = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == FOOT_TYPE) {
            return new FootViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_load_more_layout, viewGroup, false));
        } else {
            return new RVQuestionViewHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_item_bank_item_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof RVQuestionViewHolder) {
            RVQuestionViewHolder rvQuestionViewHolder = (RVQuestionViewHolder) holder;
            rvQuestionViewHolder.questionView.setQuestionInfo(datas.get(i), i);
        } else if (holder instanceof FootViewHolder) {
            FootViewHolder footHolder = (FootViewHolder) holder;
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
        return datas != null ? datas.size() + 1 : 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == datas.size()) {
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

    public class RVQuestionViewHolder extends RecyclerView.ViewHolder {


        private KnowledgeQuestionView questionView;

        public RVQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionView = itemView.findViewById(R.id.item_bank_questionView);
        }
    }
}
