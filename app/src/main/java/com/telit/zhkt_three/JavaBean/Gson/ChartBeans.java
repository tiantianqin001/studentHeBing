package com.telit.zhkt_three.JavaBean.Gson;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/10/9 14:36
 */
public class ChartBeans {
    private List<ReportBean> pieChartData;

    private List<ReportBean> radarChartData;

    private List<ReportBean> barChartData;

    public List<ReportBean> getPieChartData() {
        return pieChartData;
    }

    public void setPieChartData(List<ReportBean> pieChartData) {
        this.pieChartData = pieChartData;
    }

    public List<ReportBean> getRadarChartData() {
        return radarChartData;
    }

    public void setRadarChartData(List<ReportBean> radarChartData) {
        this.radarChartData = radarChartData;
    }

    public List<ReportBean> getBarChartData() {
        return barChartData;
    }

    public void setBarChartData(List<ReportBean> barChartData) {
        this.barChartData = barChartData;
    }


    public static class ReportBean {
        private String workId;
        private int score;
        private int total;
        private String qindex;
        // 0 单选 1 多选 2 填空 3 主观 4连线 5 判断
        private int questionType;
        private String scoreRate;
        // 0 错误 1 正确 2 客观
        private int status;

        public String getWorkId() {
            return workId;
        }

        public void setWorkId(String workId) {
            this.workId = workId;
        }

        public int getScore() {
            return score;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public String getQindex() {
            return qindex;
        }

        public void setQindex(String qindex) {
            this.qindex = qindex;
        }

        public int getQuestionType() {
            return questionType;
        }

        public void setQuestionType(int questionType) {
            this.questionType = questionType;
        }

        public String getScoreRate() {
            return scoreRate;
        }

        public void setScoreRate(String scoreRate) {
            this.scoreRate = scoreRate;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        @Override
        public String toString() {
            return "ReportBean{" +
                    "workId='" + workId + '\'' +
                    ", score=" + score +
                    ", total=" + total +
                    ", qindex=" + qindex +
                    ", questionType=" + questionType +
                    ", scoreRate='" + scoreRate + '\'' +
                    ", status=" + status +
                    '}';
        }
    }
}
