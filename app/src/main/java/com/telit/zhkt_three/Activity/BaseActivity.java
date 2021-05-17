package com.telit.zhkt_three.Activity;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.manager.AppManager;

/**
 * author: qzx
 * Date: 2019/10/10 15:55
 * <p>
 * 这个目前唯一的作用是管理Activity
 */
public class BaseActivity extends AppCompatActivity {
    protected static String[] PERMISSIONS_STORAGE = {
            "android.permission.RECORD_AUDIO",
            "android.permission.CAMERA",
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.ACCESS_FINE_LOCATION",
            "android.permission.ACCESS_COARSE_LOCATION"
    };
    private boolean isfist=true;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        AppManager.getAppManager().addActivity(this);
        //去除标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置沉静状态栏
        ImmersionBar.with(this)
                .transparentBar()
                .init();
        //获取权限
        requestPerm();

        //开始显示一个闪缩的imageview  让推流一直推
        if (isfist){

            isfist=false;
        }

    }
    private final static int PERMISSIONS_OK = 10001;
    public void requestPerm() {
        if (Build.VERSION.SDK_INT>22) {
            if (!checkPermissionAllGranted(PERMISSIONS_STORAGE)) {
                //先判断有没有权限 ，没有就在这里进行权限的申请
                ActivityCompat.requestPermissions(this,
                        PERMISSIONS_STORAGE, PERMISSIONS_OK);
            }else{

            }
        }else{

        }
    }

    @SuppressLint("LongLogTag")
    private boolean checkPermissionAllGranted(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                QZXTools.logE("permission=" +permission, null);

            }
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppManager.getAppManager().removeActivity(this);
    }
}
