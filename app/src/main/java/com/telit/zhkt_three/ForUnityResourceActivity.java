package com.telit.zhkt_three;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.SSI.UnityAndroid.IMyAidlInterface;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * 与Unity端资源对接，主要展示Unity的资源
 */
@Deprecated
public class ForUnityResourceActivity extends BaseActivity {

    private IMyAidlInterface myAidlInterface;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //Log.e("zbv", "onServiceConnected");
            Toast.makeText(ForUnityResourceActivity.this, "服务连接成功", Toast.LENGTH_SHORT).show();
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            //Log.e("zbv", "onServiceDisconnected");
            Toast.makeText(ForUnityResourceActivity.this, "服务连接失败", Toast.LENGTH_SHORT).show();
            myAidlInterface = null;
        }
    };

    private void startAIDL() {
        /*
         * android5.0之后，如果servicer不在同一个App的包中，需要设置service所在程序的包名
         * */
        Intent intent = new Intent();
        intent.setAction("com.telit.aidlprovider.ProviderService");
        intent.setPackage("com.SSI.UnityAndroid");
        boolean result = bindService(intent, conn, Service.BIND_AUTO_CREATE);
        QZXTools.logE("result=" + result, null);
    }

    private void terminalAIDL() {
        unbindService(conn);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        startAIDL();

        LinearLayout one = findViewById(R.id.layout_one);
        LinearLayout two = findViewById(R.id.layout_two);
        LinearLayout three = findViewById(R.id.layout_three);

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myAidlInterface != null) {
                        myAidlInterface.fetchResource("DigestiveSystem");
                    } else {
                        Toast.makeText(ForUnityResourceActivity.this, "aidl服务未绑定", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    QZXTools.logE("exception e=" + e, null);
                    e.printStackTrace();
                }

            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myAidlInterface != null) {
                        myAidlInterface.fetchResource("Earth");
                    } else {
                        Toast.makeText(ForUnityResourceActivity.this, "aidl服务未绑定", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    QZXTools.logE("exception e=" + e, null);
                    e.printStackTrace();
                }
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (myAidlInterface != null) {
                        myAidlInterface.fetchResource("SeedPlantGrowth");
                    } else {
                        Toast.makeText(ForUnityResourceActivity.this, "aidl服务未绑定", Toast.LENGTH_SHORT).show();
                    }
                } catch (RemoteException e) {
                    QZXTools.logE("exception e=" + e, null);
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        terminalAIDL();
        super.onDestroy();
    }

    /**
     * 打开VR
     */
    public void enterAR(View view) {
        //点击事件
        Intent intent = getPackageManager().getLaunchIntentForPackage("com.TLT.ARLearning");
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
