package com.telit.zhkt_three.JavaBean.ClassRecord;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/27 11:19
 */
public class ActualOrderClassRecord {
    private String sameDate;
    private ClassRecordTwo one;
    private ClassRecordTwo two;
    private ClassRecordTwo three;
    private ClassRecordTwo four;
    private int type;//head 1 mid 2 foot 3 [end 4 数据全部加载了，有底线] OnlyOneRow 5
    private boolean isLast = false;
    private boolean isFirst = false;

    public String getSameDate() {
        return sameDate;
    }

    public void setSameDate(String sameDate) {
        this.sameDate = sameDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean first) {
        isFirst = first;
    }

    public ClassRecordTwo getOne() {
        return one;
    }

    public void setOne(ClassRecordTwo one) {
        this.one = one;
    }

    public ClassRecordTwo getTwo() {
        return two;
    }

    public void setTwo(ClassRecordTwo two) {
        this.two = two;
    }

    public ClassRecordTwo getThree() {
        return three;
    }

    public void setThree(ClassRecordTwo three) {
        this.three = three;
    }

    public ClassRecordTwo getFour() {
        return four;
    }

    public void setFour(ClassRecordTwo four) {
        this.four = four;
    }

}
