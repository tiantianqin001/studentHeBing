package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.UnityResource.Subject;
import com.telit.zhkt_three.JavaBean.UnityResource.XD;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/10/15 11:31
 */
public class UnityConditionChapterBean {
    private String msg;
    private int code;
    private List<String> result;

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

    public List<String> getResult() {
        return result;
    }

    public void setResult(List<String> result) {
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
}
