package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * author: qzx
 * Date: 2019/9/28 11:03
 * <p>
 * 弹出内容显示：所有题序
 */
@SuppressLint("ViewConstructor")
public class PieMarkerView extends MarkerView {

    private final TextView tvContent;

    private Map<Integer, String> mapIndex;

    public void setMapIndex(Map<Integer, String> mapIndex) {
        this.mapIndex = mapIndex;
    }

    public PieMarkerView(Context context) {
        super(context, R.layout.custom_marker_view);

        tvContent = findViewById(R.id.tvContent);
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        PieEntry pieEntry = (PieEntry) e;
        String label = pieEntry.getLabel();

        if (label.equals("正确")) {
            String value = mapIndex.get(1);
            if (TextUtils.isEmpty(value)) {
                tvContent.setText("暂无题序");
            } else {
                tvContent.setText("答对的题序为：" + value);
            }
        } else if (label.equals("错误")) {
            String value = mapIndex.get(0);
            if (TextUtils.isEmpty(value)) {
                tvContent.setText("暂无题序");
            } else {
                tvContent.setText("答错的题序为：" + value);
            }
        } else if (label.equals("客观")) {
            String value = mapIndex.get(2);
            if (TextUtils.isEmpty(value)) {
                tvContent.setText("暂无题序");
            } else {
                tvContent.setText("客观的题序为：" + value);
            }
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
