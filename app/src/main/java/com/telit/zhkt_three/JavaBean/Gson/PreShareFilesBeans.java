package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.PreView.SysFileShare;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/20 13:45
 */
public class PreShareFilesBeans {
    private boolean success;
    private String errorCode;
    private String msg;
    private List<SysFileShare> result;
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

    public List<SysFileShare> getResult() {
        return result;
    }

    public void setResult(List<SysFileShare> result) {
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
}
