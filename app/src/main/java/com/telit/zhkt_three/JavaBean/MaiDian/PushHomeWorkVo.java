package com.telit.zhkt_three.JavaBean.MaiDian;

/**
 * 作业推送统计
 *
 * 教师端推送次数埋点、学生端接收后完成字数埋点
 */
public class PushHomeWorkVo {
    /** 主键 */
    private Integer id;

    /** 教师id */
    private Long tId;

    private Integer gradeId;
    /** 班级id */
    private String classId;

    /** 学科id */
    private Integer subjectId;

    /** 学科名称 */
    private String subjectName;

    /** 作业ids */
    private String hwIds;

    /** 课后作业推送次数 */
    private Integer pushHwNum;

    /** 题目数 */
    private Integer hwQuestionNum;

    /** 完成作业次数 */
    private Integer sCompleteHwNum;

    /** 学生完成题目数 */
    private Integer sCompleteQuestionNum;

    /** sCompleteQuestionTime */
    private Long sCompleteQuestionTime;

    /** createDate */
    private String createDate;

    private String flag;//1.课后作业推送次数（作业题数） 2完成作业次数（题目数，时间）.

    private String cityName;

    private String districtsName;

    private String schoolName;

    private String userName;

    private String  userId;

    private String className;
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long gettId() {
        return tId;
    }

    public void settId(Long tId) {
        this.tId = tId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getHwIds() {
        return hwIds;
    }

    public void setHwIds(String hwIds) {
        this.hwIds = hwIds;
    }

    public Integer getPushHwNum() {
        return pushHwNum;
    }

    public void setPushHwNum(Integer pushHwNum) {
        this.pushHwNum = pushHwNum;
    }

    public Integer getHwQuestionNum() {
        return hwQuestionNum;
    }

    public void setHwQuestionNum(Integer hwQuestionNum) {
        this.hwQuestionNum = hwQuestionNum;
    }

    public Integer getsCompleteHwNum() {
        return sCompleteHwNum;
    }

    public void setsCompleteHwNum(Integer sCompleteHwNum) {
        this.sCompleteHwNum = sCompleteHwNum;
    }

    public Integer getsCompleteQuestionNum() {
        return sCompleteQuestionNum;
    }

    public void setsCompleteQuestionNum(Integer sCompleteQuestionNum) {
        this.sCompleteQuestionNum = sCompleteQuestionNum;
    }

    public Long getsCompleteQuestionTime() {
        return sCompleteQuestionTime;
    }

    public void setsCompleteQuestionTime(Long sCompleteQuestionTime) {
        this.sCompleteQuestionTime = sCompleteQuestionTime;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictsName() {
        return districtsName;
    }

    public void setDistrictsName(String districtsName) {
        this.districtsName = districtsName;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return "PushHomeWorkVo{" +
                "id=" + id +
                ", tId=" + tId +
                ", gradeId=" + gradeId +
                ", classId='" + classId + '\'' +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", hwIds='" + hwIds + '\'' +
                ", pushHwNum=" + pushHwNum +
                ", hwQuestionNum=" + hwQuestionNum +
                ", sCompleteHwNum=" + sCompleteHwNum +
                ", sCompleteQuestionNum=" + sCompleteQuestionNum +
                ", sCompleteQuestionTime=" + sCompleteQuestionTime +
                ", createDate='" + createDate + '\'' +
                ", flag='" + flag + '\'' +
                ", cityName='" + cityName + '\'' +
                ", districtsName='" + districtsName + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}
