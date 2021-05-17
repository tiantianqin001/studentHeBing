package com.telit.zhkt_three.Adapter.interactive;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.JavaBean.InterActive.SelectGroup;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/13 16:44
 */
public class RVSelectGroupAdapter extends RecyclerView.Adapter<RVSelectGroupAdapter.RVSelectGroupHolder> {

    private Context mContext;
    private List<SelectGroup.SelectGroupDetail> mData;
    private CommitOnItemClickListener listener;
    private String random;
    private String clickIsUser;

    private   boolean isFistClick=true;

    public void setmData(List<SelectGroup.SelectGroupDetail> mData) {
        this.mData = mData;
    }

    public RVSelectGroupAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RVSelectGroupHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVSelectGroupHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_select_group_item_layout,
                viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RVSelectGroupHolder rvSelectGroupHolder, int i) {
        rvSelectGroupHolder.group_select_name.setText(mData.get(i).getGroupName());
        rvSelectGroupHolder.group_select_title.setText(mData.get(i).getTheme());
        if (!TextUtils.isEmpty(random)){
            if (Integer.valueOf(random).equals(i)){
                //todo 现在没有设置
                rvSelectGroupHolder. group_select_name.setSelected(true);
            }else {
                rvSelectGroupHolder. group_select_name.setSelected(false);
            }

        }

    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    //选中的视图
    private View selectedView;

    private String selectedGroupIndex;

    private int selectedGroupId;

    public int getSelectedGroupId() {
        return selectedGroupId;
    }

    public String getSelectedGroupIndex() {
        return selectedGroupIndex;
    }

    public void setSelected(String random, String clickIsUser) {

        this.random = random;
        this.clickIsUser = clickIsUser;
    }

    public class RVSelectGroupHolder extends RecyclerView.ViewHolder {

        private TextView group_select_name;
        private TextView group_select_title;
        private LinearLayout group_select_linear;

        public RVSelectGroupHolder(@NonNull View itemView) {
            super(itemView);
            group_select_name = itemView.findViewById(R.id.group_select_name);
            group_select_title = itemView.findViewById(R.id.group_select_title);
            group_select_linear = itemView.findViewById(R.id.group_select_linear);

            group_select_linear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //只能点一次
                    if (!TextUtils.isEmpty(clickIsUser)){
                        //在随机的时候已经点了
                        return;
                    }
                    if (isFistClick){
                        if (selectedView != null) {
                            if (selectedView == group_select_name) {
                                return;
                            }
                            selectedView.setSelected(false);
                            selectedView.setClickable(false);


                        }
                        selectedGroupIndex = mData.get(getLayoutPosition()).getGroupIndex();
                        selectedGroupId = mData.get(getLayoutPosition()).getGroupDiscussId();
                        QZXTools.logE("selectedGroupIndex=" + selectedGroupIndex + ";selectedGroupId=" + selectedGroupId, null);
                        if (listener!=null){
                            listener.onClick();
                        }


                        group_select_name.setSelected(true);
                        selectedView = group_select_name;

                        isFistClick=false;


                    }



                }
            });

        }
    }

    public interface CommitOnItemClickListener{
        void onClick();
    }

    public void setCommitOnItemClickListener(CommitOnItemClickListener listener){

        this.listener = listener;
    }
}
