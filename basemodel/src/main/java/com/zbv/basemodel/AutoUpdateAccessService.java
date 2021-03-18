package com.zbv.basemodel;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.List;

/**
 * author: qzx
 * Date: 2020/3/20 15:33
 * <p>
 * 辅助功能自动更新
 * <p>
 * 服务的启动只能用户在设备设置中明确启动服务来触发
 * <p>
 * 当前只使用自动静默安装功能，要确保服务端的最新包也要有AccessibilityService
 */
public class AutoUpdateAccessService extends AccessibilityService {
    public static int INVOKE_TYPE = 1;                // Event默认标志
    public static final int TYPE_INSTALL_APP = 1;    // 安装应用事件标
    public static final int TYPE_UNINSTALL_APP = 2; // 卸载应用事件标志


    public static void reset() {
        INVOKE_TYPE = 0;
    }
    private String[] packageNames = {"com.android.packageinstaller","com.telit.smartclass.desktop"};

    /**
     * 连接服务成功后回调该方法
     */
    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Toast.makeText(AutoUpdateAccessService.this, "连接服务成功",
                Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPreferences = getSharedPreferences("access_mode", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("had_access", true).commit();

        AccessibilityServiceInfo mAccessibilityServiceInfo = new AccessibilityServiceInfo();
        // 响应事件的类型，这里是全部的响应事件（长按，单击，滑动等）
        mAccessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        // 反馈给用户的类型，这里是语音提示
        mAccessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN;
        // 过滤的包名
        mAccessibilityServiceInfo.packageNames = packageNames;

        setServiceInfo(mAccessibilityServiceInfo);

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.e("zbv", "onAccessibilityEvent被调用 ==> EventType=" + event.getEventType()
                + ";EventTime=" + event.getEventTime() /*+ ";source=" + event.getSource()*/ + ";text=" + event.getText()
                + ";classname=" + event.getClassName() + ";packagename=" + event.getPackageName()
                + ";INVOKE_TYPE=" + INVOKE_TYPE);
        this.processAccessibilityEnvent(event);
    }


    /**
     * 功能 ：事件处理方法
     *
     * @param event 事件类型
     */
    private void processAccessibilityEnvent(AccessibilityEvent event) {

        if (event.getSource() != null) {
            switch (INVOKE_TYPE) {
                case TYPE_UNINSTALL_APP:
                    uninstallApplication(event); // 静默卸载
                    break;
                case TYPE_INSTALL_APP:
                    installApplication(event); // 静默安装
                default:
                    break;
            }
        }
    }


    /**
     * 功能：实现静默卸载
     *
     * @param event 卸载事件
     */
    private void uninstallApplication(AccessibilityEvent event) {
        findAndPerformActions(event, "确定");
        findAndPerformActions(event, "确认");
        findAndPerformActions(event, "卸载");
    }


    /**
     * 功能 ：实现静默安装
     *
     * @param event 事件，如ACTION_INSTALL
     */
    private void installApplication(AccessibilityEvent event) {
        //com.zbv.basemodel
        findAndPerformActions(event, "立即更新");
        //com.android.packageinstaller
        findAndPerformActions(event, "安装");
        findAndPerformActions(event, "下一步");
        findAndPerformActions(event, "打开");
    }


    /**
     * 功能：模拟用户点击操作
     *
     * @param text
     */
    private void findAndPerformActions(AccessibilityEvent event, String text) {
        if (event.getSource() != null) {

            // 判断当前界面为安装界面
            boolean isInstallPage = event.getPackageName().equals(
                    "com.android.packageinstaller");
           Log.e("zbv", "packagename=" + event.getPackageName() + ";className=" + event.getClassName()+";isInstallPage"+isInstallPage);

            if (isInstallPage || text.equals("立即更新")) {
                List<AccessibilityNodeInfo> action_nodes = event.getSource()
                        .findAccessibilityNodeInfosByText(text);
                if (action_nodes != null && !action_nodes.isEmpty()) {
                    AccessibilityNodeInfo node = null;
                    for (int i = 0; i < action_nodes.size(); i++) {
                        node = action_nodes.get(i);
                        // 执行按钮点击行为
                        if (node.getClassName().equals("android.widget.Button")
                                && node.isEnabled()) {
                            Log.e("zbv", "就是Button");
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        } else if (node.getClassName().equals("android.widget.TextView") && node.isEnabled()) {
                            Log.e("zbv", "就是TextView");
                            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
        }
    }


    @Override
    public void onInterrupt() {
        Log.e("zbv", "服务功能不可用");
    }
}
