package com.telit.zhkt_three.JavaBean;

/**
 * author: qzx
 * Date: 2019/4/1 15:10
 */
public class EventMsg {
    private int appChangeType;
    private String appPackageName;

    public int getAppChangeType() {
        return appChangeType;
    }

    public void setAppChangeType(int appChangeType) {
        this.appChangeType = appChangeType;
    }

    public String getAppPackageName() {
        return appPackageName;
    }

    public void setAppPackageName(String appPackageName) {
        this.appPackageName = appPackageName;
    }

    @Override
    public String toString() {
        return "EventMsg{" +
                "appChangeType=" + appChangeType +
                ", appPackageName='" + appPackageName + '\'' +
                '}';
    }
}
