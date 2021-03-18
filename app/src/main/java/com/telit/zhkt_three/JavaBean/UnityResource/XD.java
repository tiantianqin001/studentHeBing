package com.telit.zhkt_three.JavaBean.UnityResource;

public class XD {
    private int xdId;
    private String xdName;

    public int getXdId() {
        return xdId;
    }

    public void setXdId(int xdId) {
        this.xdId = xdId;
    }

    public String getXdName() {
        return xdName;
    }

    public void setXdName(String xdName) {
        this.xdName = xdName;
    }

    @Override
    public String toString() {
        return "XD{" +
                "xdId=" + xdId +
                ", xdName='" + xdName + '\'' +
                '}';
    }
}
