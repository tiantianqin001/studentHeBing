package com.telit.zhkt_three.Activity.InteractiveScreen;

/**
 * author: qzx
 * Date: 2019/11/13 14:35
 * <p>
 * 需要新增className、studentName classId
 */
public class InteractionAnswerVO {
    private long id;
    private long interactionDetailId;
    private String useTime;
    private String answerText;
    private String studentId;
    private String creater;
    private String createDate;
    private String photo;
    private String userId;
    private String letter;
    private String answeringProcessUrl;
    private String thumbnail;

    private String classId;
    private String className;
    private String studentName;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getInteractionDetailId() {
        return interactionDetailId;
    }

    public void setInteractionDetailId(long interactionDetailId) {
        this.interactionDetailId = interactionDetailId;
    }

    public String getUseTime() {
        return useTime;
    }

    public void setUseTime(String useTime) {
        this.useTime = useTime;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLetter() {
        return letter;
    }

    public void setLetter(String letter) {
        this.letter = letter;
    }

    public String getAnsweringProcessUrl() {
        return answeringProcessUrl;
    }

    public void setAnsweringProcessUrl(String answeringProcessUrl) {
        this.answeringProcessUrl = answeringProcessUrl;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
}
