package com.telit.zhkt_three.JavaBean.ClassRecord;

/**
 * author: qzx
 * Date: 2019/12/26 18:20
 */
public class ClassRecordTwo {
    private String type;
    private String recordId;
    private String createDate;
    private String fileUrl;//截屏分享的Url
    private String recordName;
    private String userName;//老师的名字

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getRecordName() {
        return recordName;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
