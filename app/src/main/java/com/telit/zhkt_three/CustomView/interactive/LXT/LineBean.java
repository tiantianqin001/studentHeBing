package com.telit.zhkt_three.CustomView.interactive.LXT;

import android.graphics.Rect;

/**
 * author: qzx
 * Date: 2019/7/16 9:01
 * <p>
 * 连线题左侧和右侧一致，唯一的id以及连线内容组成
 * <p>
 * 新增rangeRect记录在界面的范围
 * 新增hasMatching是否配对了
 */
public class LineBean {
    private int id;
    private String content;

    private Rect rangeRect;

    private boolean hasMatching;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Rect getRangeRect() {
        return rangeRect;
    }

    public void setRangeRect(Rect rangeRect) {
        this.rangeRect = rangeRect;
    }

    public boolean isHasMatching() {
        return hasMatching;
    }

    public void setHasMatching(boolean hasMatching) {
        this.hasMatching = hasMatching;
    }

    @Override
    public String toString() {
        return "LineBean{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", rangeRect=" + rangeRect +
                ", hasMatching=" + hasMatching +
                '}';
    }
}
