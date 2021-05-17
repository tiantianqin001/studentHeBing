package com.telit.zhkt_three.JavaBean.Gson;

/**
 * author: qzx
 * Date: 2019/6/9 11:14
 */
public class TypicalAnswersBean {
    private boolean success;
    private String errorCode;
    private String msg;
    private TypicalAnswersBeanTwo result;
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

    public TypicalAnswersBeanTwo getResult() {
        return result;
    }

    public void setResult(TypicalAnswersBeanTwo result) {
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
