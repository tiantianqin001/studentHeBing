package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.TimeTableInfo;

/**
 * author: qzx
 * Date: 2019/6/16 13:52
 */
public class SocketIpBean {
    private boolean success;
    private String errorCode;
    private String msg;
    private SocketIp result;
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

    public SocketIp getResult() {
        return result;
    }

    public void setResult(SocketIp result) {
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
        return "TimeTableBean{" +
                "success=" + success +
                ", errorCode='" + errorCode + '\'' +
                ", msg='" + msg + '\'' +
                ", result=" + result +
                ", total=" + total +
                ", pageNo=" + pageNo +
                '}';
    }

    public static class SocketIp {
        private String serverPort;
        private String serverIp;

        public String getServerPort() {
            return serverPort;
        }

        public void setServerPort(String serverPort) {
            this.serverPort = serverPort;
        }

        public String getServerIp() {
            return serverIp;
        }

        public void setServerIp(String serverIp) {
            this.serverIp = serverIp;
        }

        @Override
        public String toString() {
            return "SocketIp{" +
                    "serverPort='" + serverPort + '\'' +
                    ", serverIp='" + serverIp + '\'' +
                    '}';
        }
    }
}
