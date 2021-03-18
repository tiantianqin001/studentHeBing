package com.telit.zhkt_three.JavaBean.ClassRecord;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/26 19:53
 * <p>
 * 4个ClassRecordTwo为一组
 */
public class OrderByDateClassRecord {
    private String SameDate;
    private List<ClassRecordTwo> classRecordTwos;

    public String getSameDate() {
        return SameDate;
    }

    public void setSameDate(String sameDate) {
        SameDate = sameDate;
    }

    public List<ClassRecordTwo> getClassRecordTwos() {
        return classRecordTwos;
    }

    public void setClassRecordTwos(List<ClassRecordTwo> classRecordTwos) {
        this.classRecordTwos = classRecordTwos;
    }
}
