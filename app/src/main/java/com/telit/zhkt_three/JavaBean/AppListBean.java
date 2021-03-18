package com.telit.zhkt_three.JavaBean;

import java.io.Serializable;
import java.util.List;

public class AppListBean  implements Serializable {
    private List<AppInfo> datas;

    public List<AppInfo> getDatas() {
        return datas;
    }

    public void setDatas(List<AppInfo> datas) {
        this.datas = datas;
    }
}
