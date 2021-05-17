package com.telit.zhkt_three.JavaBean.PreView;

/**
 * author: qzx
 * Date: 2019/12/20 15:34
 * <p>
 * 因为第一次的SysFileShare以及第二次的Disk,而RecyclerView的item数据基本相同，所以在这个类整合
 */
public class PreViewDisplayBean {
    private int type;//类型(0 文件 1文件夹)
    private String fileName;
    private long fileSize;
    private String thumbnail;
    private String previewUrl;//预览地址
    private float avgStar;
    private String fileFormat;
    private String createDate;//创建时间
    private String source;//分享来源  平台/网盘
    private String fileId;
    private Integer id;

    private String savePath;
    private String status;
    private String collectionState;
    private String collectionTime;
    private String collectionId;

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    private int commentId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //下面三个参数用于资源评论
    private String shareId;
    private String resId;
    private String shareTitle;

    //已评论的内容
    private String commentContent;
    private String resStars;
    private int curPosition;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
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

    public float getAvgStar() {
        return avgStar;
    }

    public void setAvgStar(float avgStar) {
        this.avgStar = avgStar;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getShareId() {
        return shareId;
    }

    public void setShareId(String shareId) {
        this.shareId = shareId;
    }

    public String getResId() {
        return resId;
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public String getShareTitle() {
        return shareTitle;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getResStars() {
        return resStars;
    }

    public void setResStars(String resStars) {
        this.resStars = resStars;
    }

    public int getCurPosition() {
        return curPosition;
    }

    public void setCurPosition(int curPosition) {
        this.curPosition = curPosition;
    }

    public String getCollectionState() {
        return collectionState;
    }

    public void setCollectionState(String collectionState) {
        this.collectionState = collectionState;
    }

    public String getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(String collectionTime) {
        this.collectionTime = collectionTime;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
