package com.telit.zhkt_three.Service;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;

/**
 * how to use:
 * one step ：（in the manifests file）
 * <service
 * android:name=".Service.SimpleSocketLinkServerTwo"
 * android:permission="android.permission.BIND_JOB_SERVICE" />
 * <p>
 * two step ：
 * //开启连接服务
 * Intent serverIntent = new Intent(this, SimpleSocketLinkServerTwo.class);
 * serverIntent.setAction(Constant.SOCKET_CONNECT_ACTION);
 * serverIntent.setPackage(getPackageName());
 * //如果安卓o,api26（8.0）
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 * SimpleSocketLinkServerTwo.enqueueWork(this, serverIntent);
 * } else {
 * startService(serverIntent);
 * }
 */
public class SimpleSocketLinkServerTwo extends JobIntentService {

    private static final int JOB_ID = 70001;

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        QZXTools.logE("SimpleSocketLinkServerTwo onHandleWork =》Thread=" + Thread.currentThread().getName(), null);
        //这个是子线程中执行的，可以操作耗时处理
        if (intent != null && intent.getAction().equals(Constant.SOCKET_CONNECT_ACTION)) {
          //  SimpleClientNetty.getInstance().init(UrlUtils.SocketIp, port).connectAsync();
        }
    }

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, SimpleSocketLinkServerTwo.class, JOB_ID, work);
    }
}
