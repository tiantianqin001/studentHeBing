package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.os.Bundle;
import android.widget.ImageView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.CustomView.JobReport.CustomJobReportChartView;
import com.telit.zhkt_three.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class JobReportActivity extends BaseActivity {

    private Unbinder unbinder;

    @BindView(R.id.job_report_back)
    ImageView job_report_back;
    @BindView(R.id.job_report_chart_complete_rate)
    CustomJobReportChartView job_report_chart_complete_rate;
    @BindView(R.id.job_report_chart_right_rate)
    CustomJobReportChartView job_report_chart_right_rate;
    @BindView(R.id.job_report_xRecycler)
    XRecyclerView xRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_report);
        unbinder = ButterKnife.bind(this);

        job_report_chart_complete_rate.setTypeAndResult(0, "5题", "0题");
        job_report_chart_right_rate.setTypeAndResult(1, "4题", "1题");
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }
}
