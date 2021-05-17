package com.telit.zhkt_three.JavaBean.MicroClass;

/**
 * author: qzx
 * Date: 2019/12/25 16:47
 */
public class ActualMicBean {
    private String resourceName;
    private float score;
    private String createDate;
    private String thumbnail;
    private String previewUrl;

    public String getFileName() {
        return fileName;
    }

    private String fileName;

    //微课文件大小
    private long size;

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
