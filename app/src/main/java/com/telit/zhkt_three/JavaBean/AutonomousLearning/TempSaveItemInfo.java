package com.telit.zhkt_three.JavaBean.AutonomousLearning;

/**
 * author: qzx
 * Date: 2019/6/15 10:51
 */
public class TempSaveItemInfo {
    private int index;
    private String key;
    private String value;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "TempSaveItemInfo{" +
                "index=" + index +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
