package com.telit.zhkt_three.CustomView.QuestionView.matching;

/**
 * author: qzx
 * Date: 2019/7/17 19:37
 *
 * demo用的
 */
public class TestLineBean {
    private String id;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "TestLineBean{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
