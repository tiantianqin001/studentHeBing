package com.telit.zhkt_three.JavaBean.AfterHomework;

/**
 * 项目名称：desktop
 * 类名称： UnfinishHomeworkListBean
 * 类说明： 家庭作业界面的列表界面
 * 创建人  王磊
 * 创建时间： 2017/3/13
 * 修改人： 王磊
 * 修改时间： 2017/3/13
 * 当前版本： v1.0
 */
public class AfterHomeworkBean {

    /**
     * {
     * "id": "e3a2704e03904a80bd34a40f7546c4a6",
     * "name": "2019年5月29日作业",
     * "description": null,
     * "attachment": null,
     * "subjectId": "0",
     * "status": "0",
     * "startDate": "2019-05-29 09:29:00",
     * "endDate": "2019-05-29 09:29:35",
     * "byHand": "1"
     * }
     */

    private String id;
    private String name;
    private String description;
    private String attachment;
    private String subjectId;
    private String status;
    private String startDate;
    private String endDate;
    private String byHand;
    private String showAnswerDate;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getByHand() {
        return byHand;
    }

    public void setByHand(String byHand) {
        this.byHand = byHand;
    }

    public String getShowAnswerDate() {
        return showAnswerDate;
    }

    public void setShowAnswerDate(String showAnswerDate) {
        this.showAnswerDate = showAnswerDate;
    }

    @Override
    public String toString() {
        return "AfterHomeworkBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", attachment='" + attachment + '\'' +
                ", subjectId='" + subjectId + '\'' +
                ", status='" + status + '\'' +
                ", startDate='" + startDate + '\'' +
                ", endDate='" + endDate + '\'' +
                ", byHand='" + byHand + '\'' +
                '}';
    }
}
