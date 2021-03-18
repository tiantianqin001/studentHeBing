package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author: qzx
 * Date: 2019/8/1 10:38
 * <p>
 * Linearlayout中添加子View手动LayoutParams
 */
public class CJFLResultDialog extends DialogFragment implements View.OnClickListener {

    private LinearLayout cjfl_result_layout;
    private ImageView cjfl_result_close;

    private Map<String, List<String>> datas;

    /**
     * 填充超级分类的分类和分类项
     */
    public void fillCJFKResult(Map<String, List<String>> datas) {
        this.datas = datas;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cjfl_result_layout, container, false);
        cjfl_result_layout = view.findViewById(R.id.cjfl_result_layout);
        cjfl_result_close = view.findViewById(R.id.cjfl_result_close);

        //遍历Map展示超级分类的结果
        Iterator<Map.Entry<String, List<String>>> iterator = datas.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, List<String>> entry = iterator.next();
            addItemsView(entry.getKey(), entry.getValue());
        }

        cjfl_result_close.setOnClickListener(this);
        return view;
    }

    /**
     * 添加每个分类项视图
     */
    private void addItemsView(String title, List<String> items) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.cjfl_result_item_layout, null);
        TextView textView = view.findViewById(R.id.cjfl_result_item_title);
        LinearLayout linearLayout = view.findViewById(R.id.cjfl_result_item_layout);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.x250),
                getResources().getDimensionPixelSize(R.dimen.x362));

        layoutParams.leftMargin = getResources().getDimensionPixelOffset(R.dimen.x30);
        layoutParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.x30);

        textView.setText(title);
        for (String str : items) {
            View view_item = LayoutInflater.from(getContext()).inflate(R.layout.cjfl_result_item_item_tv, null);
            TextView itemView = view_item.findViewById(R.id.cjfl_result_item_tv);
            itemView.setText(str);
            linearLayout.addView(view_item);
        }

        cjfl_result_layout.addView(view, layoutParams);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cjfl_result_close:
                dismiss();
                break;
        }
    }
}
