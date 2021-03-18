package com.telit.zhkt_three.JavaBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 项目名称：desktop
 * 类名称：StudentInfo
 * 类描述：学生登陆信息（与服务器端返回一致）
 * 创建人：luxun
 * 创建时间：2017/2/23 19:53
 * 修改人：luxun
 * 修改时间：2017/2/23 19:53
 * 当前版本：v1.0
 */

@Entity
public class StudentInfo {

    /**
     * studentId : 222
     * schoolId : 9021fcddd00840b2ada19671d4a51f37
     * userId : 1
     * gradeId : 1
     * classId : 1
     * studentName : 1
     * dateOfBirth : 2017-02-07
     * sex : 女
     * specialty : 1
     * remark : 1
     * gradeName : 一年级
     * className : 一班
     * photo : http://192.168.110.126:8080/jeeplus/userfiles/1/images/grkj.png
     * schoolName : 上派初级中学
     * loginName : admin
     * token : 1_dc9285e8c11548f2aea450bf5573275c
     */

    private String studentId;
    private String schoolId;
    @Id
    private String userId;
    private String gradeId;
    private String classId;
    private String studentName;
    private String dateOfBirth;
    private String sex;
    private String specialty;
    private String remark;
    private String gradeName;
    private String className;
    private String photo;
    private String schoolName;
    private String loginName;
    private String token;

    private String classShortId;	//班级短id


    @Generated(hash = 1011282740)
    public StudentInfo(String studentId, String schoolId, String userId,
            String gradeId, String classId, String studentName, String dateOfBirth,
            String sex, String specialty, String remark, String gradeName,
            String className, String photo, String schoolName, String loginName,
            String token, String classShortId) {
        this.studentId = studentId;
        this.schoolId = schoolId;
        this.userId = userId;
        this.gradeId = gradeId;
        this.classId = classId;
        this.studentName = studentName;
        this.dateOfBirth = dateOfBirth;
        this.sex = sex;
        this.specialty = specialty;
        this.remark = remark;
        this.gradeName = gradeName;
        this.className = className;
        this.photo = photo;
        this.schoolName = schoolName;
        this.loginName = loginName;
        this.token = token;
        this.classShortId = classShortId;
    }


    @Generated(hash = 2016856731)
    public StudentInfo() {
    }

    @Override
    public String toString() {
        return "StudentInfo{" +
                "studentId='" + studentId + '\'' +
                ", schoolId='" + schoolId + '\'' +
                ", userId='" + userId + '\'' +
                ", gradeId='" + gradeId + '\'' +
                ", classId='" + classId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", sex='" + sex + '\'' +
                ", specialty='" + specialty + '\'' +
                ", remark='" + remark + '\'' +
                ", gradeName='" + gradeName + '\'' +
                ", className='" + className + '\'' +
                ", photo='" + photo + '\'' +
                ", schoolName='" + schoolName + '\'' +
                ", loginName='" + loginName + '\'' +
                ", token='" + token + '\'' +
                ", classShortId='" + classShortId + '\'' +
                '}';
    }

    public String getStudentId() {
        return this.studentId;
    }


    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


    public String getSchoolId() {
        return this.schoolId;
    }


    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }


    public String getUserId() {
        return this.userId;
    }


    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getGradeId() {
        return this.gradeId;
    }


    public void setGradeId(String gradeId) {
        this.gradeId = gradeId;
    }


    public String getClassId() {
        return this.classId;
    }


    public void setClassId(String classId) {
        this.classId = classId;
    }


    public String getStudentName() {
        return this.studentName;
    }


    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


    public String getDateOfBirth() {
        return this.dateOfBirth;
    }


    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }


    public String getSex() {
        return this.sex;
    }


    public void setSex(String sex) {
        this.sex = sex;
    }


    public String getSpecialty() {
        return this.specialty;
    }


    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }


    public String getRemark() {
        return this.remark;
    }


    public void setRemark(String remark) {
        this.remark = remark;
    }


    public String getGradeName() {
        return this.gradeName;
    }


    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }


    public String getClassName() {
        return this.className;
    }


    public void setClassName(String className) {
        this.className = className;
    }


    public String getPhoto() {
        return this.photo;
    }


    public void setPhoto(String photo) {
        this.photo = photo;
    }


    public String getSchoolName() {
        return this.schoolName;
    }


    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }


    public String getLoginName() {
        return this.loginName;
    }


    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }


    public String getToken() {
        return this.token;
    }


    public void setToken(String token) {
        this.token = token;
    }


    public String getClassShortId() {
        return this.classShortId;
    }


    public void setClassShortId(String classShortId) {
        this.classShortId = classShortId;
    }
}
