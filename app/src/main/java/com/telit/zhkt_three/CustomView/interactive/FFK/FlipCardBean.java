package com.telit.zhkt_three.CustomView.interactive.FFK;

/**
 * author: qzx
 * Date: 2019/8/2 10:43
 */
public class FlipCardBean {
    //正面
    private int sign_font;//是图片是文字： 0文字、1图片
    private String text_font;//文本字符串或者图片Url
    private int resId_font;//本地图片

    //反面
    private int sign_behind;//是图片是文字： 0文字、1图片
    private String text_behind;//文本字符串或者图片Url
    private int resId_behind;//本地图片

    public int getSign_font() {
        return sign_font;
    }

    public void setSign_font(int sign_font) {
        this.sign_font = sign_font;
    }

    public String getText_font() {
        return text_font;
    }

    public void setText_font(String text_font) {
        this.text_font = text_font;
    }

    public int getResId_font() {
        return resId_font;
    }

    public void setResId_font(int resId_font) {
        this.resId_font = resId_font;
    }

    public int getSign_behind() {
        return sign_behind;
    }

    public void setSign_behind(int sign_behind) {
        this.sign_behind = sign_behind;
    }

    public String getText_behind() {
        return text_behind;
    }

    public void setText_behind(String text_behind) {
        this.text_behind = text_behind;
    }

    public int getResId_behind() {
        return resId_behind;
    }

    public void setResId_behind(int resId_behind) {
        this.resId_behind = resId_behind;
    }

    @Override
    public String toString() {
        return "FlipCardBean{" +
                "sign_font=" + sign_font +
                ", text_font='" + text_font + '\'' +
                ", resId_font=" + resId_font +
                ", sign_behind=" + sign_behind +
                ", text_behind='" + text_behind + '\'' +
                ", resId_behind=" + resId_behind +
                '}';
    }
}
