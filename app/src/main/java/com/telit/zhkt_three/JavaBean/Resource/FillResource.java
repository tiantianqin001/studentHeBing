package com.telit.zhkt_three.JavaBean.Resource;

import java.io.Serializable;

/**
 * author: qzx
 * Date: 2019/6/10 9:28
 * <p>
 * 填充Adapter的资源javabean
 */
public class FillResource implements Serializable {
    private String id;
    private String title;
    private String pressname;
    private String cover;
    private String termname;
    private String gradename;
    private String type;
    private boolean isTeachingMaterial;
    private String subjectName;
    private boolean isItemBank;

    private String subjectId;

    private String knowledgeId;
    private String xd;
    private String chid;

    private String filePath;

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

    public String getPressname() {
        return pressname;
    }

    public void setPressname(String pressname) {
        this.pressname = pressname;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTermname() {
        return termname;
    }

    public void setTermname(String termname) {
        this.termname = termname;
    }

    public String getGradename() {
        return gradename;
    }

    public void setGradename(String gradename) {
        this.gradename = gradename;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTeachingMaterial() {
        return isTeachingMaterial;
    }

    public void setTeachingMaterial(boolean teachingMaterial) {
        isTeachingMaterial = teachingMaterial;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isItemBank() {
        return isItemBank;
    }

    public void setItemBank(boolean itemBank) {
        isItemBank = itemBank;
    }

    public String getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(String knowledgeId) {
        this.knowledgeId = knowledgeId;
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

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }


    @Override
    public String toString() {
        return "FillResource{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", pressname='" + pressname + '\'' +
                ", cover='" + cover + '\'' +
                ", termname='" + termname + '\'' +
                ", gradename='" + gradename + '\'' +
                ", type='" + type + '\'' +
                ", isTeachingMaterial=" + isTeachingMaterial +
                ", subjectName='" + subjectName + '\'' +
                ", isItemBank=" + isItemBank +
                ", subjectId='" + subjectId + '\'' +
                ", knowledgeId='" + knowledgeId + '\'' +
                ", xd='" + xd + '\'' +
                ", chid='" + chid + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}
