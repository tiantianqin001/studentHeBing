package com.telit.zhkt_three.ManagerController;

import android.os.Bundle;

/**
 * author: qzx
 * Date: 2019/11/29 11:19
 */
public interface MCCallback {
    void connect();

    void disconnect();

    void onReceiveMsg(int command, Bundle data);
}
