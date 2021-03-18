package com.telit.zhkt_three.JavaBean.Communication;

/**
 * 项目名称：desktop
 * 类名称：FileRecieve
 * 类描述：接收教师端文件
 * 创建人：luxun
 * 创建时间：2017/4/24 0024 15:34
 * 修改人：luxun
 * 修改时间：2017/4/24 0024 15:34
 * 当前版本：v1.0
 */
public class FileReceive {
    /**
     * "fileName":"index.jpg",
     * "fileSize":2714167,
     * "downloadPath":"http://60.166.13.165:8088/filesystem/distribution/941bb05b1e8d4000bf30ae46cabbde2d.jpg"
     */
    private String fileName;
    private String fileSize;
    private String downloadPath;

    public FileReceive(String fileName, String fileSize, String downloadPath) {
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.downloadPath = downloadPath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadPath() {
        return downloadPath;
    }

    public void setDownloadPath(String downloadPath) {
        this.downloadPath = downloadPath;
    }

    @Override
    public String toString() {
        return "FileRecieve{" +
                "fileName='" + fileName + '\'' +
                ", fileSize='" + fileSize + '\'' +
                ", downloadPath='" + downloadPath + '\'' +
                '}';
    }

}
