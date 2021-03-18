package com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave;

/**
 * author: qzx
 * Date: 2019/5/27 18:31
 */
public class AnswerItem {
    public String itemId;
    public String content;
    public String blanknum;

    public String getBlanknum() {
        return blanknum;
    }

    public void setBlanknum(String blanknum) {
        this.blanknum = blanknum;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "AnswerItem{" +
                "itemId='" + itemId + '\'' +
                ", content='" + content + '\'' +
                ", blanknum='" + blanknum + '\'' +
                '}';
    }
}
