package com.telit.zhkt_three.JavaBean.InterActive;

import android.support.annotation.NonNull;

import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/12/25 10:39
 * <p>
 * 分组讨论新版
 */
public class DiscussListBeanTwo implements Comparable<DiscussListBeanTwo> {

    private int discussGroupId;
    private String userName;
    private String userId;
    private String photo;
    private String theme;
    private String groupName;
    private String fileUrl;
    private String groupLeader;

    //讨论结论相关---获取组信息没有这两个字段，课堂记录的详情才有
    private String enclosure;
    private String groupConclusion;
    private String groupIndex;

    //新增班级和发言时间
    private String speakTime;
    private String className;

    public int getDiscussGroupId() {
        return discussGroupId;
    }

    public void setDiscussGroupId(int discussGroupId) {
        this.discussGroupId = discussGroupId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getGroupLeader() {
        return groupLeader;
    }

    public void setGroupLeader(String groupLeader) {
        this.groupLeader = groupLeader;
    }

    public String getSpeakTime() {
        return speakTime;
    }

    public void setSpeakTime(String speakTime) {
        this.speakTime = speakTime;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getEnclosure() {
        return enclosure;
    }

    public void setEnclosure(String enclosure) {
        this.enclosure = enclosure;
    }

    public String getGroupConclusion() {
        return groupConclusion;
    }

    public void setGroupConclusion(String groupConclusion) {
        this.groupConclusion = groupConclusion;
    }

    public String getGroupIndex() {
        return groupIndex;
    }

    public void setGroupIndex(String groupIndex) {
        this.groupIndex = groupIndex;
    }

    /**
     * 按照字母排序
     */
    @Override
    public int compareTo(@NonNull DiscussListBeanTwo o) {
        char first = QZXTools.getPinYinHeadChar(this.getUserName()).toCharArray()[0];
        char second = QZXTools.getPinYinHeadChar(o.getUserName()).toCharArray()[0];
        QZXTools.logE("one =" + first + ";second=" + second, null);
        if (first > second) {
            return 1;
        } else if (first < second) {
            return -1;
        } else {
            return 0;
        }
    }
}
