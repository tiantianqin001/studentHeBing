package com.telit.zhkt_three.ManagerController;

import android.app.Application;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/11/29 10:55
 * <p>
 * 与航智云管控连接的客户端
 */
public class ManagerControllerClient {
    private static volatile ManagerControllerClient managerControllerClient = null;

    private ManagerControllerClient() {

    }

    public static ManagerControllerClient getInstance() {
        if (managerControllerClient == null) {
            synchronized (ManagerControllerClient.class) {
                if (managerControllerClient == null) {
                    managerControllerClient = new ManagerControllerClient();
                }
            }
        }
        return managerControllerClient;
    }

    /**
     * 信息接收处理的Handler
     */
    private class MCMsgHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            QZXTools.logE("receive msg =" + msg, null);
            mcCallback.onReceiveMsg(msg.what, msg.getData());
        }
    }

    private MCConnect mcConnect = null;
    private MCCallback mcCallback = null;
    //消息发送者
    private Messenger msgSender = null;
    //消息接收者
    private Messenger msgReceiver = null;
    //消息接收处理者
    private MCMsgHandler msgHandler = null;

    /**
     * 与管控端建立连接
     */
    public void bind(Application application) {
        mcConnect = new MCConnect();
        msgHandler = new MCMsgHandler();
        msgReceiver = new Messenger(msgHandler);

        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.example.edcationcloud",
                "com.example.edcationcloud.ccs.remote.RemoteConnectServerService");
        intent.setComponent(componentName);
        application.bindService(intent, mcConnect, Service.BIND_AUTO_CREATE);
    }

    /**
     * 向云管控端发送消息
     */
    public void sendMCMessage(int command, Bundle data) {
        Message message = Message.obtain();
        message.what = command;
        if (data != null) {
            message.setData(data);
        }
        //把客户端接收服务端的信使发给服务端
        message.replyTo = msgReceiver;

        try {
            msgSender.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
            QZXTools.logE("send mc msg exception ", e);
        }


    }

    /**
     * 销毁连接
     */
    public void destroy(Application application) {
        if (msgHandler != null) {
            msgHandler.removeCallbacksAndMessages(null);
        }

        msgHandler = null;
        msgReceiver = null;
        msgSender = null;

        //解绑
        if (mcConnect != null) {
            application.unbindService(mcConnect);
            mcConnect = null;
        }
    }

    /**
     * 注册与云管控反馈的信息回调
     */
    public void registerCallback(MCCallback callback) {
        mcCallback = callback;
    }


    private class MCConnect implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            QZXTools.logE("与云管控建立连接了...", null);
            msgSender = new Messenger(service);
            mcCallback.connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            QZXTools.logE("与云管控连接断开...", null);
            mcConnect = null;
            mcCallback.disconnect();
        }
    }


}
