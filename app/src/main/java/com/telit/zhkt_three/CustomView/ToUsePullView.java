package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
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
public class ToUsePullView extends RelativeLayout implements View.OnClickListener {

    private Context mContext;

    private LinearLayout pull_linear;
    private TextView pull_content;
    private ImageView pull_img;

    /**
     * 下拉图标资源id
     */
    private int imgResId;

    public void setImgResId(int imgResId) {
        this.imgResId = imgResId;
        pull_img.setImageResource(imgResId);
    }

    /**
     * 文本颜色
     */
    private int color;

    public void setColor(int color) {
        this.color = color;
        pull_content.setTextColor(color);
    }

    /**
     * hint颜色
     */
    private int hintColor;

    public void setHintColor(int hintColor) {
        this.hintColor = hintColor;
        pull_content.setHintTextColor(hintColor);
    }

    /**
     * 设置提示文本
     */
    private String hintText;

    public void setHintText(String hintText) {
        this.hintText = hintText;
        pull_content.setHint(hintText);
    }

    /**
     * 间距
     */
    private int distance_txt_img;

    /**
     * 是否需要上拉而不是下拉
     */
    private boolean isPullUp;

    public boolean isPullUp() {
        return isPullUp;
    }

    public void setPullUp(boolean pullUp) {
        isPullUp = pullUp;
    }

    public void setDistance_txt_img(int distance_txt_img) {
        this.distance_txt_img = distance_txt_img;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pull_img.getLayoutParams();
        layoutParams.leftMargin = distance_txt_img;
        pull_img.setLayoutParams(layoutParams);
    }

    public ToUsePullView(Context context) {
        this(context, null);
    }

    public ToUsePullView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToUsePullView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.pull_select_layout, this, true);

        pull_linear = view.findViewById(R.id.pull_linear);
        pull_content = view.findViewById(R.id.pull_content);
        pull_img = view.findViewById(R.id.pull_img);

        TypedArray typedArray = mContext.obtainStyledAttributes(attrs, R.styleable.ToUsePullView, defStyleAttr, 0);
        imgResId = typedArray.getResourceId(R.styleable.ToUsePullView_pull_icon, R.mipmap.pull_down);
        color = typedArray.getColor(R.styleable.ToUsePullView_text_color, Color.WHITE);
        hintColor = typedArray.getColor(R.styleable.ToUsePullView_hint_color, 0xFFA2A2A2);
        hintText = typedArray.getString(R.styleable.ToUsePullView_hint_text);
        popMenuBgColor = typedArray.getColor(R.styleable.ToUsePullView_pop_menu_bg, 0xFF4562CF);
        popMenuTextColor = typedArray.getColor(R.styleable.ToUsePullView_pop_text_color, Color.WHITE);
        popMenuChoosedTextColor = typedArray.getColor(R.styleable.ToUsePullView_pop_choosed_text_color, Color.BLUE);
        distance_txt_img = typedArray.getDimensionPixelSize(R.styleable.ToUsePullView_distance_txt_img,
                getResources().getDimensionPixelSize(R.dimen.y60));

        isPullUp = typedArray.getBoolean(R.styleable.ToUsePullView_is_pull_up, false);

        typedArray.recycle();

        pull_img.setImageResource(imgResId);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) pull_img.getLayoutParams();
        layoutParams.leftMargin = distance_txt_img;
        pull_img.setLayoutParams(layoutParams);

        pull_content.setTextColor(color);
        pull_content.setHintTextColor(hintColor);
        if (!TextUtils.isEmpty(hintText)) {
            pull_content.setHint(hintText);
        }

        pull_linear.setOnClickListener(this);
    }

    /**
     * 原先是点击必弹出下拉框的
     * 修改：如果是处于下拉状态，点击收回
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pull_linear:
                if (dataList == null || dataList.size() <= 0) {
                    QZXTools.popToast(getContext(), "暂时没有可选项", false);
                    return;
                }
                if (isPullShown) {
                    if (menuPopup != null) {
                        menuPopup.dismiss();
                    }
                } else {
                    //下拉选项菜单
                    popupSelectMenu(v);
                }
                break;
        }
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

    private boolean isPullShown = false;

    /**
     * 是否下拉显示中
     */
    public boolean pullViewPopShown() {
        return isPullShown;
    }

    /**
     * popwindow弹出框仅支持xml属性
     */
    private int popMenuBgColor;
    private int popMenuTextColor;
    private int popMenuChoosedTextColor;

    private void popupSelectMenu(View v) {
        if (menuPopup != null) {
            menuPopup.dismiss();
        }

        isPullShown = true;

        View menuView = LayoutInflater.from(getContext()).inflate(R.layout.pull_rv_menu_layout, null);

        if (isPullUp) {
//            menuPopup = new PopupWindow(menuView, getMeasuredWidth(), (int) getResources().getDimension(R.dimen.x400));
            menuPopup = new PopupWindow(menuView, getMeasuredWidth(),
                    getMeasuredHeight() + dataList.size() * (int) getResources().getDimensionPixelSize(R.dimen.x76));
        } else {
            menuPopup = new PopupWindow(menuView, getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
        }

        menuPopup.setBackgroundDrawable(new ColorDrawable());
        menuPopup.setOutsideTouchable(true);

        menuPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                iconRotate(pull_img, 180, 0);
                //为什么延迟，因为点击事件一级背景后触发
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isPullShown = false;
                    }
                }, 1000);
            }
        });

        ConstraintLayout constraintLayout = menuView.findViewById(R.id.pull_menu_bg);
        //设置弹出框背景色
        constraintLayout.setBackgroundColor(popMenuBgColor);

        RecyclerView recyclerView = menuView.findViewById(R.id.pull_menu_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RVPullMenuAdapter adapter = new RVPullMenuAdapter(popMenuTextColor, getPullContent(), popMenuChoosedTextColor);
        recyclerView.setAdapter(adapter);

        //popup只有具体的尺寸，底部空间不够才会在上面显示

        menuPopup.showAsDropDown(v, 0, 0);

        iconRotate(pull_img, 0, 180);
    }

    /**
     * author: qzx
     * Date: 2019/5/15 15:16
     */
    public class RVPullMenuAdapter extends RecyclerView.Adapter<RVPullMenuAdapter.RVPullMenuViewHolder> {

        private int textColor;
        private String choosedText;
        private int choosedColor;

        /**
         * 传入要修改的item文本颜色
         *
         * @param color        下拉的文本颜色
         * @param choosedText  选中的文本
         * @param choosedColor 选中的文本颜色
         */
        public RVPullMenuAdapter(int color, String choosedText, int choosedColor) {
            textColor = color;
            this.choosedText = choosedText;
            this.choosedColor = choosedColor;
        }

        @NonNull
        @Override
        public RVPullMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RVPullMenuViewHolder(LayoutInflater.from(mContext).inflate(R.layout.pull_rv_item_layout, viewGroup,
                    false));
        }

        @Override
        public void onBindViewHolder(@NonNull RVPullMenuViewHolder rvPullMenuViewHolder, int i) {
            rvPullMenuViewHolder.textView.setText(dataList.get(i));
            if (dataList.get(i).equals(choosedText)) {
                rvPullMenuViewHolder.textView.setTextColor(popMenuChoosedTextColor);
            } else {
                rvPullMenuViewHolder.textView.setTextColor(textColor);
            }
            rvPullMenuViewHolder.textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = ((TextView) v).getText().toString().trim();
                    if (clickInterface != null) {
                        clickInterface.spinnerClick(ToUsePullView.this, text);
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
                textView.setTextColor(textColor);
            }
        }
    }

    /**
     * 图标的旋转180度
     */
    public void iconRotate(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
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
