package com.telit.zhkt_three.CustomView.JobReport;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/6/5 8:20
 * <p>
 * 作业报告正确率和完成率的视图
 */
public class CustomJobReportChartView extends RelativeLayout {

    private View job_report_head_bg;
    private TextView job_report_head_title;
    private JobReportCircleView JobReportCircleView;
    private TextView job_report_result_one;
    private TextView job_report_result_two;
    private TextView job_report_result_three;
    private TextView job_report_result_four;

    private int type;

    /**
     * 设置类型和结果
     */
    public void setTypeAndResult(int type, String resultOne, String resultTwo) {
        this.type = type;
        if (type == 0) {
            //完成率
            job_report_head_bg.setBackgroundColor(0xFF1282FF);
            job_report_head_title.setText("作业完成率");
            job_report_result_one.setText("完成");
            job_report_result_two.setTextColor(0xFF1282FF);
            job_report_result_three.setText("未完成");
            job_report_result_four.setTextColor(0xFFFF9601);

            JobReportCircleView.setSweepAngleAndTypeResult(360, 0);
        } else {
            //正确率
            job_report_head_bg.setBackgroundColor(0xFF13B5B1);
            job_report_head_title.setText("作业正确率");
            job_report_result_one.setText("正确");
            job_report_result_two.setTextColor(0xFF13B5B1);
            job_report_result_three.setText("错误");
            job_report_result_four.setTextColor(0xFFFF4D4D);

            JobReportCircleView.setSweepAngleAndTypeResult(135, 1);
        }
        job_report_result_two.setText(resultOne);
        job_report_result_four.setText(resultTwo);
    }

    public CustomJobReportChartView(Context context) {
        this(context, null);
    }

    public CustomJobReportChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomJobReportChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_jobreport_view_layout, this, true);

        job_report_head_bg = findViewById(R.id.job_report_head_bg);
        job_report_head_title = findViewById(R.id.job_report_head_title);
        JobReportCircleView = findViewById(R.id.JobReportCircleView);
        job_report_result_one = findViewById(R.id.job_report_result_one);
        job_report_result_two = findViewById(R.id.job_report_result_two);
        job_report_result_three = findViewById(R.id.job_report_result_three);
        job_report_result_four = findViewById(R.id.job_report_result_four);
    }


}
