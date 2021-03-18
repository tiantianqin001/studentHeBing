package com.telit.zhkt_three.JavaBean.InterActive;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 项目名称：desktop
 * 类名称：${CLASS_NAME}
 * 类描述：
 * 创建人：luxun
 * 创建时间：2017/3/15 0015 14:06
 * 修改人：luxun
 * 修改时间：2017/3/15 0015 14:06
 * 当前版本：v1.0
 * <p>
 * 2019/12/19 更改 by qzx
 */

public class VoteBean {


    /**
     * {
     * "id": "26",
     * "voteId": "15",
     * "name": "选项一",
     * "index": "0",
     * "text": "狮子",
     * "step": null,
     * "imageUrl": "/filesystem/vote/20191218/1576649711242.jpeg"
     * }
     */

    private String id;
    private String title;
    private int isMultiplecheck;
    private List<VoteOptionsBean> voteOptions;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsMultiplecheck() {
        return isMultiplecheck;
    }

    public void setIsMultiplecheck(int isMultiplecheck) {
        this.isMultiplecheck = isMultiplecheck;
    }

    public List<VoteOptionsBean> getVoteOptions() {
        return voteOptions;
    }

    public void setVoteOptions(List<VoteOptionsBean> voteOptions) {
        this.voteOptions = voteOptions;
    }

    /**
     * 便于Collection.sort排序
     */
    public static class VoteOptionsBean implements Comparable<VoteOptionsBean> {
        /**
         * index : 1
         * content : 张三
         * image : 1
         */

        private int index;//顺序
        private String text;//选项的内容
        private String imageUrl;//图片Url

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        @Override
        public int compareTo(@NonNull VoteOptionsBean o) {
            if (this.index > o.getIndex()) {
                return 1;
            } else if (this.index < o.getIndex()) {
                return -1;
            } else {
                return 0;
            }
        }
    }
}
