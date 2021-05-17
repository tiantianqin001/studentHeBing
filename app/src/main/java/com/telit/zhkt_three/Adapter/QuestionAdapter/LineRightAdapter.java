package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.R;

import java.util.List;

public class LineRightAdapter extends RecyclerView.Adapter<LineRightAdapter.MyHolder> {
    private final Context mContext;
    private List<QuestionInfo.RightListBean> leftList;
    private onRightOnClickListener listener;


    public LineRightAdapter(Context mContext, List<QuestionInfo.RightListBean> leftList) {
        this.mContext = mContext;

        this.leftList = leftList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_left_line, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {

    }


    @Override
    public int getItemCount() {
        return leftList.size();
    }

  protected   class   MyHolder extends RecyclerView.ViewHolder{

      //private final TextView tv_item_line_word;

      public MyHolder(@NonNull View itemView) {
          super(itemView);
          //tv_item_line_word = itemView.findViewById(R.id.tv_item_line_word);
      }
  }


    public interface onRightOnClickListener{
        void onLeftItemCheck(int position);
    }

    public void setOnRightOnClickListener(onRightOnClickListener listener){


        this.listener = listener;
    }
}
