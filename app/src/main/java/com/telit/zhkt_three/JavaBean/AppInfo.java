package com.telit.zhkt_three.JavaBean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;

/**
 * author: qzx
 * Date: 2019/3/15 10:01
 * <p>
 * 图标、标题、包名、意图
 * <p>
 * 其实因为包名唯一，完全可以把包名当做主键键值
 * <p>
 * 一、添加OrederNum，之后可以依据这个排序展示
 */
@Entity
public class  AppInfo implements Serializable{
    private static final long serialVersionUID = 8829975621220483374L;
    private boolean isSystemApp;
    private String name;
    @Id
    private String packageName;
    private int orderNum;


    @Generated(hash = 641747490)
    public AppInfo(boolean isSystemApp, String name, String packageName,
                   int orderNum) {
        this.isSystemApp = isSystemApp;
        this.name = name;
        this.packageName = packageName;
        this.orderNum = orderNum;
    }


    @Generated(hash = 1656151854)
    public AppInfo() {
    }


    public boolean getIsSystemApp() {
        return this.isSystemApp;
    }


    public void setIsSystemApp(boolean isSystemApp) {
        this.isSystemApp = isSystemApp;
    }


    public String getName() {
        return this.name;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String getPackageName() {
        return this.packageName;
    }


    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getOrderNum() {
        return this.orderNum;
    }


    public void setOrderNum(int orderNum) {
        this.orderNum = orderNum;
    }

    @Override
    public String toString() {
        return "AppInfo{" +
                ", isSystemApp=" + isSystemApp +
                ", name='" + name + '\'' +
                ", packageName='" + packageName + '\'' +
                ", orderNum=" + orderNum +
                '}';
    }
}
