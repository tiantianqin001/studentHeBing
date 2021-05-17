package com.telit.zhkt_three.Adapter.AfterHomeWork;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.AfterWork.ItemAfterHomeworkView;
import com.telit.zhkt_three.JavaBean.AfterHomework.AfterHomeworkBean;
import com.telit.zhkt_three.JavaBean.AfterHomework.HandlerByDateHomeworkBean;
import com.telit.zhkt_three.R;

import java.util.Iterator;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/4 15:18
 * <p>
 * 课后作业的总的适配器
 * <p>
 * 如果采用R.layout.rv_after_homework_item_layout_two，嵌套了一层线性RecyclerView的话XRecyclerView的下拉显示有问题？
 */
public class RVAfterHomeWorkAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<HandlerByDateHomeworkBean> datas;
    private String comType;
    private int types;

    public RVAfterHomeWorkAdapter(Context context, List<HandlerByDateHomeworkBean> list) {
        mContext = context;
        datas = list;
    }

    public RVAfterHomeWorkAdapter(Context context, List<HandlerByDateHomeworkBean> list,OnExportClickListener onExportClickListener) {
        mContext = context;
        datas = list;
        this.onExportClickListener = onExportClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVAfterHomeWorkViewHolder(LayoutInflater.from(mContext).
                inflate(R.layout.rv_after_homework_item_layout, viewGroup, false));
    }

    /**
     * 从前到后没有问题，但是回拉即从后往前就会显示出bug
     * 解决方式：预先处理Datas而不是临时判断
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RVAfterHomeWorkViewHolder) {


            try {
                RVAfterHomeWorkViewHolder rvAfterHomeWorkViewHolder = (RVAfterHomeWorkViewHolder) viewHolder;

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("发布日期：");
                String sameDate = datas.get(i).getSameDate();
                sameDate = sameDate.replace("-", "/");
                stringBuilder.append(sameDate);
                rvAfterHomeWorkViewHolder.after_homework_head_date.setText(stringBuilder.toString());

                //先清除LinearLayout
                rvAfterHomeWorkViewHolder.after_homework_linear.removeAllViews();
                List<AfterHomeworkBean> afterHomeworkBeans = datas.get(i).getAfterHomeworkBeans();
                //添加下面的作业列表
                Iterator<AfterHomeworkBean> homeworkBeanIterator = afterHomeworkBeans.iterator();
                while (homeworkBeanIterator.hasNext()){
                    AfterHomeworkBean afterHomeworkBean = homeworkBeanIterator.next();

                    ItemAfterHomeworkView itemAfterHomeworkView = new ItemAfterHomeworkView(mContext, new ItemAfterHomeworkView.OnExportClickListener() {
                        @Override
                        public void onExportClick(View view, String homeworkId,String byHand,String homeworkName,String status) {
                            if (onExportClickListener!=null){
                                onExportClickListener.onExportClick(view,homeworkId,byHand,homeworkName,status);
                            }
                        }
                    });
                    itemAfterHomeworkView.setAfterHomeworkBean(afterHomeworkBean);
                    itemAfterHomeworkView.setType(comType);
                    itemAfterHomeworkView.setTypes(types);
                    rvAfterHomeWorkViewHolder.after_homework_linear.addView(itemAfterHomeworkView);
                }
                homeworkBeanIterator.remove();
            }catch (Exception e){
                e.fillInStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas != null ? datas.size() : 0;
    }

    public void setType(String comType) {

        this.comType = comType;
    }

    public void setTypes(int types) {
        this.types = types;
    }

    public class RVAfterHomeWorkViewHolder extends RecyclerView.ViewHolder {

        private TextView after_homework_head_date;
        private LinearLayout after_homework_linear;
        //R.layout.rv_after_homework_item_layout_two
//        private RecyclerView after_homework_recycler;

        public RVAfterHomeWorkViewHolder(@NonNull View itemView) {
            super(itemView);
            after_homework_head_date = itemView.findViewById(R.id.after_homework_head_date);
            after_homework_linear = itemView.findViewById(R.id.after_homework_linear);
//            after_homework_recycler = itemView.findViewById(R.id.after_homework_recycler);
        }
    }

    private OnExportClickListener onExportClickListener;

    public interface OnExportClickListener {
        void onExportClick(View view, String homeworkId,String byHand,String homeworkName,String status);
    }
}
