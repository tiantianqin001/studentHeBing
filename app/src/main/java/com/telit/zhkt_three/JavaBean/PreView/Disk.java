package com.telit.zhkt_three.JavaBean.PreView;

/**
 * author: qzx
 * Date: 2019/12/19 16:50
 */
public class Disk {

    /**
     * {
     * "id": 90,
     * "parentId": 0,
     * "hasChild": null,
     * "userId": 66666740531,
     * "parentUnitCode": null,
     * "belongUnitCode": null,
     * "type": 0,
     * "name": "music",
     * "fileId": 47,
     * "previewUrl":  "http://172.16.5.160:8090/filesystem/disk/20191211/1576057052440.mp4",
     * "delFlag": 0,
     * "createDate": "2019-12-11 17:37:32",
     * "updateDate": "2019-12-11 17:39:36",
     * "fileName": "music",
     * "fileSize": 17302977,
     * "fileFormat": "mp4",
     * "fileType": 1,
     * "searchFormat": null,
     * "ids": null,
     * "keyword": null,
     * "thumbnail": 	"http://172.16.5.160:8090/filesystem/disk/20191211/1576057052440.png",
     * "savePath": "D:\\wisdomclass\\uploadFiles\\disk\\20191211\\1576057052440.mp4",
     * "downloadFlag": 0, //0-未下载 1 已下载
     * "diskId": null
     * }
     */

    private Integer id;
    private Integer parentId;
    private int hasChild;
    private String parentUnitCode;
    private String belongUnitCode;
    private int type;//类型(0 文件 1文件夹)
    private String name;
    private int fileId;
    private String previewUrl;
    private String fileName;
    private long fileSize;
    private String fileFormat;
    private int fileType;//文件类型  0-非微课 1-微课
    private String thumbnail;
    private String savePath;
    private int downloadFlag;//下载标识(0-未下载 1-已下载)
    private String diskId;
    private float avgStar;
    private String createDate;//创建时间
    private String updateDate;
    private String ids;

    private String collectionState;
    private String collectionTime;
    private String collectionId;

    //新增资源评论字段
    private String resComment;

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getHasChild() {
        return hasChild;
    }

    public void setHasChild(int hasChild) {
        this.hasChild = hasChild;
    }

    public String getParentUnitCode() {
        return parentUnitCode;
    }

    public void setParentUnitCode(String parentUnitCode) {
        this.parentUnitCode = parentUnitCode;
    }

    public String getBelongUnitCode() {
        return belongUnitCode;
    }

    public void setBelongUnitCode(String belongUnitCode) {
        this.belongUnitCode = belongUnitCode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFileId() {
        return fileId;
    }

    public void setFileId(int fileId) {
        this.fileId = fileId;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
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

    public String getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getDownloadFlag() {
        return downloadFlag;
    }

    public void setDownloadFlag(int downloadFlag) {
        this.downloadFlag = downloadFlag;
    }

    public String getDiskId() {
        return diskId;
    }

    public void setDiskId(String diskId) {
        this.diskId = diskId;
    }

    public float getAvgStar() {
        return avgStar;
    }

    public void setAvgStar(float avgStar) {
        this.avgStar = avgStar;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getResComment() {
        return resComment;
    }

    public void setResComment(String resComment) {
        this.resComment = resComment;
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

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }
}
