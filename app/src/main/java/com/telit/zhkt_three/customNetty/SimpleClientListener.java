package com.telit.zhkt_three.customNetty;

/**
 * author: qzx
 * Date: 2019/4/28 10:13
 */
public interface SimpleClientListener {
    void onLine();

    void offLine();

    void receiveData(String msgInfo);
    void isNoUser();
}
