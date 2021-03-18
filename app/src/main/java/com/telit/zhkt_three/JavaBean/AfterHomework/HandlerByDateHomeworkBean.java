package com.telit.zhkt_three.JavaBean.AfterHomework;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/7/1 14:04
 */
public class HandlerByDateHomeworkBean {
    private String SameDate;
    private List<AfterHomeworkBean> afterHomeworkBeans;

    public String getSameDate() {
        return SameDate;
    }

    public void setSameDate(String sameDate) {
        SameDate = sameDate;
    }

    public List<AfterHomeworkBean> getAfterHomeworkBeans() {
        return afterHomeworkBeans;
    }

    public void setAfterHomeworkBeans(List<AfterHomeworkBean> afterHomeworkBeans) {
        this.afterHomeworkBeans = afterHomeworkBeans;
    }

    @Override
    public String toString() {
        return "HandlerByDateHomeworkBean{" +
                "SameDate='" + SameDate + '\'' +
                ", afterHomeworkBeans=" + afterHomeworkBeans +
                '}';
    }
}
