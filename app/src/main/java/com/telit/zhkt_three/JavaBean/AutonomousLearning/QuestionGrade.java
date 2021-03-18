package com.telit.zhkt_three.JavaBean.AutonomousLearning;

/**
 * @ClassName QuestionGrade
 * @Description TODO
 * @Author baoXu
 * @Date 2019/5/15 20:13
 *
 * 试题对应的年级
 */
public class QuestionGrade {

    //主键
    private Integer id;
    //年级id
    private Integer gradeId;
    //年级名称
    private String gradeName;
    //学段
    private Integer xd;

    public QuestionGrade(){

    }

    public QuestionGrade(Integer xd){
        this.xd = xd;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public Integer getXd() {
        return xd;
    }

    public void setXd(Integer xd) {
        this.xd = xd;
    }

    @Override
    public String toString() {
        return "QuestionGrade{" +
                "id=" + id +
                ", gradeId=" + gradeId +
                ", gradeName='" + gradeName + '\'' +
                ", xd=" + xd +
                '}';
    }
}
