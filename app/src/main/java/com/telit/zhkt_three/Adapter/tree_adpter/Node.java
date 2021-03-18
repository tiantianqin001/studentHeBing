package com.telit.zhkt_three.Adapter.tree_adpter;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/17 14:04
 */
public class Node {
    //选中情况
    public final static int CHOOSE_NONE = -1;
    public final static int CHOOSE_PART = 0;
    public final static int CHOOSE_ALL = 1;
    //展开情况
    public final static int CHILD_EXPAND_ALL = 1;
    public final static int CHILD_EXPAND_PART = 0;
    public final static int CHILD_EXPAND_NONE = -1;

    private String id;
    private String pid;
    private int level;
    private boolean expand;
    private int choosed = CHOOSE_NONE;
    private String showText;
    private List<Node> childs = new ArrayList<>();

    /**
     * 是否需要请求网络
     */
    private boolean needRequestNet = true;

    /**
     * 电子资源页码
     */
    private int start;

    private boolean isHideCheckBox = false;

    public Node(String id, String pid, int level, String showText) {
        this.id = id;
        this.pid = pid;
        this.level = level;
        this.showText = showText;
    }

    public boolean isHideCheckBox() {
        return isHideCheckBox;
    }

    public void setHideCheckBox(boolean hideCheckBox) {
        isHideCheckBox = hideCheckBox;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public boolean isNeedRequestNet() {
        return needRequestNet;
    }

    public void setNeedRequestNet(boolean needRequestNet) {
        this.needRequestNet = needRequestNet;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public int getChoosed() {
        return choosed;
    }

    public void setChoosed(int choosed) {
        this.choosed = choosed;
    }

    public String getShowText() {
        return showText;
    }

    public void setShowText(String showText) {
        this.showText = showText;
    }

    public List<Node> getChilds() {
        return childs;
    }

    public void setChilds(List<Node> childs) {
        this.childs = childs;
    }
}
