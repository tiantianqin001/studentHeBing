package com.telit.zhkt_three.JavaBean.AfterHomework;

/**
 * 项目名称：desktop
 * 类名称： UnfinishHomeworkListBean
 * 类说明： 收藏题目界面的列表界面
 * 创建人  王磊
 * 创建时间： 2017/3/13
 * 修改人： 王磊
 * 修改时间： 2017/3/13
 * 当前版本： v1.0
 */
public class CollectQuestionBean {

    /*{
    "studentId":"66666759309",
    "questionId":"111",
    "createTime":"2021-01-28 18:39:44",
    "id":"48a18838ed5840cb873a8228e3532c1a",
    "title":"1111"
}*/

    private String id;
    private String title;
    private String questionId;
    private String studentId;
    private String createTime;
    private String subjectId;
    private String collectId;
    private String homeworkId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }
}
