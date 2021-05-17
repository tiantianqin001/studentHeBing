package com.telit.zhkt_three.websocket;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.telit.zhkt_three.Activity.HomeScreen.MainActivity;
import com.telit.zhkt_three.Activity.HomeScreen.OffLineWarningActivity;
import com.telit.zhkt_three.BuildConfig;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class JWebSocketClientService extends Service {
    public JWebSocketClient client;
    private JWebSocketClientBinder mBinder = new JWebSocketClientBinder();

    private boolean closeWS;

    //用于Activity和service通讯
    public class JWebSocketClientBinder extends Binder {
        public JWebSocketClientService getService() {
            return JWebSocketClientService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //初始化websocket
        initSocketClient("初始化连接");

        mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测

        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        closeConnect();

        stopForeground(true);

        super.onDestroy();
    }

    public JWebSocketClientService() {
    }

    /**
     * 初始化websocket连接
     */
    private void initSocketClient(String flag) {
        String url = UrlUtils.WSBaseUrl +"/websocket/login/"+ UserUtils.getUserId() +"/"+UserUtils.getToken();
        QZXTools.logE(flag +" url:"+url,null);

        URI uri = URI.create(url);
        client = new JWebSocketClient(uri) {
            @Override
            public void onMessage(String message) {
                QZXTools.logE("收到的消息1：" + message,null);

                if (client!=null&&client.isOpen()){
                    client.close();
                    closeWS = true;
                }

                if (!TextUtils.isEmpty(message)){
                    JSONObject jsonObject= JSONObject.parseObject(message);
                    if (!UserUtils.getToken().equals(jsonObject.getString("token"))){
                        //如果是被迫下线
                        Intent intentOffLine = new Intent(JWebSocketClientService.this, OffLineWarningActivity.class);
                        //必须加上这条属性
                        intentOffLine.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        JWebSocketClientService.this.startActivity(intentOffLine);
                    }
                }
            }

            @Override
            public void onOpen(ServerHandshake handshakedata) {
                super.onOpen(handshakedata);
                QZXTools.logE("websocket连接成功",null);
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                super.onClose(code, reason, remote);
                QZXTools.logE("websocket连接关闭",null);
            }
        };
        connect();
    }

    /**
     * 连接websocket
     */
    private void connect() {
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    client.connectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            if (null != client) {
                client.close();
            }
            if (null!=mHandler){
                mHandler.removeCallbacks(heartBeatRunnable);
            }
        } catch (Exception e) {
            e.printStackTrace();
            QZXTools.logE( "Exception：",e);
        } finally {
            client = null;
            mHandler = null;
        }
    }

    /**
     * 获取应用名称
     *
     * @param context
     * @return
     */
    public String getAppName(Context context) {
        if (context == null) {
            return null;
        }
        try {
            PackageManager packageManager = context.getPackageManager();
            return String.valueOf(packageManager.getApplicationLabel(context.getApplicationInfo()));
        } catch (Throwable e) {
        }
        return null;
    }


    //    -------------------------------------websocket心跳检测------------------------------------------------
    private static final long HEART_BEAT_RATE = 10 * 1000;//每隔10秒进行一次对长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            QZXTools.logE("心跳包检测websocket连接状态",null);
            if (client != null) {
                if (client.isClosed()) {
                    if (!closeWS){
                        reconnectWs();
                    }
                }
            } else {
                //如果client已为空，重新初始化连接
                client = null;
                initSocketClient("重新初始化连接");
            }
            //每隔一定的时间，对长连接进行一次心跳检测
            mHandler.postDelayed(this, HEART_BEAT_RATE);
        }
    };

    /**
     * 开启重连
     */
    private void reconnectWs() {
        mHandler.removeCallbacks(heartBeatRunnable);
        new Thread() {
            @Override
            public void run() {
                try {
                    QZXTools.logE("开启重连",null);
                    if (!closeWS){
                        client.reconnectBlocking();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 创建通知
     */
    private void createNotification(){
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = BuildConfig.APPLICATION_ID + ".server";
            String channelName = getAppName(MyApplication.getInstance());
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);
        }

        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        String title = getAppName(MyApplication.getInstance());

        builder.setSmallIcon(R.mipmap.nlogo)
                .setContentTitle(title)
                .setContentText("运行中...")
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .build();
        startForeground(2, builder.build());
    }
}
