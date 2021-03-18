package com.telit.zhkt_three.JavaBean.AutonomousLearning;

import java.util.List;

/**
 * @ClassName QuestionData
 * @Description 题库知识点实体类
 * @Author baoXu
 * @Date 2019/5/14 21:03
 * <p>
 * 这个类查询知识点左侧的章节信息
 */
public class QuestionKnowledge {
    //主键
    private Integer id;
    //知识点名称
    private String name;
    //父类节点
    private String parentId;
    //知识点ID
    private Integer knowledgeId;
    //是否有子节点
    private Integer haschild;
    //创建日期
    private Integer createTime;
    //更新日期
    private String updateTime;
    //学段
    private String xd;
    //学科
    private String chid;

    private String tikuType;

    private List<QuestionKnowledge> questionKnowledgeList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Integer knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public Integer getHaschild() {
        return haschild;
    }

    public void setHaschild(Integer haschild) {
        this.haschild = haschild;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getXd() {
        return xd;
    }

    public void setXd(String xd) {
        this.xd = xd;
    }

    public String getChid() {
        return chid;
    }

    public void setChid(String chid) {
        this.chid = chid;
    }

    public String getTikuType() {
        return tikuType;
    }

    public void setTikuType(String tikuType) {
        this.tikuType = tikuType;
    }

    public List<QuestionKnowledge> getQuestionKnowledgeList() {
        return questionKnowledgeList;
    }

    public void setQuestionKnowledgeList(List<QuestionKnowledge> questionKnowledgeList) {
        this.questionKnowledgeList = questionKnowledgeList;
    }

    @Override
    public String toString() {
        return "QuestionKnowledge{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", parentId='" + parentId + '\'' +
                ", knowledgeId=" + knowledgeId +
                ", haschild=" + haschild +
                ", createTime=" + createTime +
                ", updateTime='" + updateTime + '\'' +
                ", xd='" + xd + '\'' +
                ", chid='" + chid + '\'' +
                ", tikuType='" + tikuType + '\'' +
                ", questionKnowledgeList=" + questionKnowledgeList +
                '}';
    }
}
