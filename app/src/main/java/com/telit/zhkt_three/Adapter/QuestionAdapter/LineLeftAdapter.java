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
    private List<QuestionInfo.RightListBean> rightList;
    private onLeftOnClickListener listener;

    public LineLeftAdapter(Context mContext, List<QuestionInfo.LeftListBean> leftList,
                           List<QuestionInfo.RightListBean> rightList) {
        this.mContext = mContext;
        this.leftList = leftList;
        this.rightList = rightList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new MyHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_left_line, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
        myHolder.tv_item_line_word_lift.setText(leftList.get(i).getTitle());
        myHolder.tv_item_line_word_right.setText(rightList.get(i).getTitle());

        myHolder.tv_item_line_word_lift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)listener.onLeftItemCheck(i);
            }
        });

        myHolder.tv_item_line_word_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener!=null)listener.onRightItemClick(i);
            }
        });
    }


    @Override
    public int getItemCount() {
        return leftList.size();
    }

  protected   class   MyHolder extends RecyclerView.ViewHolder{

      private final TextView tv_item_line_word_lift;
      private final TextView tv_item_line_word_right;

      public MyHolder(@NonNull View itemView) {
          super(itemView);
          tv_item_line_word_lift = itemView.findViewById(R.id.tv_item_line_word_left);
          tv_item_line_word_right = itemView.findViewById(R.id.tv_item_line_word_right);
      }
  }


  public interface onLeftOnClickListener{
        void onLeftItemCheck(int position);
        void onRightItemClick(int position);
  }

  public void setOnLeftOnClickListener(onLeftOnClickListener listener){

      this.listener = listener;
  }
}
