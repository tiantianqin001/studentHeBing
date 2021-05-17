package com.telit.zhkt_three.JavaBean;

import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItemListConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

@Entity
public class FillBlankBean {
    @Id
    public String id;//问题ID

    @Convert(columnType = String.class, converter = AnswerItemListConverter.class)
    public List<AnswerItem> list;//答案列表---用于单选题、多选题、填空题

    @Generated(hash = 907532585)
    public FillBlankBean(String id, List<AnswerItem> list) {
        this.id = id;
        this.list = list;
    }

    @Generated(hash = 1925176699)
    public FillBlankBean() {
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<AnswerItem> getList() {
        return this.list;
    }

    public void setList(List<AnswerItem> list) {
        this.list = list;
    }
}
