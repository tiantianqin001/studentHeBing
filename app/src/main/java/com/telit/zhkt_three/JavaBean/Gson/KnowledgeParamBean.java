package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionParam;

/**
 * author: qzx
 * Date: 2019/5/16 11:06
 */
public class KnowledgeParamBean {
    private boolean success;
    private String errorCode;
    private String msg;
    private QuestionParam result;
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

    public QuestionParam getResult() {
        return result;
    }

    public void setResult(QuestionParam result) {
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

    @Override
    public String toString() {
        return "KnowledgeParamBean{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                ", total=" + total +
                ", pageNo=" + pageNo +
                '}';
    }
}
