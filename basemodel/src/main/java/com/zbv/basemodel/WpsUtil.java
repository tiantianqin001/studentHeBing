package com.zbv.basemodel;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.List;

public class WpsUtil {
    public WpsInterface wpsInterface;
    private WpsCloseListener wpsCloseListener = null;
    private Boolean canWrite;
    private Activity mActivity;
    private String fileUrl;

    public WpsUtil(WpsInterface wpsInterface,  String fileUrl, Boolean canWrite, Activity activity) {
        this.wpsInterface = wpsInterface;
        this.canWrite = canWrite;
        this.mActivity = activity;
        this.fileUrl = fileUrl;
    }

    public void openDocument() {
        openDocument(null);
    }

    public void openDocument(File file) {
        try {
            wpsCloseListener = new WpsCloseListener();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.kingsoft.writer.back.key.down");//按下返回键
            filter.addAction("com.kingsoft.writer.home.key.down");//按下home键
            filter.addAction("cn.wps.moffice.file.save");//保存
            filter.addAction("cn.wps.moffice.file.close");//关闭
            mActivity.registerReceiver(wpsCloseListener,filter);//注册广播
            if (file!=null) {
                openDocWithSimple(file);
            } else {
                openDocFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 打开本地文件
    public void openDocWithSimple(File file) {
        try {
            Intent intent = mActivity.getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");

            Bundle bundle = new Bundle();
            //打开模式
            bundle.putString(Define.OPEN_MODE, Define.NORMAL);
            bundle.putBoolean(Define.ENTER_REVISE_MODE, true);//以修订模式打开
//            bundle.putString(Define.OPEN_MODE, Define.READ_ONLY);
            bundle.putBoolean(Define.SEND_SAVE_BROAD, true);
            bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);
            bundle.putBoolean(Define.HOME_KEY_DOWN, true);
            bundle.putBoolean(Define.BACK_KEY_DOWN, true);
            bundle.putBoolean(Define.ENTER_REVISE_MODE, true);
            bundle.putBoolean(Define.IS_SHOW_VIEW, false);
            bundle.putBoolean(Define.AUTO_JUMP, true);
            //设置广播
            bundle.putString(Define.THIRD_PACKAGE, mActivity.getPackageName());
            intent.setAction(Intent.ACTION_VIEW);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(mActivity,
                        "com.telit.smartclass.desktop.fileprovider", file);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(contentUri, "*/*");
            } else {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.fromFile(file), "*/*");
            }
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 打开文档
    boolean openDocFile()
    {
        try {
            Intent intent = mActivity.getPackageManager().getLaunchIntentForPackage("cn.wps.moffice_eng");
            Bundle bundle = new Bundle();
            if (canWrite) {
                bundle.putString(Define.OPEN_MODE, Define.NORMAL);
                bundle.putBoolean(Define.ENTER_REVISE_MODE, true);//以修订模式打开
            } else {
                bundle.putString(Define.OPEN_MODE, Define.READ_ONLY);
            }
            //打开模式
            bundle.putBoolean(Define.SEND_SAVE_BROAD, true);
            bundle.putBoolean(Define.SEND_CLOSE_BROAD, true);
            bundle.putBoolean(Define.HOME_KEY_DOWN, true);
            bundle.putBoolean(Define.BACK_KEY_DOWN, true);
            bundle.putBoolean(Define.ENTER_REVISE_MODE, true);
            bundle.putBoolean(Define.IS_SHOW_VIEW, false);
            bundle.putBoolean(Define.AUTO_JUMP, true);
            //设置广播
            bundle.putString(Define.THIRD_PACKAGE, mActivity.getPackageName());
            //第三方应用的包名，用于对改应用合法性的验证
//            bundle.putBoolean(Define.CLEAR_FILE, true);
            //关闭后删除打开文件
            intent.setAction(Intent.ACTION_VIEW);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse(fileUrl));
            intent.putExtras(bundle);
            mActivity.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public interface WpsInterface {
        void doRequest(String filePath);
        void doFinish();
    }

    // 广播接收器
    private class WpsCloseListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("cn.wps.moffice.file.save")) {
                    String fileSavePath = intent.getExtras().getString(Define.SAVE_PATH);
                    if(canWrite) {
                        wpsInterface.doRequest(fileSavePath);
                    }
                } else if (intent.getAction().equals("cn.wps.moffice.file.close")||
                        intent.getAction().equals("com.kingsoft.writer.back.key.down")) {
                    wpsInterface.doFinish();
                    mActivity.unregisterReceiver(wpsCloseListener);//注销广播
                }
            }catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public void appBack() {
        //获取ActivityManager
        ActivityManager mAm = (ActivityManager) mActivity.getSystemService(Context.ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList = mAm.getRunningTasks(100);
        //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
        for (ActivityManager.RunningTaskInfo rti : taskList) {
            if (rti.topActivity.getPackageName().equals(mActivity.getPackageName())) {
            Intent LaunchIntent = new Intent(Intent.ACTION_MAIN);
            ComponentName cn = new ComponentName(mActivity.getPackageName(), rti.topActivity.getClassName());
            LaunchIntent.setComponent(cn);
            mActivity.startActivity(LaunchIntent);
            break;
            }
        }

    }


}
