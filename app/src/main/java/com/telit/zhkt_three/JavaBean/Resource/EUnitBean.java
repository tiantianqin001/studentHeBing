package com.telit.zhkt_three.JavaBean.Resource;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/13 11:42
 *
 * 电子资源的树状菜单
 */
public class EUnitBean {
    private String chapterName;
    private int start;
    private List<EUnitBean> lson;

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public List<EUnitBean> getLson() {
        return lson;
    }

    public void setLson(List<EUnitBean> lson) {
        this.lson = lson;
    }

    @Override
    public String toString() {
        return "EUnitBean{" +
                "chapterName='" + chapterName + '\'' +
                ", start=" + start +
                ", lson=" + lson +
                '}';
    }
}
