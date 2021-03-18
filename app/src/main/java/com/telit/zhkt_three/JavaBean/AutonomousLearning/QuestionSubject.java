package com.telit.zhkt_three.JavaBean.AutonomousLearning;

/**
 * @ClassName QuestionSubject
 * @Description TODO
 * @Author baoXu
 * @Date 2019/5/15 20:09
 *
 * 试题学科
 */
public class QuestionSubject {
    //主键
    private Integer id;
    //学科id
    private Integer chid;
    //学科名称
    private String chname;
    //学段
    private Integer xd;

    public QuestionSubject(){

    }

    public QuestionSubject(Integer xd){
        this.xd = xd;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChid() {
        return chid;
    }

    public void setChid(Integer chid) {
        this.chid = chid;
    }

    public String getChname() {
        return chname;
    }

    public void setChname(String chname) {
        this.chname = chname;
    }

    public Integer getXd() {
        return xd;
    }

    public void setXd(Integer xd) {
        this.xd = xd;
    }

    @Override
    public String toString() {
        return "QuestionSubject{" +
                "id=" + id +
                ", chid=" + chid +
                ", chname='" + chname + '\'' +
                ", xd=" + xd +
                '}';
    }
}
