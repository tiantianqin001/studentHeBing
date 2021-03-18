package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/6/23 17:50
 * <p>
 * 学生个人单次作业分析报告，通过已批阅作业中跳转或者个人空间个人报告中跳转
 */
public class PersonAnalysisReportDialog extends DialogFragment implements OnChartValueSelectedListener, View.OnClickListener {

    private Unbinder unbinder;

    @BindView(R.id.report_close)
    ImageView report_close;
    @BindView(R.id.report_title)
    TextView report_title;
    @BindView(R.id.report_enter_detail)
    TextView report_enter_detail;
    @BindView(R.id.pie_chart)
    PieChart pie_chart;
    @BindView(R.id.bar_chart)
    BarChart bar_chart;
    @BindView(R.id.radar_chart)
    RadarChart radar_chart;

    //字体
    private Typeface tfRegular;
    private Typeface tfLight;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_analysis_report, container, false);
        unbinder = ButterKnife.bind(this, view);

        tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
        tfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");

        initView();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        //进入该作业报告的作业详情页面
        report_enter_detail.setOnClickListener(this);
        report_close.setOnClickListener(this);

        //-----------------------------------------------------------------初始化馅饼图
        pie_chart.setUsePercentValues(true);
        pie_chart.getDescription().setEnabled(false);
        pie_chart.setExtraOffsets(5, 10, 5, 5);

        pie_chart.setDragDecelerationFrictionCoef(0.95f);

        pie_chart.setCenterTextTypeface(tfRegular);
        pie_chart.setCenterText("作业正确错误率");
        pie_chart.setDrawCenterText(true);

        pie_chart.setExtraOffsets(20.f, 0.f, 20.f, 0.f);

        pie_chart.setDrawHoleEnabled(true);
        pie_chart.setHoleColor(Color.WHITE);

        pie_chart.setTransparentCircleColor(Color.WHITE);
        pie_chart.setTransparentCircleAlpha(110);

        pie_chart.setHoleRadius(58f);
        pie_chart.setTransparentCircleRadius(61f);

        pie_chart.setRotationAngle(0);
        // enable rotation of the pie_chart by touch
        pie_chart.setRotationEnabled(true);
        pie_chart.setHighlightPerTapEnabled(true);

        pie_chart.setOnChartValueSelectedListener(this);

        pie_chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = pie_chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
//        l.setEnabled(false);//是否显示右上角的类型标记

        //-----------------------------------------------------------------初始化馅饼图

        //-----------------------------------------------------------------初始化柱状图
        bar_chart.setOnChartValueSelectedListener(this);

        bar_chart.setDrawBarShadow(false);
        bar_chart.setDrawValueAboveBar(true);

        bar_chart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        bar_chart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        bar_chart.setPinchZoom(false);

        bar_chart.setDrawGridBackground(false);
        // chart.setDrawYLabels(false);

        ValueFormatter xAxisFormatter = new DayAxisValueFormatter(bar_chart);

        XAxis xAxis = bar_chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);

        ValueFormatter custom = new MyValueFormatter("$");


        YAxis leftAxis = bar_chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis rightAxis = bar_chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        rightAxis.setEnabled(false);

        Legend l_bar = bar_chart.getLegend();
        l_bar.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l_bar.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l_bar.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l_bar.setDrawInside(false);
        l_bar.setForm(Legend.LegendForm.SQUARE);
        l_bar.setFormSize(9f);
        l_bar.setTextSize(11f);
        l_bar.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(getContext(), xAxisFormatter);
        mv.setChartView(bar_chart); // For bounds control
        bar_chart.setMarker(mv); // Set the marker to the chart
        //-----------------------------------------------------------------初始化柱状图

        //-----------------------------------------------------------------初始化雷达图
        //雷达图背景色
//        radar_chart.setBackgroundColor(Color.rgb(60, 65, 82));

        radar_chart.getDescription().setEnabled(false);

        radar_chart.setWebLineWidth(1f);
        radar_chart.setWebColor(Color.LTGRAY);
        radar_chart.setWebLineWidthInner(1f);
        radar_chart.setWebColorInner(Color.LTGRAY);
        radar_chart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv_radar = new RadarMarkerView(getContext(), R.layout.radar_markerview);
        mv_radar.setChartView(radar_chart); // For bounds control
        radar_chart.setMarker(mv_radar); // Set the marker to the chart

        radar_chart.animateXY(1400, 1400, Easing.EaseInOutQuad);

        XAxis xAxis_radar = radar_chart.getXAxis();
        xAxis_radar.setTypeface(tfLight);
        xAxis_radar.setTextSize(9f);
        xAxis_radar.setYOffset(0f);
        xAxis_radar.setXOffset(0f);
        xAxis_radar.setValueFormatter(new ValueFormatter() {

            private final String[] mActivities = new String[]{"Burger", "Steak", "Salad", "Pasta", "Pizza"};

            @Override
            public String getFormattedValue(float value) {
                return mActivities[(int) value % mActivities.length];
            }
        });
        //雷达图指示文字颜色
        xAxis_radar.setTextColor(Color.BLACK);

        YAxis yAxis = radar_chart.getYAxis();
        yAxis.setTypeface(tfLight);
        yAxis.setLabelCount(5, false);
        yAxis.setTextSize(9f);
        yAxis.setAxisMinimum(0f);
        yAxis.setAxisMaximum(80f);
        yAxis.setDrawLabels(false);

        Legend l_radar = radar_chart.getLegend();
        l_radar.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l_radar.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l_radar.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l_radar.setDrawInside(false);
        l_radar.setTypeface(tfLight);
        l_radar.setXEntrySpace(7f);
        l_radar.setYEntrySpace(5f);
        //雷达图说明颜色
        l_radar.setTextColor(Color.BLACK);
        //-----------------------------------------------------------------初始化雷达图

        setData(3, 100);

    }

    protected final String[] parties = new String[]{
            "Party A", "Party B", "Party C", "Party D", "Party E", "Party F", "Party G", "Party H",
            "Party I", "Party J", "Party K", "Party L", "Party M", "Party N", "Party O", "Party P",
            "Party Q", "Party R", "Party S", "Party T", "Party U", "Party V", "Party W", "Party X",
            "Party Y", "Party Z"
    };

    /**
     * 塞入数据
     *
     * @param count 总的类型数
     * @param range 总的百分比数据
     */
    private void setData(int count, float range) {
        ArrayList<PieEntry> entries = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < count; i++) {
            entries.add(new PieEntry((float) ((Math.random() * range) + range / 5),
                    parties[i % parties.length],
                    getResources().getDrawable(R.drawable.star)));
        }

        PieDataSet dataSet = new PieDataSet(entries, "作业正确率结果");

        //不显示图标
        dataSet.setDrawIcons(false);

        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(5f);

        // add a lot of colors

        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        //外面的线
        dataSet.setValueLinePart1OffsetPercentage(80.f);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0.4f);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pie_chart));
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tfLight);
        pie_chart.setData(data);

        // undo all highlights
        pie_chart.highlightValues(null);

        pie_chart.invalidate();

        //--------------------------------------------------------------------------------------

        float start = 1f;

        ArrayList<BarEntry> values = new ArrayList<>();

        for (int i = (int) start; i < start + count; i++) {
            float val = (float) (Math.random() * (range + 1));

            if (Math.random() * 100 < 25) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.star)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }

        BarDataSet set1;

        if (bar_chart.getData() != null &&
                bar_chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) bar_chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            bar_chart.getData().notifyDataChanged();
            bar_chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, "The year 2017");

            set1.setDrawIcons(false);

//            set1.setColors(ColorTemplate.MATERIAL_COLORS);

            /*int startColor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);
            int endColor = ContextCompat.getColor(this, android.R.color.holo_blue_bright);
            set1.setGradientColor(startColor, endColor);*/

            int startColor1 = getResources().getColor(android.R.color.holo_orange_light);
            int startColor2 = getResources().getColor(android.R.color.holo_blue_light);
            int startColor3 = getResources().getColor(android.R.color.holo_orange_light);
            int startColor4 = getResources().getColor(android.R.color.holo_green_light);
            int startColor5 = getResources().getColor(android.R.color.holo_red_light);
            int endColor1 = getResources().getColor(android.R.color.holo_blue_dark);
            int endColor2 = getResources().getColor(android.R.color.holo_purple);
            int endColor3 = getResources().getColor(android.R.color.holo_green_dark);
            int endColor4 = getResources().getColor(android.R.color.holo_red_dark);
            int endColor5 = getResources().getColor(android.R.color.holo_orange_dark);

            List<GradientColor> gradientColors = new ArrayList<>();
            gradientColors.add(new GradientColor(startColor1, endColor1));
            gradientColors.add(new GradientColor(startColor2, endColor2));
            gradientColors.add(new GradientColor(startColor3, endColor3));
            gradientColors.add(new GradientColor(startColor4, endColor4));
            gradientColors.add(new GradientColor(startColor5, endColor5));

            set1.setGradientColors(gradientColors);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data_bar = new BarData(dataSets);
            data_bar.setValueTextSize(10f);
            data_bar.setValueTypeface(tfLight);
            data_bar.setBarWidth(0.9f);

            bar_chart.setData(data_bar);
        }
        //-------------------------------------------------------------------------

        float mul = 80;
        float min = 20;
        int cnt = 5;

        ArrayList<RadarEntry> entries1 = new ArrayList<>();
        ArrayList<RadarEntry> entries2 = new ArrayList<>();

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        for (int i = 0; i < cnt; i++) {
            float val2 = (float) (Math.random() * mul) + min;
            entries2.add(new RadarEntry(val2));
        }


        RadarDataSet set2 = new RadarDataSet(entries2, "This Week");
        set2.setColor(Color.rgb(121, 162, 175));
        set2.setFillColor(Color.rgb(121, 162, 175));
        set2.setDrawFilled(true);
        set2.setFillAlpha(180);
        set2.setLineWidth(2f);
        set2.setDrawHighlightCircleEnabled(true);
        set2.setDrawHighlightIndicators(false);

        ArrayList<IRadarDataSet> sets = new ArrayList<>();
        sets.add(set2);

        RadarData data_radar = new RadarData(sets);
        data_radar.setValueTypeface(tfLight);
        data_radar.setValueTextSize(8f);
        data_radar.setDrawValues(false);
        data_radar.setValueTextColor(Color.WHITE);

        radar_chart.setData(data_radar);
        radar_chart.invalidate();

    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;
        QZXTools.logE("Value: " + e.getY() + ", xIndex: " + e.getX()
                + ", DataSet index: " + h.getDataSetIndex(), null);

    }

    @Override
    public void onNothingSelected() {
        QZXTools.logE("onNothingSelected", null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.report_close:
                break;
            case R.id.report_enter_detail:
                break;
        }
    }

    //饼状图中间文字说明---花样文字暂时不需要
//    private SpannableString generateCenterSpannableText() {
//        SpannableString s = new SpannableString("MPAndroidChart\ndeveloped by Philipp Jahoda");
//        s.setSpan(new RelativeSizeSpan(1.5f), 0, 14, 0);
//        s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
//        s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
//        s.setSpan(new RelativeSizeSpan(.65f), 14, s.length() - 15, 0);
//        s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(), 0);
//        s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()), s.length() - 14, s.length(), 0);
//        return s;
//    }
}
