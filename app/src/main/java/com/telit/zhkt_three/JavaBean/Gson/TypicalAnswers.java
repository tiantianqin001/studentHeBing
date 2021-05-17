package com.telit.zhkt_three.JavaBean.Gson;

import java.util.List;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/3/31 12:47
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class TypicalAnswers {
    private String studentName;
    private List<String> attachmentArr;

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public List<String> getAttachmentArr() {
        return attachmentArr;
    }

    public void setAttachmentArr(List<String> attachmentArr) {
        this.attachmentArr = attachmentArr;
    }
}
