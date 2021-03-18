package com.zbv.docxmodel;

/**
 * author: qzx
 * Date: 2020/4/1 10:21
 */
public interface DocxCallback {
    /**
     * 正在导出中
     */
    void isDocxing(boolean docx);

    /**
     * 导出完成
     */
    void onCompleted();
}
