package com.telit.zhkt_three.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Utils.LogUtil;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

public class SocketReceiver extends BroadcastReceiver {
    private static final String TAG = "SocketReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle=intent.getExtras();
        String message = bundle.getString("message");
        QZXTools.logD(message);

        EventBus.getDefault().post(Constant.SEND_MULTICAST, message);
    }
}
