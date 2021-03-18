package com.telit.zhkt_three.JavaBean.InterActive;

/**
 * 项目名称：desktop
 * 类名称：DiscussListBean
 * 类描述：聊天列表的Item
 * 创建人：luxun
 * 创建时间：2017/3/17  18:58
 * 修改人：luxun
 * 修改时间：2017/3/17 18:58
 * 当前版本：v1.0
 */

public class DiscussListBean {

    /**
     * studentid : 1
     * studentName : 张三
     * photo :
     * groupName:
     * isLeader: 1表示为组长 0为普通成员
     */

    private String studentid;
    private String studentName;
    private String photo;
    private String groupName;
    private int isLeader;

    //新增班级和发言时间
    private String speakTime;
    private String className;
    private String discussName;

    public int getIsLeader() {
        return isLeader;
    }

    public void setIsLeader(int isLeader) {
        this.isLeader = isLeader;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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

    public String getDiscussName() {
        return discussName;
    }

    public void setDiscussName(String discussName) {
        this.discussName = discussName;
    }

    @Override
    public String toString() {
        return "DiscussListBean{" +
                "studentid='" + studentid + '\'' +
                ", studentName='" + studentName + '\'' +
                ", photo='" + photo + '\'' +
                ", groupName='" + groupName + '\'' +
                ", isLeader=" + isLeader +
                ", speakTime='" + speakTime + '\'' +
                ", className='" + className + '\'' +
                ", discussName='" + discussName + '\'' +
                '}';
    }
}
