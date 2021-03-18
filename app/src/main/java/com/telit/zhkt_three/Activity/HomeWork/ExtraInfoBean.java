package com.telit.zhkt_three.Activity.HomeWork;

/**
 * author: qzx
 * Date: 2019/6/29 9:52
 */
public class ExtraInfoBean {
    private String flag;
    private String filePath;
    private String questionId;

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    @Override
    public String toString() {
        return "ExtraInfoBean{" +
                "flag='" + flag + '\'' +
                ", filePath='" + filePath + '\'' +
                ", questionId='" + questionId + '\'' +
                '}';
    }
}
