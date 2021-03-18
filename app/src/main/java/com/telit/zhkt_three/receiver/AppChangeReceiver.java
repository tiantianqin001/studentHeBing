package com.telit.zhkt_three.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Service.AppInfoService;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/3/29 15:26
 * <p>
 * 监听App的安装和卸载以及更新
 */
public class AppChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //package:com.zbv.littleappwidget
//        String packageName = intent.getDataString();
        String packageName = intent.getData().getSchemeSpecificPart();
        QZXTools.logE("packageName=" + packageName, null);
        // 安装
        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            QZXTools.logE("android.intent.action.PACKAGE_ADDED", null);

            Intent serviceIntent = new Intent(context, AppInfoService.class);
            serviceIntent.putExtra("app_type", Constant.APP_NEW_ADD);
            serviceIntent.putExtra("package_name", packageName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppInfoService.enqueueWork(context, serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
            //之前用EventBus，但是出现该应用被杀死后无法执行后续操作的问题，所以改用Service
        }
        // 覆盖安装
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            QZXTools.logE("android.intent.action.PACKAGE_REPLACED", null);

            Intent serviceIntent = new Intent(context, AppInfoService.class);
            serviceIntent.putExtra("app_type", Constant.APP_UPDATE);
            serviceIntent.putExtra("package_name", packageName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppInfoService.enqueueWork(context, serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
        // 移除
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            QZXTools.logE("android.intent.action.PACKAGE_REMOVED", null);

            Intent serviceIntent = new Intent(context, AppInfoService.class);
            serviceIntent.putExtra("app_type", Constant.APP_DELETE);
            serviceIntent.putExtra("package_name", packageName);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppInfoService.enqueueWork(context, serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}
