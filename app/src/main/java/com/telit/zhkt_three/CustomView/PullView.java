package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/15 14:41
 */
public class PullView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private TextView pull_title;
    private LinearLayout pull_linear;
    private TextView pull_content;
    private ImageView pull_img;

    public PullView(Context context) {
        this(context, null);
    }

    public PullView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.pull_select_layout_two, this, true);

        pull_title = view.findViewById(R.id.pull_title);
        pull_linear = view.findViewById(R.id.pull_linear);
        pull_content = view.findViewById(R.id.pull_content);
        pull_img = view.findViewById(R.id.pull_img);

        pull_linear.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pull_linear:
                if (dataList == null || dataList.size() <= 0) {
                    QZXTools.popToast(getContext(), "暂时没有可选项", false);
                    return;
                }
                //下拉选项菜单
                popupSelectMenu(v);
                break;
        }
    }

    /**
     * 设置下拉选项标题
     */
    public void setPullTitle(String title) {
        pull_title.setText(title);
    }

    /**
     * 设置内容,一开始是默认的值
     */
    public void setPullContent(String content) {
        pull_content.setText(content);
        if (menuPopup != null) {
            menuPopup.dismiss();
        }
    }

    /**
     * 返回设置的选项名称
     */
    public String getPullContent() {
        return pull_content.getText().toString().trim();
    }


    private PopupWindow menuPopup;

    /**
     * 下拉数据项
     */
    private List<String> dataList;

    public void setDataList(List<String> dataList) {
        this.dataList = dataList;
    }

    private void popupSelectMenu(View v) {
        if (menuPopup != null) {
            menuPopup.dismiss();
        }

        View menuView = LayoutInflater.from(getContext()).inflate(R.layout.pull_rv_menu_layout, null);
        menuPopup = new PopupWindow(menuView,
                pull_content.getMeasuredWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);

        menuPopup.setBackgroundDrawable(new ColorDrawable());
        menuPopup.setOutsideTouchable(true);

        RecyclerView recyclerView = menuView.findViewById(R.id.pull_menu_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RVPullMenuAdapter adapter = new RVPullMenuAdapter();
        recyclerView.setAdapter(adapter);

        //popup只有具体的尺寸，底部空间不够才会在上面显示
        menuPopup.showAsDropDown(v, 0, -3);
    }

    /**
     * author: qzx
     * Date: 2019/5/15 15:16
     */
    public class RVPullMenuAdapter extends RecyclerView.Adapter<RVPullMenuAdapter.RVPullMenuViewHolder> {

        @NonNull
        @Override
        public RVPullMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RVPullMenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pull_rv_item_layout, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RVPullMenuViewHolder rvPullMenuViewHolder, int i) {
            rvPullMenuViewHolder.textView.setText(dataList.get(i));
            rvPullMenuViewHolder.textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = ((TextView) v).getText().toString().trim();
                    if (clickInterface != null) {
                        clickInterface.spinnerClick(PullView.this, text);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataList.size();
        }

        public class RVPullMenuViewHolder extends RecyclerView.ViewHolder {

            TextView textView;

            public RVPullMenuViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.pull_item_tv);
            }
        }
    }

    /**
     * 下拉点击回调
     */
    private SpinnerClickInterface clickInterface;

    public void setSpinnerClick(SpinnerClickInterface spinnerClick) {
        this.clickInterface = spinnerClick;
    }

    public interface SpinnerClickInterface {
        void spinnerClick(View parent, String text);
    }
}
