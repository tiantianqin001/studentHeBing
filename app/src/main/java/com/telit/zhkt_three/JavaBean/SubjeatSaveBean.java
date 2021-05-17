package com.telit.zhkt_three.JavaBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SubjeatSaveBean {
    @Id
    private String id;

   private String images;

   private int layoutPosition;

@Generated(hash = 1435403048)
public SubjeatSaveBean(String id, String images, int layoutPosition) {
    this.id = id;
    this.images = images;
    this.layoutPosition = layoutPosition;
}

@Generated(hash = 767977940)
public SubjeatSaveBean() {
}

public String getId() {
    return this.id;
}

public void setId(String id) {
    this.id = id;
}

public String getImages() {
    return this.images;
}

public void setImages(String images) {
    this.images = images;
}

public int getLayoutPosition() {
    return this.layoutPosition;
}

public void setLayoutPosition(int layoutPosition) {
    this.layoutPosition = layoutPosition;
}
}
