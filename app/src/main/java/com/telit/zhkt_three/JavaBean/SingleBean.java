package com.telit.zhkt_three.JavaBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class SingleBean {
    @Id
    private String id;
    private int position;

    @Generated(hash = 1731585085)
    public SingleBean(String id, int position) {
        this.id = id;
        this.position = position;
    }

    @Generated(hash = 1322880855)
    public SingleBean() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
