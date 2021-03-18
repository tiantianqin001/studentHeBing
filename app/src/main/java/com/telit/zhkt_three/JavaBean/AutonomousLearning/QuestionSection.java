package com.telit.zhkt_three.JavaBean.AutonomousLearning;

/**
 * author: qzx
 * Date: 2019/5/17 19:23
 * 学段信息
 */
public class QuestionSection {
    private Integer id;
    //学科名称
    private String xdName;
    //学段
    private Integer xd;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getXdName() {
        return xdName;
    }

    public void setXdName(String xdName) {
        this.xdName = xdName;
    }

    public Integer getXd() {
        return xd;
    }

    public void setXd(Integer xd) {
        this.xd = xd;
    }

    @Override
    public String toString() {
        return "QuestionSection{" +
                "id=" + id +
                ", xdName='" + xdName + '\'' +
                ", xd=" + xd +
                '}';
    }
}
