package com.telit.zhkt_three.Service;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import com.telit.zhkt_three.Activity.InteractiveScreen.RecordScreenOperator;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 同样使用MediaProjection执行录屏操作，也需要用户授权后才能操作
 * <p>
 * todo 问题一：画质很差，有点花屏 问题二：安卓适配（Android10奔溃）
 * 问题一：Service没有适配Android8.0 ;
 * --
 * <p>
 * Intent intent = new Intent(this, ScreenRecordService.class);
 * intent.putExtra("result_code", resultCode);
 * intent.putExtra("data_intent", data);
 * intent.setPackage(getPackageName());
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
 * startForegroundService(intent);
 * } else {
 * startService(intent);
 * }
 * <p>
 * --
 */
public class ScreenRecordService extends Service {

    private MediaProjection mediaProjection;
    private RecordScreenOperator recordScreenOperator;

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MediaProjectionManager mediaProjectionManager = MyApplication.getInstance().getMPM();
        //传入两个参数：code以及data[intent]
        int code = intent.getIntExtra("result_code", -777);
        Intent data = intent.getParcelableExtra("data_intent");

        if (code == -777 || data == null) {
            return super.onStartCommand(intent, flags, startId);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaProjection = mediaProjectionManager.getMediaProjection(code, data);
        }

        if (mediaProjection != null) {
            recordScreenOperator = new RecordScreenOperator(this, mediaProjection);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    recordScreenOperator.startScreenRecord();
                    Looper.loop();
                }
            }).start();


        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        QZXTools.logE("screen record destroy...", null);

        if (recordScreenOperator != null) {
            recordScreenOperator.stopScreenRecord();
        }

        super.onDestroy();
    }
}
