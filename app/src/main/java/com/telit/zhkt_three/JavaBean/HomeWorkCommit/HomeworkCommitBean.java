package com.telit.zhkt_three.JavaBean.HomeWorkCommit;

/**
 * 项目名称：desktop
 * 类名称：${CLASS_NAME}
 * 类描述：
 * 创建人：luxun
 * 创建时间：2017/4/12 0012 20:11
 * 修改人：luxun
 * 修改时间：2017/4/12 0012 20:11
 * 当前版本：v1.0
 *
 * 连线题把连线的结果拼接成字符串塞入answerContent
 */

public class HomeworkCommitBean {

    private String questionId;
    private String answerId;
    private String answerContent;
    private String blanknum;
    private String homeworkId;
    private String studentId;
    private String classId;

    public String getBlanknum() {
        return blanknum;
    }

    public void setBlanknum(String blanknum) {
        this.blanknum = blanknum;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    @Override
    public String toString() {
        return "HomeworkCommitBean{" +
                "questionId='" + questionId + '\'' +
                ", answerId='" + answerId + '\'' +
                ", answerContent='" + answerContent + '\'' +
                ", homeworkId='" + homeworkId + '\'' +
                ", studentId='" + studentId + '\'' +
                ", classId='" + classId + '\'' +
                ", blanknum='" + blanknum + '\'' +
                '}';
    }
}
