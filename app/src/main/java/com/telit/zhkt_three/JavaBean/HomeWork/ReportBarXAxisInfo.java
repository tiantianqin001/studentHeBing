package com.telit.zhkt_three.JavaBean.HomeWork;

import android.support.annotation.NonNull;

/**
 * author: qzx
 * Date: 2019/9/29 9:57
 *
 * 排序JavaBean
 */
public class ReportBarXAxisInfo implements Comparable<ReportBarXAxisInfo> {

    private int index;
    private String questionType;
    private int myScore;
    private int totalScore;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public int getMyScore() {
        return myScore;
    }

    public void setMyScore(int myScore) {
        this.myScore = myScore;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public String toString() {
        return "ReportBarXAxisInfo{" +
                "index=" + index +
                ", questionType='" + questionType + '\'' +
                ", myScore='" + myScore + '\'' +
                ", totalScore='" + totalScore + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NonNull ReportBarXAxisInfo o) {
        return this.index - o.getIndex();//默认是升序
    }
}
