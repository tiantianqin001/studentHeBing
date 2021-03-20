package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.R;

import java.util.List;

public class LineLeftAdapter extends RecyclerView.Adapter<LineLeftAdapter.MyHolder> {
    private final Context mContext;
    private final List<QuestionInfo.LeftListBean> leftList;

    public LineLeftAdapter(Context mContext, List<QuestionInfo.LeftListBean> leftList) {
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
        myHolder.tv_item_line_word.setText(leftList.get(i).getTitle());
    }


    @Override
    public int getItemCount() {
        return leftList.size();
    }

  protected   class   MyHolder extends RecyclerView.ViewHolder{

      private final TextView tv_item_line_word;

      public MyHolder(@NonNull View itemView) {
          super(itemView);
          tv_item_line_word = itemView.findViewById(R.id.tv_item_line_word);
      }
  }
}
