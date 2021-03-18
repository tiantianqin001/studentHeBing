package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.UnityResource.UnityContent;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/10/15 11:31
 */
public class UnityContentBean {
    private String msg;
    private int code;
    private List<UnityContent> result;
    private int pageNo;
    private int pageSize;

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

    public List<UnityContent> getResult() {
        return result;
    }

    public void setResult(List<UnityContent> result) {
        this.result = result;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "UnityContentBean{" +
                "msg='" + msg + '\'' +
                ", code=" + code +
                ", result=" + result +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                '}';
    }
}
