package com.telit.zhkt_three.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.text.TextUtils;
import android.util.Log;

import com.github.druk.dnssd.BrowseListener;
import com.github.druk.dnssd.DNSSD;
import com.github.druk.dnssd.DNSSDBindable;
import com.github.druk.dnssd.DNSSDException;
import com.github.druk.dnssd.DNSSDService;
import com.telit.zhkt_three.Activity.InteractiveScreen.InteractiveActivity;
import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.manager.AppManager;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.util.Properties;

/**
 * author: qzx
 * Date: 2019/4/28 10:21
 * <p>
 * how to use:
 * one step ：（in manifest file）
 * <service
 * android:name=".Service.SimpleSocketLinkServer"/>
 * <p>
 * two step ：
 * //开启连接服务
 * Intent serverIntent = new Intent(this, SimpleSocketLinkServerTwo.class);
 * serverIntent.setAction(Constant.SOCKET_CONNECT_ACTION);
 * serverIntent.setPackage(getPackageName());
 * //如果安卓o,api26（8.0）
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 * startForegroundService(serverIntent);
 * <p>
 * } else {
 * startService(serverIntent);
 * }
 */
public class SimpleSocketLinkServer extends Service {

    private static final String CHANNEL_ID_STRING = "netty007";

    private DNSSD dnssd;
    private DNSSDService browseService;

    @Override
    public void onCreate() {
        super.onCreate();
        //适配8.0service
        NotificationManager notificationManager = (NotificationManager) getApplicationContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID_STRING, "netty_server", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_STRING).build();
            startForeground(1, notification);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("WrongConstant")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
        QZXTools.logE("onStartCommand", null);
        if (intent != null && intent.getAction().equals(Constant.SOCKET_CONNECT_ACTION)) {

            dnssd = new DNSSDBindable(this);
            try {
                browseService =  dnssd.browse(Constant.SSDP_XIE_YI, new BrowseListener() {

                    @Override
                    public void serviceFound(DNSSDService browser, int flags, int ifIndex,
                                             final String serviceName, String regType, String domain) {

                    }
                    @Override
                    public void serviceLost(DNSSDService browser, int flags, int ifIndex,
                                            String serviceName, String regType, String domain) {
                      String serviceName1=  SharedPreferenceUtil.getInstance(MyApplication.getInstance())
                                .getString("serviceName");
                        if (TextUtils.isEmpty(serviceName1) || TextUtils.isEmpty(serviceName)){
                            return;
                        }
                        if (serviceName1.equals(serviceName)){
                            AppManager.getAppManager().AppExit();
                            Intent intent1 = new Intent(SimpleSocketLinkServer.this, SelectClassActivity.class);
                            startActivity(intent1);

                            stopSelf();
                            if (browseService!=null){
                                browseService.stop();
                            }
                        }

                    }

                    @Override
                    public void operationFailed(DNSSDService service, int errorCode) {


                    }
                });
            } catch (DNSSDException e) {

            }

        }
        return super.onStartCommand(intent, flags, startId);
    }
}
