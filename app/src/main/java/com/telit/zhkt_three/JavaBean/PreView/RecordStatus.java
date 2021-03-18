package com.telit.zhkt_three.JavaBean.PreView;

import java.io.Serializable;

/**
 * author: qzx
 * Date: 2019/12/24 9:06
 */
public class RecordStatus implements Serializable {
    private String savedFilePath;
    private String previewUrl;

    public String getSavedFilePath() {
        return savedFilePath;
    }

    public void setSavedFilePath(String savedFilePath) {
        this.savedFilePath = savedFilePath;
    }

    public String getPreviewUrl() {
        return previewUrl;
    }

    public void setPreviewUrl(String previewUrl) {
        this.previewUrl = previewUrl;
    }
}
