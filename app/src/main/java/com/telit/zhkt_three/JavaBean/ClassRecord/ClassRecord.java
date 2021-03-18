package com.telit.zhkt_three.JavaBean.ClassRecord;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/30 9:15
 */
public class ClassRecord {
    //活动标题
    private String title;
    //活动id
    private String id;
    //所属活动类型
    private String type;
    //所属活动类型名称
    private String typeName;
    //活动时间
    private String date;

    private String interData;

    private List<ClassRecord> countInterationList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInterData() {
        return interData;
    }

    public void setInterData(String interData) {
        this.interData = interData;
    }

    public List<ClassRecord> getCountInterationList() {
        return countInterationList;
    }

    public void setCountInterationList(List<ClassRecord> countInterationList) {
        this.countInterationList = countInterationList;
    }

    @Override
    public String toString() {
        return "ClassRecord{" +
                "title='" + title + '\'' +
                ", id='" + id + '\'' +
                ", type='" + type + '\'' +
                ", typeName='" + typeName + '\'' +
                ", date='" + date + '\'' +
                ", interData='" + interData + '\'' +
                ", countInterationList=" + countInterationList +
                '}';
    }
}
