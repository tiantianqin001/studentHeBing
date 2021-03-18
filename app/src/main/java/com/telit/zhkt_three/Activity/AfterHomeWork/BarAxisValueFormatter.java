package com.telit.zhkt_three.Activity.AfterHomeWork;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.telit.zhkt_three.JavaBean.HomeWork.ReportBarXAxisInfo;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/9/28 17:31
 */
public class BarAxisValueFormatter extends ValueFormatter {

    private List<ReportBarXAxisInfo> xAxisValues;

    public void setxAxisValues(List<ReportBarXAxisInfo> xAxisValues) {
        this.xAxisValues = xAxisValues;
    }

    /**
     * 得到对应题号的总分值
     */
    public int getIndexTotalScore(int index) {
        //因为index题序我从一开始的
        return xAxisValues.get(index - 1).getTotalScore();
    }

    /**
     * 这个value只能取六个值：序号：题型 【0-5】
     */
    @Override
    public String getFormattedValue(float value) {
//        QZXTools.logE("BarAxisValueFormatter value=" + value, null);

        int index = (int) value;

        int orderIndex = xAxisValues.get(index).getIndex();
        String questionType = xAxisValues.get(index).getQuestionType();

        StringBuilder stringBuilder = new StringBuilder();
        //序号从一开始
        stringBuilder.append((orderIndex + 1));
        stringBuilder.append(":");
        stringBuilder.append(questionType);

        return stringBuilder.toString();
    }
}
