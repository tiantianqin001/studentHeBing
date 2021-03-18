package com.telit.zhkt_three.JavaBean.InterActive;

import java.io.Serializable;
import java.util.List;

/**
 * 项目名称：desktop
 * 类名称：VoteResultBean
 * 类描述：投票结果
 * 创建人：luxun
 * 创建时间：2017/3/21 0021 15:28
 * 修改人：luxun
 * 修改时间：2017/3/21 0021 15:28
 * 当前版本：v1.0
 */

public class VoteResultBean{
    private String title;
    private String total;
    private String createtime;
    private String isMultiplecheck;
    private List<OptResult> list;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getIsMultiplecheck() {
        return isMultiplecheck;
    }

    public void setIsMultiplecheck(String isMultiplecheck) {
        this.isMultiplecheck = isMultiplecheck;
    }

    public List<OptResult> getList() {
        return list;
    }

    public void setList(List<OptResult> list) {
        this.list = list;
    }

    public class OptResult{
        private int ischoose;//s是否是自己选择的(1是，0不是)
        private String index;
        private String count;
        private String option;

        public int ischoose() {
            return ischoose;
        }

        public void setIschose(int ischose) {
            this.ischoose = ischose;
        }

        public String getIndex() {
            return index;
        }

        public void setIndex(String index) {
            this.index = index;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getOption() {
            return option;
        }

        public void setOption(String option) {
            this.option = option;
        }

        @Override
        public String toString() {
            return "OptResult{" +
                    "ischose=" + ischoose +
                    ", index='" + index + '\'' +
                    ", count='" + count + '\'' +
                    ", option='" + option + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VoteResultBean{" +
                ", title='" + title + '\'' +
                ", total='" + total + '\'' +
                ", createtime='" + createtime + '\'' +
                ", isMultiplecheck='" + isMultiplecheck + '\'' +
                ", list=" + list +
                '}';
    }
}
