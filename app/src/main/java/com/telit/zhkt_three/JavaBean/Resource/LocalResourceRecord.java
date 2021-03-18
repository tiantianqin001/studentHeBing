package com.telit.zhkt_three.JavaBean.Resource;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * author: qzx
 * Date: 2019/6/12 14:23
 * <p>
 * 自主学习资源保存的本地数据库存储记录
 */
@Entity
public class LocalResourceRecord {
    /**
     * 资源类型：电子课本1010 视频3 音频2 图片1（可能是zip）
     */
    private String resourceType;
    @Id
    private String resourceId;
    /**
     * 资源的文件路径
     */
    private String resourceFilePath;


    private String resourceUpdateDate;

    private String imageUrl;

    private String resourceName;

    private boolean canChecked;

    /**
     * 是否选中要被删除
     */
    private boolean isChoosed;

    @Generated(hash = 1075555356)
    public LocalResourceRecord(String resourceType, String resourceId,
            String resourceFilePath, String resourceUpdateDate, String imageUrl,
            String resourceName, boolean canChecked, boolean isChoosed) {
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.resourceFilePath = resourceFilePath;
        this.resourceUpdateDate = resourceUpdateDate;
        this.imageUrl = imageUrl;
        this.resourceName = resourceName;
        this.canChecked = canChecked;
        this.isChoosed = isChoosed;
    }

    @Generated(hash = 827146568)
    public LocalResourceRecord() {
    }

    public String getResourceType() {
        return this.resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return this.resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceFilePath() {
        return this.resourceFilePath;
    }

    public void setResourceFilePath(String resourceFilePath) {
        this.resourceFilePath = resourceFilePath;
    }

    public String getResourceUpdateDate() {
        return this.resourceUpdateDate;
    }

    public void setResourceUpdateDate(String resourceUpdateDate) {
        this.resourceUpdateDate = resourceUpdateDate;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public boolean getCanChecked() {
        return this.canChecked;
    }

    public void setCanChecked(boolean canChecked) {
        this.canChecked = canChecked;
    }

    public boolean getIsChoosed() {
        return this.isChoosed;
    }

    public void setIsChoosed(boolean isChoosed) {
        this.isChoosed = isChoosed;
    }

    @Override
    public String toString() {
        return "LocalResourceRecord{" +
                "resourceType='" + resourceType + '\'' +
                ", resourceId='" + resourceId + '\'' +
                ", resourceFilePath='" + resourceFilePath + '\'' +
                ", resourceUpdateDate='" + resourceUpdateDate + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", resourceName='" + resourceName + '\'' +
                ", canChecked=" + canChecked +
                ", isChoosed=" + isChoosed +
                '}';
    }
}
