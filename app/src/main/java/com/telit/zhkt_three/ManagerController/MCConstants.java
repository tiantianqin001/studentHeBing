package com.telit.zhkt_three.ManagerController;

/**
 * author: qzx
 * Date: 2019/11/29 9:39
 */
public class MCConstants {
    //---------------------------发送数据指令给管控端

    //触发管控App的数据同步
    public static final int MSG_SYNC_HAT_DATA = 201;

    //隐藏导航栏back键
    public static final int MSG_HIDE_BACK_BUTTON = 3001;
    //参数true隐藏false显示
    public static final String EXTRA_HIDE_BACK_BUTTON = "extra.hide.back.button";

    //隐藏导航栏home键
    public static final int MSG_HIDE_HOME_BUTTON = 3002;
    public static final String EXTRA_HIDE_HOME_BUTTON = "extra.hide.home.button";

    //关机
    public static final int MSG_SHUTDOWN_DEVICE = 3004;

    //重启
    public static final int MSG_REBOOT_DEVICE = 3005;

    //亮屏
    public static final int MSG_WAKEUP_DEVICE = 3006;

    //熄屏
    public static final int MSG_SLEEP_DEVICE = 3007;

    //结束应用进程
    public static final int MSG_KILL_PKG = 3010;
    //参数为需要结束进程的应用包名
    public static final String EXTRA_KILL_PKG_LIST = "extra.kill.pkg.list";

    //请求管控桌面显示的应用包名集合
    public static final int MSG_REQUEST_DESKTOP_APP_LIST = 3017;

    //---------------------------发送数据指令给管控端

    //------------------------------------------------------------------------------------------

    //---------------------------管控端数据反馈

    //客户端发送的命令，在当前管控App中未定义
    public static final int MSG_UNDEFINED_COMMAND = 101;
    //客户端发送的命令，在当前管控App中未定义，返回未定义的命令
    public static final String EXTRA_UNDEFINED_COMMAND = "extra.undefined.command";

    //客户端发送的命令，在当前管控App中不支持
    public static final int MSG_UNSUPPORTED_COMMAND = 102;
    //客户端发送的命令，在当前管控App中不支持，返回不支持的命令
    public static final String EXTRA_UNSUPPORTED_COMMAND = "extra.unsupported.command";

    //返回管控桌面显示的包名集合
    public static final int MSG_REQUEST_DESKTOP_APP_LIST_RESULT = 4002;
    //返回的包名集合类型为ArrayList<String>
    public static final String EXTRA_REQUEST_DESKTOP_APP_LIST_RESULT = "extra.request.desktop.app.list.result";

    //---------------------------管控端数据反馈

}
