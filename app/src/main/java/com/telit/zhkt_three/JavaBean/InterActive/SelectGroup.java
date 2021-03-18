package com.telit.zhkt_three.JavaBean.InterActive;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/31 9:19
 */
public class SelectGroup {
    private Integer id;
    private String classId;
    private String teacherId;
    private String createDate;
    private String creator;
    private List<SelectGroupDetail> discussionGroup;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public List<SelectGroupDetail> getDiscussionGroup() {
        return discussionGroup;
    }

    public void setDiscussionGroup(List<SelectGroupDetail> discussionGroup) {
        this.discussionGroup = discussionGroup;
    }

    public static class SelectGroupDetail {
        private int discussId;
        private int groupDiscussId;
        private String groupIndex;
        private String groupName;
        private String theme;
        private String createDate;
        private String creator;

        public int getDiscussId() {
            return discussId;
        }

        public void setDiscussId(int discussId) {
            this.discussId = discussId;
        }

        public String getGroupIndex() {
            return groupIndex;
        }

        public void setGroupIndex(String groupIndex) {
            this.groupIndex = groupIndex;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getCreator() {
            return creator;
        }

        public void setCreator(String creator) {
            this.creator = creator;
        }

        public int getGroupDiscussId() {
            return groupDiscussId;
        }

        public void setGroupDiscussId(int groupDiscussId) {
            this.groupDiscussId = groupDiscussId;
        }
    }
}
