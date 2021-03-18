package com.telit.zhkt_three.JavaBean.Resource;

/**
 * 项目名称：desktop
 * 类名称：ResourceCondition
 * 类描述：资源条件类别
 * 创建人：luxun
 * 创建时间：2017/3/23 19:27
 * 修改人：luxun
 * 修改时间：2017/3/23 19:27
 * 当前版本：v1.0
 */

public class ResourceCondition {
    private String id;
    private String name;
    private String code;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "ResourceCondition{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
