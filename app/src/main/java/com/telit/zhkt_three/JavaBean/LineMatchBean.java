package com.telit.zhkt_three.JavaBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LineMatchBean {
    private  int position;
    private  float startX;
    private float startY;
    private float endX;
    private float endY;
    @Id(autoincrement = true)
    public Long id;
    private String leftId;
    private String rightId;
    private String TypeId;
    public String getTypeId() {
        return TypeId;
    }

    public void setTypeId(String typeId) {
        TypeId = typeId;
    }


    public String getLeftId() {
        return leftId;
    }

    public void setLeftId(String leftId) {
        this.leftId = leftId;
    }

    public String getRightId() {
        return rightId;
    }

    public void setRightId(String rightId) {
        this.rightId = rightId;
    }

    public LineMatchBean() {
    }

    @Generated(hash = 1899117372)
    public LineMatchBean(int position, float startX, float startY, float endX,
            float endY, Long id, String leftId, String rightId, String TypeId) {
        this.position = position;
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
        this.id = id;
        this.leftId = leftId;
        this.rightId = rightId;
        this.TypeId = TypeId;
    }




    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartY() {
        return startY;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getEndX() {
        return endX;
    }

    public void setEndX(float endX) {
        this.endX = endX;
    }

    public float getEndY() {
        return endY;
    }

    public void setEndY(float endY) {
        this.endY = endY;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
