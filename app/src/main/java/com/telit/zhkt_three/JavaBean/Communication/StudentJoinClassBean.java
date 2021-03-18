package com.telit.zhkt_three.JavaBean.Communication;

/**
 * author: qzx
 * Date: 2019/5/13 13:56
 * <p>
 * 性别、姓名、头像URL、学生ID
 */
public class StudentJoinClassBean {
    private String studentId;
    private String sex;
    private String photo;
    private String studentName;
    private String ip;
    private String rtspUrl;

    public String getRtspUrl() {
        return rtspUrl;
    }

    public void setRtspUrl(String rtspUrl) {
        this.rtspUrl = rtspUrl;
    }

    public String getStudentId() {
        return studentId;
    }
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }


    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "StudentJoinClassBean{" +
                "studentId='" + studentId + '\'' +
                ", sex='" + sex + '\'' +
                ", photo='" + photo + '\'' +
                ", studentName='" + studentName + '\'' +
                '}';
    }
}
