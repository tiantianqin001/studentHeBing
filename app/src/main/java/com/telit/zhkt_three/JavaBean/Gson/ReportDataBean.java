package com.telit.zhkt_three.JavaBean.Gson;

/**
 * author: qzx
 * Date: 2019/9/29 9:01
 */
public class ReportDataBean {
    private String byhand;

    private ChartBeans chartData;

    public String getByhand() {
        return byhand;
    }

    public void setByhand(String byhand) {
        this.byhand = byhand;
    }

    public ChartBeans getChartData() {
        return chartData;
    }

    public void setChartData(ChartBeans chartData) {
        this.chartData = chartData;
    }

    @Override
    public String toString() {
        return "ReportDataBean{" +
                "byhand='" + byhand + '\'' +
                ", chartData=" + chartData +
                '}';
    }
}
