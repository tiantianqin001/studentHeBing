package com.telit.zhkt_three.JavaBean.Gson;

import java.util.List;

public class CommentCommitBean {

    /**
     * success : true
     * errorCode : 1
     * msg : 操作成功
     * result : [{"id":null,"userId":null,"shareId":null,"shareTitle":"20210128_193301","resId":"3681","resName":"20210128_193301","resComment":"fgjklkjggjj","resStars":6,"createDate":"2021-01-30 16:48:28","studentName":"体验班级1_体验学生4","studentPhoto":"http://test.download.cycore.cn/edc/openapi/avatar_default_teacher_200_m_2.png","source":"disk"}]
     * total : 1
     * pageNo : 0
     */

    private boolean success;
    private String errorCode;
    private String msg;
    private int total;
    private int pageNo;
    private List<ResultBean> result;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public List<ResultBean> getResult() {
        return result;
    }

    public void setResult(List<ResultBean> result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * id : null
         * userId : null
         * shareId : null
         * shareTitle : 20210128_193301
         * resId : 3681
         * resName : 20210128_193301
         * resComment : fgjklkjggjj
         * resStars : 6
         * createDate : 2021-01-30 16:48:28
         * studentName : 体验班级1_体验学生4
         * studentPhoto : http://test.download.cycore.cn/edc/openapi/avatar_default_teacher_200_m_2.png
         * source : disk
         */

        private Object id;
        private Object userId;
        private Object shareId;
        private String shareTitle;
        private String resId;
        private String resName;
        private String resComment;
        private int resStars;
        private String createDate;
        private String studentName;
        private String studentPhoto;
        private String source;

        public Object getId() {
            return id;
        }

        public void setId(Object id) {
            this.id = id;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public Object getShareId() {
            return shareId;
        }

        public void setShareId(Object shareId) {
            this.shareId = shareId;
        }

        public String getShareTitle() {
            return shareTitle;
        }

        public void setShareTitle(String shareTitle) {
            this.shareTitle = shareTitle;
        }

        public String getResId() {
            return resId;
        }

        public void setResId(String resId) {
            this.resId = resId;
        }

        public String getResName() {
            return resName;
        }

        public void setResName(String resName) {
            this.resName = resName;
        }

        public String getResComment() {
            return resComment;
        }

        public void setResComment(String resComment) {
            this.resComment = resComment;
        }

        public int getResStars() {
            return resStars;
        }

        public void setResStars(int resStars) {
            this.resStars = resStars;
        }

        public String getCreateDate() {
            return createDate;
        }

        public void setCreateDate(String createDate) {
            this.createDate = createDate;
        }

        public String getStudentName() {
            return studentName;
        }

        public void setStudentName(String studentName) {
            this.studentName = studentName;
        }

        public String getStudentPhoto() {
            return studentPhoto;
        }

        public void setStudentPhoto(String studentPhoto) {
            this.studentPhoto = studentPhoto;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
