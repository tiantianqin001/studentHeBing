package com.telit.zhkt_three.JavaBean.MistakesCollection;

import java.util.List;

/**
 * author: qzx
 * Date: 2020/4/8 15:20
 * <p>
 * 优秀答案右侧属性类
 */
public class PerfectRightBean {
    private String photoUrl;
    private List<String> imgLists;
    private String txtAnswer;

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public List<String> getImgLists() {
        return imgLists;
    }

    public void setImgLists(List<String> imgLists) {
        this.imgLists = imgLists;
    }

    public String getTxtAnswer() {
        return txtAnswer;
    }

    public void setTxtAnswer(String txtAnswer) {
        this.txtAnswer = txtAnswer;
    }

    @Override
    public String toString() {
        return "PerfectRightBean{" +
                "photoUrl='" + photoUrl + '\'' +
                ", imgLists=" + imgLists +
                ", txtAnswer='" + txtAnswer + '\'' +
                '}';
    }
}
