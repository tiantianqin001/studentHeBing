package com.telit.zhkt_three.JavaBean.AppUpdate;

/**
 * 项目名称：desktop
 * 类名称：${CLASS_NAME}
 * 类描述：
 * 创建人：luxun
 * 创建时间：2017/3/15 0015 16:39
 * 修改人：luxun
 * 修改时间：2017/3/15 0015 16:39
 * 当前版本：v1.0
 */

public class UpdateBean {

    /**
     * softName : 智慧课堂
     * versionCode : 2
     * description : 修复显示bug
     * versionName : 1.02
     * updateUrl : http://172.16.4.222:8080/filesystem/softupdate/1.apk
     */

    private String softName;
    private int versionCode;
    private String description;
    private String versionName;
    private String updateUrl;

    public String getSoftName() {
        return softName;
    }

    public void setSoftName(String softName) {
        this.softName = softName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    @Override
    public String toString() {
        return "UpdateBean{" +
                "softName='" + softName + '\'' +
                ", versionCode=" + versionCode +
                ", description='" + description + '\'' +
                ", versionName='" + versionName + '\'' +
                ", updateUrl='" + updateUrl + '\'' +
                '}';
    }
}
