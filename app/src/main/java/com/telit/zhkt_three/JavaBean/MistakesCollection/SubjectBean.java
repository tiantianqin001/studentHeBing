package com.telit.zhkt_three.JavaBean.MistakesCollection;

/**
 * 项目名称：desktop
 * 类名称：SubjectBean
 * 类描述：科目Bean
 * 创建人：luxun
 * 创建时间：2017/5/8 0008 10:21
 * 修改人：luxun
 * 修改时间：2017/5/8 0008 10:21
 * 当前版本：v1.0
 */

public class SubjectBean {

    /**
     * id : 17
     * name : 语文
     */

    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SubjectBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
