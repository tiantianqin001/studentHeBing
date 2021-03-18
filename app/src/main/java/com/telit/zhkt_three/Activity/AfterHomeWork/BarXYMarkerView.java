package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.telit.zhkt_three.R;

import java.text.DecimalFormat;

/**
 * author: qzx
 * Date: 2019/9/28 11:03
 * <p>
 * 弹出内容显示：得分+总分
 */
@SuppressLint("ViewConstructor")
public class BarXYMarkerView extends MarkerView {

    private final TextView tvContent;
    private final ValueFormatter xAxisValueFormatter;

    private final DecimalFormat format;

    public BarXYMarkerView(Context context, ValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);

        this.xAxisValueFormatter = xAxisValueFormatter;
        tvContent = findViewById(R.id.tvContent);
        format = new DecimalFormat("###.0");
    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        String xStr = xAxisValueFormatter.getFormattedValue(e.getX());
        String[] splitXStr = xStr.split(":");

        String x = "";

        if (xAxisValueFormatter instanceof BarAxisValueFormatter) {
            x = ((BarAxisValueFormatter) xAxisValueFormatter).getIndexTotalScore(Integer.parseInt(splitXStr[0])) + "";
        } else {
            x = xAxisValueFormatter.getFormattedValue(e.getX());
        }

        tvContent.setText(String.format("我的得分: %s, 该题总分: %s", x, format.format(e.getY())));

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
