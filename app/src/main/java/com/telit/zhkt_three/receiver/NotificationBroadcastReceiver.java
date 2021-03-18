package com.telit.zhkt_three.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.telit.zhkt_three.Utils.QZXTools;

import java.io.File;

/**
 * author: qzx
 * Date: 2019/7/9 20:58
 *
 * 通知栏点击跳转接收器
 */
public class NotificationBroadcastReceiver extends BroadcastReceiver {
    public static final String TYPE = "type";//这个type是为了Notification更新信息的
    public static final String ACTION_CLICK = "com.zbv.notification.CLICK";
    public static final String ACTION_CANCEL = "com.zbv.notification.CANCEL";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int type = intent.getIntExtra(TYPE, -1);
        String filePath = intent.getStringExtra("shot_path");

        QZXTools.logE("--------receiver type=" + type + ";action=" + action + ";filePath=" + filePath, null);

        if (type != -1) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(type);
        }

        if (action.equals(ACTION_CLICK)) {
            QZXTools.logE("click notification", null);
            //处理点击事件
            File file = new File(filePath);
            QZXTools.openFile(file, context);
        }

        if (action.equals(ACTION_CANCEL)) {
            QZXTools.logE("cancel notification", null);
            //处理滑动清除和点击删除事件 

        }
    }
}
