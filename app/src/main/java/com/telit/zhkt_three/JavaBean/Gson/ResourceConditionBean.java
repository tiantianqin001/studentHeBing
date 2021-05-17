package com.telit.zhkt_three.JavaBean.Gson;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/9 11:14
 */
public class ResourceConditionBean {


    /**
     * success : true
     * errorCode : 1
     * msg : 操作成功
     * result : {"period":[{"id":"0","name":"小学","code":null},{"id":"1","name":"初中","code":null},{"id":"2","name":"高中","code":null}],"subject":[{"id":"0","name":"语文","code":null},{"id":"1","name":"数学","code":null},{"id":"10","name":"思想品德","code":null},{"id":"11","name":"历史","code":null},{"id":"12","name":"地理","code":null},{"id":"13","name":"物理","code":null},{"id":"14","name":"化学","code":null},{"id":"15","name":"生物","code":null},{"id":"16","name":"体育","code":null},{"id":"17","name":"思维训练","code":null},{"id":"18","name":"阅读","code":null},{"id":"19","name":"健康","code":null},{"id":"2","name":"英语","code":null},{"id":"20","name":"口语","code":null},{"id":"21","name":"班会","code":null},{"id":"22","name":"自习","code":null},{"id":"28","name":"活动","code":null},{"id":"29","name":"政治","code":null},{"id":"3","name":"品德与生活","code":null},{"id":"30","name":"书法","code":null},{"id":"31","name":"道德与法治","code":null},{"id":"32","name":"数活","code":null},{"id":"33","name":"手工","code":null},{"id":"34","name":"诵读","code":null},{"id":"35","name":"周会","code":null},{"id":"36","name":"体活","code":null},{"id":"37","name":"语活","code":null},{"id":"38","name":"班会/国防","code":null},{"id":"39","name":"阅读/写作","code":null},{"id":"4","name":"品德与社会","code":null},{"id":"40","name":"英活","code":null},{"id":"41","name":"国防","code":null},{"id":"42","name":"2+1","code":null},{"id":"46","name":"计算机","code":null},{"id":"5","name":"音乐","code":null},{"id":"6","name":"美术","code":null},{"id":"7","name":"信息技术","code":null},{"id":"8","name":"科学","code":null},{"id":"9","name":"综合实践","code":null}],"grade":[{"id":null,"name":"一年级","code":"1"},{"id":null,"name":"二年级","code":"2"},{"id":null,"name":"三年级","code":"3"},{"id":null,"name":"四年级","code":"4"},{"id":null,"name":"五年级","code":"5"},{"id":null,"name":"六年级","code":"6"},{"id":null,"name":"七年级","code":"7"},{"id":null,"name":"八年级","code":"8"},{"id":null,"name":"九年级","code":"9"},{"id":null,"name":"高一","code":"10"},{"id":null,"name":"高二","code":"11"},{"id":null,"name":"高三","code":"12"}],"term":[{"id":"0","name":"上学期","code":null},{"id":"1","name":"下学期","code":null},{"id":"9","name":"全册","code":null}],"press":[{"id":"1","name":"苏教版","code":null},{"id":"0","name":"部编版","code":null},{"id":"88","name":"人民音乐出版社","code":null},{"id":"90","name":"安徽大学出版社","code":null},{"id":"87","name":"人民美术出版社","code":null},{"id":"2","name":"外研版","code":null},{"id":"91","name":"合肥工业大学出版社","code":null},{"id":"92","name":"电子工业出版社","code":null},{"id":"93","name":"教育科学出版社","code":null},{"id":"98","name":"西冷印社出版社","code":null},{"id":"3","name":"人民教育出版社","code":null},{"id":"99","name":"其它","code":null}],"bookType":[{"id":"1010","name":"电子教材","code":null},{"id":"0","name":"文档","code":null},{"id":"1","name":"图片","code":null},{"id":"2","name":"音频","code":null},{"id":"3","name":"视频","code":null},{"id":"4","name":"压缩包","code":null}]}
     * total : 0
     * pageNo : 0
     */

    private boolean success;
    private String errorCode;
    private String msg;
    private ResultBean result;
    private int total;
    private int pageNo;

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

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
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

    public static class ResultBean {
        private List<PeriodBean> period;
        private List<SubjectBean> subject;
        private List<GradeBean> grade;
        private List<TermBean> term;
        private List<PressBean> press;
        private List<BookTypeBean> bookType;
        private List<PressBean> read_press;
        private List<GradeBean> read_grade;

        public List<PeriodBean> getPeriod() {
            return period;
        }

        public void setPeriod(List<PeriodBean> period) {
            this.period = period;
        }

        public List<SubjectBean> getSubject() {
            return subject;
        }

        public void setSubject(List<SubjectBean> subject) {
            this.subject = subject;
        }

        public List<GradeBean> getGrade() {
            return grade;
        }

        public void setGrade(List<GradeBean> grade) {
            this.grade = grade;
        }

        public List<TermBean> getTerm() {
            return term;
        }

        public void setTerm(List<TermBean> term) {
            this.term = term;
        }

        public List<PressBean> getPress() {
            return press;
        }

        public void setPress(List<PressBean> press) {
            this.press = press;
        }

        public List<BookTypeBean> getBookType() {
            return bookType;
        }

        public void setBookType(List<BookTypeBean> bookType) {
            this.bookType = bookType;
        }

        public List<PressBean> getRead_press() {
            return read_press;
        }

        public void setRead_press(List<PressBean> read_press) {
            this.read_press = read_press;
        }

        public List<GradeBean> getRead_grade() {
            return read_grade;
        }

        public void setRead_grade(List<GradeBean> read_grade) {
            this.read_grade = read_grade;
        }

        public static class PeriodBean {
            /**
             * id : 0
             * name : 小学
             * code : null
             */

            private String id;
            private String name;
            private Object code;

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

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }
        }

        public static class SubjectBean {
            /**
             * id : 0
             * name : 语文
             * code : null
             */

            private String id;
            private String name;
            private Object code;

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

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }
        }

        public static class GradeBean {
            /**
             * id : null
             * name : 一年级
             * code : 1
             */

            private Object id;
            private String name;
            private String code;

            public Object getId() {
                return id;
            }

            public void setId(Object id) {
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
        }

        public static class TermBean {
            /**
             * id : 0
             * name : 上学期
             * code : null
             */

            private String id;
            private String name;
            private Object code;

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

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }
        }

        public static class PressBean {
            /**
             * id : 1
             * name : 苏教版
             * code : null
             */

            private String id;
            private String name;
            private Object code;

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

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }
        }

        public static class BookTypeBean {
            /**
             * id : 1010
             * name : 电子教材
             * code : null
             */

            private String id;
            private String name;
            private Object code;

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

            public Object getCode() {
                return code;
            }

            public void setCode(Object code) {
                this.code = code;
            }
        }
    }
}
