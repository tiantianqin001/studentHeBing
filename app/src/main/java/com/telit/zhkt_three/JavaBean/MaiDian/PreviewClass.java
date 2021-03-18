package com.telit.zhkt_three.JavaBean.MaiDian;

import java.util.Date;

/**
 * 课前预习统计
 */
public class PreviewClass {
    //主键
    private Integer id;
    //教师id
    private Long tId;
    //班级id
    private String classId;
    //学科id
    private Integer subjectId;
    //学科名称
    private String subjectName;
    //资源推送次数
    private Integer pushResNum;
    //资源ids
    private String resIds;
    //推送资源数量
    private Integer resCount;
    //学生参与人数
    private Integer sJoinNum;
    //学生学习次数
    private Integer sLearnNum;
    //学习资源条数
    private Integer sLearnResNum;
    //创建日期
    private String createDate;

    //记录用户登录数据
    private String schoolId;
    public String userName;
    //学校名称
    private String schoolName;
    //班级名称
    private String className;
    // 0 代表教师端 1 代表学生端
    private String roletype;
    //省
    private String provinceId;
    private String provinceName;
    //市
    private String cityId;
    private String cityName;
    //区县
    private String districtsId;
    private String districtsName;

    //分组字段
    private String gbParams;

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

    public Integer getPushResNum() {
        return pushResNum;
    }

    public void setPushResNum(Integer pushResNum) {
        this.pushResNum = pushResNum;
    }

    public String getResIds() {
        return resIds;
    }

    public void setResIds(String resIds) {
        this.resIds = resIds;
    }

    public Integer getResCount() {
        return resCount;
    }

    public void setResCount(Integer resCount) {
        this.resCount = resCount;
    }

    public Integer getsJoinNum() {
        return sJoinNum;
    }

    public void setsJoinNum(Integer sJoinNum) {
        this.sJoinNum = sJoinNum;
    }

    public Integer getsLearnNum() {
        return sLearnNum;
    }

    public void setsLearnNum(Integer sLearnNum) {
        this.sLearnNum = sLearnNum;
    }

    public Integer getsLearnResNum() {
        return sLearnResNum;
    }

    public void setsLearnResNum(Integer sLearnResNum) {
        this.sLearnResNum = sLearnResNum;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getRoletype() {
        return roletype;
    }

    public void setRoletype(String roletype) {
        this.roletype = roletype;
    }

    public String getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(String provinceId) {
        this.provinceId = provinceId;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDistrictsId() {
        return districtsId;
    }

    public void setDistrictsId(String districtsId) {
        this.districtsId = districtsId;
    }

    public String getDistrictsName() {
        return districtsName;
    }

    public void setDistrictsName(String districtsName) {
        this.districtsName = districtsName;
    }

    public String getGbParams() {
        return gbParams;
    }

    public void setGbParams(String gbParams) {
        this.gbParams = gbParams;
    }

    @Override
    public String toString() {
        return "PreviewClass{" +
                "id=" + id +
                ", tId=" + tId +
                ", classId='" + classId + '\'' +
                ", subjectId=" + subjectId +
                ", subjectName='" + subjectName + '\'' +
                ", pushResNum=" + pushResNum +
                ", resIds='" + resIds + '\'' +
                ", resCount=" + resCount +
                ", sJoinNum=" + sJoinNum +
                ", sLearnNum=" + sLearnNum +
                ", sLearnResNum=" + sLearnResNum +
                ", createDate='" + createDate + '\'' +
                ", schoolId='" + schoolId + '\'' +
                ", userName='" + userName + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", className='" + className + '\'' +
                ", roletype='" + roletype + '\'' +
                ", provinceId='" + provinceId + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", cityId='" + cityId + '\'' +
                ", cityName='" + cityName + '\'' +
                ", districtsId='" + districtsId + '\'' +
                ", districtsName='" + districtsName + '\'' +
                ", gbParams='" + gbParams + '\'' +
                '}';
    }
}
