package com.telit.zhkt_three.JavaBean;

import android.widget.FrameLayout;
import android.widget.ImageView;

public class SubjectBean {
    private String id;
    private FrameLayout answer_frame_one;
    private FrameLayout answer_frame_two;
    private FrameLayout answer_frame_three;

    private ImageView subjective_img_one;
    private ImageView subjective_img_two;
    private ImageView subjective_img_three;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public FrameLayout getAnswer_frame_one() {
        return answer_frame_one;
    }

    public void setAnswer_frame_one(FrameLayout answer_frame_one) {
        this.answer_frame_one = answer_frame_one;
    }

    public FrameLayout getAnswer_frame_two() {
        return answer_frame_two;
    }

    public void setAnswer_frame_two(FrameLayout answer_frame_two) {
        this.answer_frame_two = answer_frame_two;
    }

    public FrameLayout getAnswer_frame_three() {
        return answer_frame_three;
    }

    public void setAnswer_frame_three(FrameLayout answer_frame_three) {
        this.answer_frame_three = answer_frame_three;
    }

    public ImageView getSubjective_img_one() {
        return subjective_img_one;
    }

    public void setSubjective_img_one(ImageView subjective_img_one) {
        this.subjective_img_one = subjective_img_one;
    }

    public ImageView getSubjective_img_two() {
        return subjective_img_two;
    }

    public void setSubjective_img_two(ImageView subjective_img_two) {
        this.subjective_img_two = subjective_img_two;
    }

    public ImageView getSubjective_img_three() {
        return subjective_img_three;
    }

    public void setSubjective_img_three(ImageView subjective_img_three) {
        this.subjective_img_three = subjective_img_three;
    }
}
