package com.telit.zhkt_three.JavaBean.MicroClass;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/25 17:36
 */
public class OrderByDateMicBean {
    private String SameDate;
    private List<ActualMicBean> actualMicBeans;

    public String getSameDate() {
        return SameDate;
    }

    public void setSameDate(String sameDate) {
        SameDate = sameDate;
    }

    public List<ActualMicBean> getActualMicBeans() {
        return actualMicBeans;
    }

    public void setActualMicBeans(List<ActualMicBean> actualMicBeans) {
        this.actualMicBeans = actualMicBeans;
    }
}
