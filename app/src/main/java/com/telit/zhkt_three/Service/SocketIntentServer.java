package com.telit.zhkt_three.Service;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.CustomView.RippleBackground;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class SocketIntentServer extends Service {

    private static final String TAG = "SocketIntentServer";

    private static final String Multicast_IP = "239.5.6.7";
    private static final int Multicast_Port = 37656;
    private MulticastSocket multicastSocket;
    private WifiManager.MulticastLock multicastLock;

    private DatagramPacket packet;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String message = (String) msg.obj;

                if (listener!=null){
                    listener.sendMessage(message);
                }

            }
        }
    };


    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "服务器已绑定");
        return new MyBind();
    }
    public SocketIntentServer() {

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //创建组播
        try {
            //添加组播锁
            multicastSocket = createMulticastGroupAndJoin(Multicast_IP, Multicast_Port);
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifiManager.createMulticastLock(SelectClassActivity.class.getSimpleName());
            multicastLock.acquire();
            multicastSocket.setNetworkInterface(NetworkInterface.getByName("wlan0"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    protected void onHandleIntent() {
        //执行后台的操作
        while (true) {
            try {

                String message = recieveData(multicastSocket, Multicast_IP);//接收组播组传来的消息
                Thread.sleep(1000);
                if (!TextUtils.isEmpty(message)) {
                    Message obtain = Message.obtain();
                    obtain.what = 0;
                    obtain.obj = message;
                    handler.sendMessage(obtain);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    // 创建一个组播组并加入此组的函数
    public MulticastSocket createMulticastGroupAndJoin(String groupurl, int port) {
        try {
            InetAddress group = InetAddress.getByName(groupurl); // 设置组播组的地址为239.0.0.0
            MulticastSocket socket = new MulticastSocket(port); // 初始化MulticastSocket类并将端口号与之关联

            //  socket.setTimeToLive(1); // 设置组播数据报的发送范围为本地网络
            socket.setSoTimeout(10000); // 设置套接字的接收数据报的最长时间
            socket.joinGroup(group); // 加入此组播组
            //socket.setLoopbackMode(false);
            return socket;
        } catch (Exception e1) {
            System.out.println("Error: " + e1); // 捕捉异常情况
            return null;
        }
    }

    public synchronized String recieveData(MulticastSocket socket, String groupurl) {
        String message = null;
        try {
            InetAddress group = InetAddress.getByName(groupurl);
            byte[] data = new byte[1024];
            packet = new DatagramPacket(data, data.length, group, Multicast_Port);
            socket.receive(packet); // 通过MulticastSocket实例端口从组播组接收数据
            // 将接受的数据转换成字符串形式
            message = new String(packet.getData(), 0, packet.getLength());
           // Log.i(TAG, "recieveData: " + message);
        } catch (Exception e1) {
            e1.fillInStackTrace();
            return "";
        }
        return message;
    }
    private onCommitMessage listener;
    public interface onCommitMessage{
        void sendMessage(String message);
    }
    public void setonCommitMessage(onCommitMessage listener){

        this.listener = listener;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    onHandleIntent();
                }
            }
        }).start();

    }

    public class MyBind extends Binder{
        public SocketIntentServer getMyService(){
            return  new SocketIntentServer();
        }
    }
}
