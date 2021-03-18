package com.telit.zhkt_three.JavaBean.MaiDian;

/**
 * 自主学习埋点
 * <p>
 * 按照科目来进行人数学习的设定
 */
public class SelfLearningVo {
    //学生id
    private String sId;
    //班级id
    private String classId;
    //年级
    private String gradeId;
    //学科id
    private Integer subjectId;
    //学科名称
    private String subjectName;

    //标识   0:参与人数 1.学习次数 2.完成试题数 3.学习资源条数 4.学习时长
    private Integer flag;

    //学习时长
    private Long sUseTime;

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getGradeId() {
        return gradeId;
    }

    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Long getsUseTime() {
        return sUseTime;
    }

    public void setsUseTime(Long sUseTime) {
        this.sUseTime = sUseTime;
    }

    public Integer getFlag() {
        return flag;
    }

    public void setFlag(Integer flag) {
        this.flag = flag;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @Override
    public String toString() {
        return "SelfLearningVo{" +
                "sId='" + sId + '\'' +
                ", classId='" + classId + '\'' +
                ", gradeId='" + gradeId + '\'' +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", flag=" + flag +
                ", sUseTime=" + sUseTime +
                '}';
    }
}
