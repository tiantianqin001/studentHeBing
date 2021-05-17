package com.telit.zhkt_three.JavaBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MulitBean {
    @Id
    private String id;

   String checkViews;

@Generated(hash = 62130825)
public MulitBean(String id, String checkViews) {
    this.id = id;
    this.checkViews = checkViews;
}

@Generated(hash = 1660067102)
public MulitBean() {
}

public String getId() {
    return this.id;
}

public void setId(String id) {
    this.id = id;
}

public String getCheckViews() {
    return this.checkViews;
}

public void setCheckViews(String checkViews) {
    this.checkViews = checkViews;
}
}
