package com.telit.zhkt_three.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.telit.zhkt_three.Utils.QZXTools;

import cn.jpush.android.api.JPushInterface;

/**
 * author: qzx
 * Date: 2019/5/21 22:13
 *
 * 这个是极光推送2.0版本的，属于老版本，这里没有使用，但是在清单文件注册过，不过没有影响，
 * 因为如果使用极光推送3.0，自动不使用2.0
 */
public class jpushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            Bundle bundle = intent.getExtras();
            switch (intent.getAction()) {
                case JPushInterface.ACTION_REGISTRATION_ID:

                    QZXTools.logE("JPush 用户注册成功", null);
                    break;
                case JPushInterface.ACTION_MESSAGE_RECEIVED:
                    QZXTools.logE("接受到推送下来的自定义消息", null);
                    break;
                case JPushInterface.ACTION_NOTIFICATION_RECEIVED:
                    QZXTools.logE("接受到推送下来的通知", null);
                    String id = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                    String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                    String message = bundle.getString(JPushInterface.EXTRA_ALERT);
                    String extra = bundle.getString(JPushInterface.EXTRA_EXTRA);

                    QZXTools.logE("title=$title;message=$message;extra=$extra", null);
                    break;
                case JPushInterface.ACTION_NOTIFICATION_OPENED:
                    QZXTools.logE("用户点击打开了通知", null);
                    break;
                case JPushInterface.ACTION_CONNECTION_CHANGE:
                    boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                    QZXTools.logE(" 网络连接状态改变 " + connected, null);
                    break;
                default:
                    QZXTools.logE("Unhandled intent - " + intent.getAction(), null);
                    break;
            }
        }
    }

}
