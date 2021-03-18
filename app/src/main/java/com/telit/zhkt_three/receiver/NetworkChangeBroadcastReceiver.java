package com.telit.zhkt_three.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/5/31 13:57
 * <p>
 * 参见csdn：https://blog.csdn.net/qq_35213388/article/details/82664697
 */
public class NetworkChangeBroadcastReceiver extends BroadcastReceiver {
    /**
     * 安卓7.0以上对ConnectivityManager.CONNECTIVITY_ACTION无法静态注册
     * 所以在需要的地方动态注册
     * //注册网络状态监听
     * IntentFilter filter = new IntentFilter();
     * //监听wifi连接（手机与路由器之间的连接）
     * filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
     * //监听互联网连通性（也就是是否已经可以上网了），当然只是指wifi网络的范畴
     * filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
     * //这个是监听网络状态的，包括了wifi和移动网络。
     * filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
     * mNetConnectChangedReceiver = new NetConnectChangedReceiver();
     * registerReceiver(mNetConnectChangedReceiver, filter);
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        // 监听wifi的打开与关闭的5种状态
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLING:
                    QZXTools.logE("WiFi正要开启的状态, 是 Enabled 和 Disabled 的临界状态", null);
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    QZXTools.logE("WiFi已经完全开启的状态", null);
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    QZXTools.logE("WiFi正要关闭的状态, 是 Disabled 和 Enabled 的临界状态", null);
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    QZXTools.logE("WiFi已经完全关闭的状态", null);
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    QZXTools.logE("WiFi未知的状态, WiFi开启, 关闭过程中出现异常, 或是厂家未配备WiFi外挂模块会出现的情况", null);
                    break;
            }
        }

        // 监听wifi的连接状态,即是否连接上了一个有效wifi
        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                // 获取联网状态的NetWorkInfo对象
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
                //获取的State对象则代表着连接成功与否等状态
                NetworkInfo.State state = networkInfo.getState();
                //判断网络是否已经连接
                boolean isConnected = state == NetworkInfo.State.CONNECTED;
                if (isConnected) {
                    QZXTools.logE("连接上了wifi", null);
                } else {

                }
            }
        }

        // 监听网络连接，包括wifi和移动数据的打开和关闭,以及连接上可用的连接都会接到监听
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
//            NetworkInfo info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            ConnectivityManager cManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = cManager.getActiveNetworkInfo();
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        QZXTools.logE("连接上了wifi网络", null);
                        if (mListener != null) {
                            mListener.onWifi(true);
                        }
                    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        QZXTools.logE("连接上了手机网络", null);
                        if (mListener != null) {
                            mListener.onMobile(true);
                        }
                    }

                } else {
                    if (info.getType() == ConnectivityManager.TYPE_WIFI) {
                        QZXTools.logE("断开了wifi网络", null);
                        if (mListener != null) {
                            mListener.onWifi(false);
                        }
                    } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
                        QZXTools.logE("断开了手机网络", null);
                        if (mListener != null) {
                            mListener.onMobile(false);
                        }
                    }
                }
            }
        }
    }

    /**
     * 网络状态改变监听器
     */
    private OnNetConnectChangedListener mListener;

    /**
     * 设置网络状态改变监听
     *
     * @param listener
     */
    public void setOnNetConnectChangedListener(OnNetConnectChangedListener listener) {
        mListener = listener;
    }

    /**
     * 网络状态改变监听器
     */
    public interface OnNetConnectChangedListener {

        /**
         * 是否连接上了wifi网络
         *
         * @param isConnected
         */
        void onWifi(boolean isConnected);

        /**
         * 是否连接上了手机网络
         *
         * @param isConnected
         */
        void onMobile(boolean isConnected);
    }
}






