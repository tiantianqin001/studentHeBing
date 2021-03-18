package com.telit.zhkt_three.JavaBean.HomeWork;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 15:53
 */
public class QuestionInfoByhand {
    private String homeworkId;
    private String index;
    private String attachment;
    private int type;


    private List<QuestionInfo> sheetlist;
    private String answerPublishDate;

    private String comment;


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getAnswerPublishDate() {
        return answerPublishDate;
    }

    public void setAnswerPublishDate(String answerPublishDate) {
        this.answerPublishDate = answerPublishDate;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<QuestionInfo> getSheetlist() {
        return sheetlist;
    }

    public void setSheetlist(List<QuestionInfo> sheetlist) {
        this.sheetlist = sheetlist;
    }

    @Override
    public String toString() {
        return "QuestionInfoByhand{" +
                "homeworkId='" + homeworkId + '\'' +
                ", index='" + index + '\'' +
                ", attachment='" + attachment + '\'' +
                ", type=" + type +
                ", sheetlist=" + sheetlist +
                '}';
    }
}
