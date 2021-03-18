package com.telit.zhkt_three.Activity.InteractiveScreen;

/**
 * author: qzx
 * Date: 2019/11/13 13:50
 */
public class JsRecordScreenBean {
    private int command;
    private String json;

    public int getCommand() {
        return command;
    }

    public void setCommand(int command) {
        this.command = command;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public JsRecordScreenBean(int command, String json) {
        this.command = command;
        this.json = json;
    }

    @Override
    public String toString() {
        return "JsRecordScreenBean{" +
                "command=" + command +
                ", json='" + json + '\'' +
                '}';
    }
}
