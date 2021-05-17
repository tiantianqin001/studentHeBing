package com.telit.zhkt_three.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;

import com.telit.zhkt_three.Activity.InteractiveScreen.ScreenShotOperator;
import com.telit.zhkt_three.BuildConfig;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 使用mediaprojectionmanager截屏
 * <p>
 * notes：使用MediaProjection截屏会弹出请求授权框，然后才能截屏等操作
 * <p>
 * 步骤分解如下：
 * 第一步：初始化一次：
 * if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
 * projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
 * }
 * 第二步：使用ZBVPermission授权判定
 * 第三步：如果高于5.0使用MediaProjectionManager，否则使用View;使用MediaProjectionManager需要用户授权如下：
 * startActivityForResult(projectionManager.createScreenCaptureIntent(), Media_Projection_Shot_RequestCode);
 * 授权得到的resultCode和data可以通过MediaProjectionManager获取到MediaProjection对象
 * 第四步：如果用户授权了就可以执行Service截屏了
 * <p>
 * 具体截屏操作步骤：
 * 第一步：设定截屏的保存文件路径shotFilePath即保存在哪个文件夹下
 * 第二步：创建虚拟显示createVirtualDisplay例如：宽高，分辨率等还有重要的surface
 * 第三步：截屏使用ImageReader,获取到Image然后转化成Bitmap,然后写入文件并且更新图库即可看到，此外可以通知用户一下
 * <p>
 * 借助ScreenShotOperator
 */
public class ScreenShotService extends Service {

    private MediaProjection mediaProjection;
    private ScreenShotOperator screenShotOperator;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {

            MediaProjectionManager mediaProjectionManager = MyApplication.getInstance().getMPM();
            //传入两个参数：code以及data[intent]

            //todo intent nullpoint

            int code = intent.getIntExtra("result_code", -777);
            Intent data = intent.getParcelableExtra("data_intent");

            if (code == -777 || data == null) {
                return super.onStartCommand(intent, flags, startId);
            }

            //创建通知
            createNotification();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection = mediaProjectionManager.getMediaProjection(code, data);
            }

            if (mediaProjection != null) {
                screenShotOperator = new ScreenShotOperator(this, mediaProjection);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                String dateStr = simpleDateFormat.format(new Date());
                String fileName = "shotImg_" + dateStr + ".png";

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        screenShotOperator.startScreenShot(fileName);
                        Looper.loop();
                    }
                }).start();


            }

        }catch (Exception e){
            e.fillInStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (screenShotOperator != null) {
            screenShotOperator.stopScreenShot();
        }

        super.onDestroy();
    }

    /**
     * 创建通知
     */
    private void createNotification(){
        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = BuildConfig.APPLICATION_ID + ".server";
            String channelName = "课堂截屏";
            NotificationChannel chan = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(chan);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        String title = getAppName(MyApplication.getInstance());

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText("课堂截屏中...")
                .setContentIntent(null)
                .setOngoing(true)
                .build();
        startForeground(2, builder.build());
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
}
