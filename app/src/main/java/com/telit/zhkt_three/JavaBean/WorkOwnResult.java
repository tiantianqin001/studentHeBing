package com.telit.zhkt_three.JavaBean;

/**
 * author: qzx
 * Date: 2019/9/27 15:53
 *
 * 用于ownList中的子项类
 */
public class WorkOwnResult {
    // '回答对错：0：错，1：对2：其他'
    private String state;
    private String score;
    private String answerContent;

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

    public String getAnswerContent() {
        return answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    @Override
    public String toString() {
        return "WorkOwnResult{" +
                "state='" + state + '\'' +
                ", score='" + score + '\'' +
                ", answerContent='" + answerContent + '\'' +
                '}';
    }
}
