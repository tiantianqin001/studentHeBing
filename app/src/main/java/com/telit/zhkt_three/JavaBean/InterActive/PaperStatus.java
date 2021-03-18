package com.telit.zhkt_three.JavaBean.InterActive;

/**
 * 项目名称：desktop
 * 类名称：PaperStatus
 * 类描述：试卷状态
 * 创建人：luxun
 * 创建时间：2017/4/26 0026 11:32
 * 修改人：luxun
 * 修改时间：2017/4/26 0026 11:32
 * 当前版本：v1.0
 */

public class PaperStatus {
    private String studentId;
    private int status;

    public PaperStatus() {
    }

    public PaperStatus(String studentId, int status) {
        this.studentId = studentId;
        this.status = status;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
