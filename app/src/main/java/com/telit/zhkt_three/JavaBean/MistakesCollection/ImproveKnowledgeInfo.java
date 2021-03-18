package com.telit.zhkt_three.JavaBean.MistakesCollection;

/**
 * author: qzx
 * Date: 2019/6/24 21:25
 */
public class ImproveKnowledgeInfo {
    private String tid;
    private String name;

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "ImproveKnowledgeInfo{" +
                "tid='" + tid + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
