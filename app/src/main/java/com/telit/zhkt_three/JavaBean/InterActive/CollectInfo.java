package com.telit.zhkt_three.JavaBean.InterActive;

/**
 * author: qzx
 * Date: 2019/7/4 17:30
 */
public class CollectInfo {
    //主键
    private Integer id;
    //收藏id
    private String collectId;
    //收藏类别
    private String collectType;
    //收藏名称
    private String collectName;
    //收藏者
    private String userId;
    //收藏时间
    private String createDate;
    //删除标志(0 未删除 1 已删除)
    private int delFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public String getCollectType() {
        return collectType;
    }

    public void setCollectType(String collectType) {
        this.collectType = collectType;
    }

    public String getCollectName() {
        return collectName;
    }

    public void setCollectName(String collectName) {
        this.collectName = collectName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(int delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "CollectInfo{" +
                "id=" + id +
                ", collectId='" + collectId + '\'' +
                ", collectType='" + collectType + '\'' +
                ", collectName='" + collectName + '\'' +
                ", userId='" + userId + '\'' +
                ", createDate='" + createDate + '\'' +
                ", delFlag=" + delFlag +
                '}';
    }
}
