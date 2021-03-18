package com.telit.zhkt_three.JavaBean.UnityResource;

public class StudentBean {

    /**
     * state : 0
     * score : 0
     * attachment : null
     * answerContent : 1
     * answerContentJson : null
     */

    private String state;
    private String score;
    private Object attachment;
    private String answerContent;
    private Object answerContentJson;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public Object getAttachment() {
        return attachment;
    }

    public void setAttachment(Object attachment) {
        this.attachment = attachment;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public Object getAnswerContentJson() {
        return answerContentJson;
    }

    public void setAnswerContentJson(Object answerContentJson) {
        this.answerContentJson = answerContentJson;
    }
}
