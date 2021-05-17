package com.telit.zhkt_three.JavaBean.InterActive;

/**
 * author: qzx
 * Date: 2019/7/1 18:05
 */
public class ServerIpInfo {
    private String className;
    private String deviceIp;
    private String devicePort;
    private String classId;

    private String address;
    private String Port;

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

    private String teacherId;

    public ServerIpInfo() {
    }


    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }

    public String getDevicePort() {
        return devicePort;
    }

    public void setDevicePort(String devicePort) {
        this.devicePort = devicePort;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return Port;
    }

    public void setPort(String port) {
        this.Port = port;
    }

    @Override
    public String toString() {
        return "ServerIpInfo{" +
                ", className='" + className + '\'' +
                ", deviceIp='" + deviceIp + '\'' +
                ", devicePort='" + devicePort + '\'' +

                '}';
    }
}
