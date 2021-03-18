package com.telit.zhkt_three.JavaBean.InterActive;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * 项目名称：desktop
 * 类名称：DiscussBean
 * 类描述：聊天消息实体
 * 创建人：luxun
 * 创建时间：2017/3/17 18:39
 * 修改人：luxun
 * 修改时间：2017/3/17 18:39
 * 当前版本：v1.0
 */

@Entity
public class DiscussBean {
    //自增使用的id必须是Long类型，而非long类型 不然不会自增
    @Id(autoincrement = true)
    private Long id;
    private String discussId;
    private String studentName;
    private String studentId;//关联UserId
    private long time;
    private int type;//0文本 1图片
    private String content;//当类型为图片时，存放图片详情地址
    private String thumbnail;//图片缩略图

    //新增
    private String photo;//头像
    private String discussGroupId;

    //新增 groupIndex
    private String groupIndex;

    @Generated(hash = 891664512)
    public DiscussBean(Long id, String discussId, String studentName,
            String studentId, long time, int type, String content, String thumbnail,
            String photo, String discussGroupId, String groupIndex) {
        this.id = id;
        this.discussId = discussId;
        this.studentName = studentName;
        this.studentId = studentId;
        this.time = time;
        this.type = type;
        this.content = content;
        this.thumbnail = thumbnail;
        this.photo = photo;
        this.discussGroupId = discussGroupId;
        this.groupIndex = groupIndex;
    }

    @Generated(hash = 380370983)
    public DiscussBean() {
    }

    @Override
    public String toString() {
        return "DiscussBean{" +
                "id=" + id +
                ", discussId='" + discussId + '\'' +
                ", studentName='" + studentName + '\'' +
                ", studentId='" + studentId + '\'' +
                ", time=" + time +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", thumbnail='" + thumbnail + '\'' +
                ", photo='" + photo + '\'' +
                ", discussGroupId='" + discussGroupId + '\'' +
                '}';
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDiscussId() {
        return this.discussId;
    }

    public void setDiscussId(String discussId) {
        this.discussId = discussId;
    }

    public String getStudentName() {
        return this.studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentId() {
        return this.studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getDiscussGroupId() {
        return this.discussGroupId;
    }

    public void setDiscussGroupId(String discussGroupId) {
        this.discussGroupId = discussGroupId;
    }

    public String getGroupIndex() {
        return this.groupIndex;
    }

    public void setGroupIndex(String groupIndex) {
        this.groupIndex = groupIndex;
    }


}
