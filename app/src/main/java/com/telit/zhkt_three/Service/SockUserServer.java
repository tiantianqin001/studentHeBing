package com.telit.zhkt_three.Service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.CustomView.RippleBackground;
import com.telit.zhkt_three.JavaBean.InterActive.ServerIpInfo;
import com.telit.zhkt_three.Utils.ApkListInfoUtils;
import com.telit.zhkt_three.Utils.QZXTools;

import org.json.JSONObject;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.util.Timer;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class SockUserServer extends Service {
    private static final String TAG = "SockUserServer";


    private static final String Multicast_IP = "239.5.6.7";
    private static final int Multicast_Port = 37656;
    private ExecutorService executorService;
    private MulticastSocket multicastSocket;
    private WifiManager.MulticastLock multicastLock;
    private DatagramPacket packet;



    @Override
    public void onCreate() {
        super.onCreate();
        try {
            //添加组播锁
            multicastSocket = createMulticastGroupAndJoin(Multicast_IP, Multicast_Port);
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wifiManager.createMulticastLock(SelectClassActivity.class.getSimpleName());
            multicastLock.acquire();
            multicastSocket.setNetworkInterface(NetworkInterface.getByName("wlan0"));

            //再开启一个线程  每隔6秒把收到的班级清除
            executorService = ApkListInfoUtils.getInstance().onStart();
            executorService.execute(new Runnable() {
                @Override
                public synchronized void run() {
                    while (true) {
                        getDatashows();

                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBinder();
    }

    private synchronized void getDatashows() {
        //这里是解决点击返回显示延迟的问题

        String message = recieveData(multicastSocket, Multicast_IP);//接收组播组传来的消息
        QZXTools.logD("getDatashows......"+message);




        if (dataCallback != null) {
            dataCallback.dataChanged(message);
        }

      /*  Intent intent = new Intent();
        intent.setAction("com.gdp2852.demo.service.broadcast");
        intent.putExtra("message", message);
       // intent.setPackage("com.telit.zhkt_three");  //添加解决Android8.0发送广播接收不到的问题);
        intent.setPackage(getPackageName());  //添加解决Android8.0发送广播接收不到的问题);

       // intent.setComponent(new ComponentName(getPackageName(),"com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity.MyReceiver"));
        sendBroadcast(intent);*/
    }


    public MulticastSocket createMulticastGroupAndJoin(String groupurl, int port) // 创建一个组播组并加入此组的函数
    {
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
        String message=null;
        try {
            InetAddress group = InetAddress.getByName(groupurl);
            byte[] data = new byte[1024];
            packet = new DatagramPacket(data, data.length, group, Multicast_Port);
            socket.receive(packet); // 通过MulticastSocket实例端口从组播组接收数据
            // 将接受的数据转换成字符串形式
            message = new String(packet.getData(), 0, packet.getLength());

        } catch (Exception e1) {
          //  Log.i(TAG, "recieveData: " + e1);

        }
        return message;
    }


    @Override
    public void onDestroy() {
        if (multicastLock != null) {
            multicastLock.release();
            multicastLock = null;
        }
        if (multicastSocket != null) {
            multicastSocket.close();
            multicastSocket = null;
        }
        if (executorService != null) {
            executorService.shutdownNow();
        }

        super.onDestroy();
    }


    DataCallback dataCallback = null;

    public DataCallback getDataCallback() {
        return dataCallback;
    }

    public void setDataCallback(DataCallback dataCallback) {//注意这里以单个回调为例  如果是向多个activity传送数据 可以定义一个回调集合 在此处进行集合的添加
        this.dataCallback = dataCallback;
    }

    // 通过回调机制，将Service内部的变化传递到外部
    public interface DataCallback {
        void dataChanged(String str);
    }

    public class MyBinder extends Binder{
       public SockUserServer getService() {
            return SockUserServer.this;
        }
    }
}
