package com.telit.zhkt_three.JavaBean.Gson;

/**
 * author: qzx
 * Date: 2019/5/23 13:44
 */
public class JpushExtrasInfo {
    private String extra1;
    private String result;
    private String warn;
    private String byhand;

    public String getExtra1() {
        return extra1;
    }

    public void setExtra1(String extra1) {
        this.extra1 = extra1;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getWarn() {
        return warn;
    }

    public void setWarn(String warn) {
        this.warn = warn;
    }

    public String getByhand() {
        return byhand;
    }

    public void setByhand(String byhand) {
        this.byhand = byhand;
    }

    @Override
    public String toString() {
        return "JpushExtrasInfo{" +
                "extra1='" + extra1 + '\'' +
                ", result='" + result + '\'' +
                ", warn='" + warn + '\'' +
                ", byhand='" + byhand + '\'' +
                '}';
    }
}
