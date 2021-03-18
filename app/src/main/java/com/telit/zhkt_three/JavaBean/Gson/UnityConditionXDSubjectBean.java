package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.UnityResource.Subject;
import com.telit.zhkt_three.JavaBean.UnityResource.XD;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/10/15 11:31
 */
public class UnityConditionXDSubjectBean {
    private String msg;
    private int code;
    private List<xdAndsubject> result;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<xdAndsubject> getResult() {
        return result;
    }

    public void setResult(List<xdAndsubject> result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "UnityConditionXDSubjectBean{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", result=" + result +
                '}';
    }

    public static class xdAndsubject {
        private XD xd;
        private List<Subject> subject;

        public XD getXd() {
            return xd;
        }

        public void setXd(XD xd) {
            this.xd = xd;
        }

        public List<Subject> getSubject() {
            return subject;
        }

        public void setSubject(List<Subject> subject) {
            this.subject = subject;
        }

        @Override
        public String toString() {
            return "xdAndsubject{" +
                    "xd=" + xd +
                    ", subject=" + subject +
                    '}';
        }
    }
}
