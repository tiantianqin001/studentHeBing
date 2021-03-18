package com.zbv.basemodel;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LingChuangUtils {
    private LingChuangUtils() {
    }

    private static LingChuangUtils instance = new LingChuangUtils();

    public static LingChuangUtils getInstance() {
        synchronized (LingChuangUtils.class) {
            return instance;
        }
    }

    //重启
    public  void Reboot(Context context) {
        //重启的广播
        Intent intent1 = new Intent("com.linspirer.edu.rebootdevice");
        intent1.setPackage("com.android.launcher3");
        context.sendBroadcast(intent1);
    }
    //禁用back
    public void stopBack(Context context){
        // 禁用 Back 键
        Intent intent1 = new Intent("com.linspirer.edu.disableback");
        intent1.setPackage("com.android.launcher3");
        context.sendBroadcast(intent1);
    }
    //启用back
    public void startBack(Context context){
        Intent intent1 = new Intent("com.linspirer.edu.enableback");
        intent1.setPackage("com.android.launcher3");
        context.sendBroadcast(intent1);
    }
    // 隐藏导航栏
    public void stopDisablenavigationbar(Context context){
        Intent intent1 = new Intent("com.linspirer.edu.disablenavigationbar");
        intent1.setPackage("com.android.launcher3");
        context.sendBroadcast(intent1);
    }
    //显示导航栏
    public void startDisablenavigationbar(Context context){
        Intent intent1 = new Intent("com.linspirer.edu.enablenavigationbar");
        intent1.setPackage("com.android.launcher3");
        context.sendBroadcast(intent1);
    }
    //关机的接口
    public void closeDevice(Context context){
        Intent intent1 = new Intent("com.linspirer.edu.shutdown");
        intent1.setPackage("com.android.launcher3");
        context.sendBroadcast(intent1);
    }
    //静默安装
    public void silentInstall(Context context){
        Intent intent1 = new Intent("com.linspirer.edu.silentinstall");
        intent1.setPackage("com.android.launcher3");
        //intent1.putExtra(String name, String value)
        context.sendBroadcast(intent1);
    }

    // 禁用 recent recent
    public void stopRecent(Context context){
        Intent intent = new Intent("com.linspirer.edu.disablerecent");
        intent.setPackage("com.android.launcher3");
        context. sendBroadcast(intent);
    }
    //启用 recent
    public void startRecent(Context context){
        Intent intent = new Intent("com.linspirer.edu.enablerecent");
        intent.setPackage("com.android.launcher3");
        context. sendBroadcast(intent);
    }
    //禁用home
    public void stopHome(Context context){
        Intent intent = new Intent("com.linspirer.edu.disablehome");
        intent.setPackage("com.android.launcher3");
        context. sendBroadcast(intent);
    }
    //开启home
    public void startHome(Context context){
        Intent intent = new Intent("com.linspirer.edu.enablehome");
        intent.setPackage("com.android.launcher3");
        context. sendBroadcast(intent);
    }


}
