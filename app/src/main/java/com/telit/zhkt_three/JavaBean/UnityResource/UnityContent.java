package com.telit.zhkt_three.JavaBean.UnityResource;

public class UnityContent {
    private Long id;

    private String contentName;

    private Integer contentType;

    private Long contentSee;

    private String contentImgUrl;


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    private String alias;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContentName() {
        return contentName;
    }

    public void setContentName(String contentName) {
        this.contentName = contentName;
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public Long getContentSee() {
        return contentSee;
    }

    public void setContentSee(Long contentSee) {
        this.contentSee = contentSee;
    }

    public String getContentImgUrl() {
        return contentImgUrl;
    }

    public void setContentImgUrl(String contentImgUrl) {
        this.contentImgUrl = contentImgUrl;
    }

    @Override
    public String toString() {
        return "UnityContent{" +
                "id=" + id +
                ", contentName='" + contentName + '\'' +
                ", contentType=" + contentType +
                ", contentSee=" + contentSee +
                ", contentImgUrl='" + contentImgUrl + '\'' +
                '}';
    }
}