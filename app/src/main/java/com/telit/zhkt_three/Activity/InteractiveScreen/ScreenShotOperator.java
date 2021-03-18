package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;

import com.telit.zhkt_three.Fragment.Dialog.ScreenShotImgDialog;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.receiver.NotificationBroadcastReceiver;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * author: qzx
 * Date: 2019/11/13 10:01
 * <p>
 * Android5.0+
 * <p>
 * 截屏操作器
 */
public class ScreenShotOperator {
    private Context mContext;

    private MediaProjection mediaProjection;

    private VirtualDisplay virtualDisplay;

    private ImageReader imageReader;

    private String shotFilePath;

    private boolean isScreenShot = false;

    public boolean isScreenShot() {
        return isScreenShot;
    }

    public ScreenShotOperator(Context context, MediaProjection mediaProjection) {
        mContext = context;
        this.mediaProjection = mediaProjection;

        setSavedFilePath();
    }

    /**
     * 设置截屏的文件保存路径
     * path：/storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/zhkt_shot/xxxx
     */
    private void setSavedFilePath() {
        shotFilePath = QZXTools.getExternalStorageForFiles(mContext, null) + "/zhkt_shot";
        File dirFile = new File(shotFilePath);
        if (!dirFile.exists()) {
            boolean dirSuccess = dirFile.mkdir();
            if (!dirSuccess) {
                QZXTools.popToast(mContext, "截屏路径出错，截屏失败", false);
                return;
            }
        }
    }

    /**
     * 创建VirtualDisplay以及ImageReader
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplayAndImageReader() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dpi = dm.densityDpi;

        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 1);

        virtualDisplay = mediaProjection.createVirtualDisplay("screen_shot_name", width, height, dpi,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC,
                imageReader.getSurface(),
                null, null);
    }

    /**
     * 开启截屏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startScreenShot(String fileName) {

        if (isScreenShot) {
            return;
        }

        QZXTools.logE("service screen shot to Start..." + new Date().getTime(), null);

        createVirtualDisplayAndImageReader();

        isScreenShot = true;

        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
            @Override
            public void onImageAvailable(ImageReader reader) {

                if (isScreenShot) {
                    Bitmap bitmap = null;
                    Image image = imageReader.acquireLatestImage();
                    if (image != null) {
                        bitmap = ImageUtils.image_2_bitmap(image, Bitmap.Config.ARGB_8888);
                    }

                    FileOutputStream fos = null;
                    try {
                        File destFile = new File(shotFilePath, fileName);
                        fos = new FileOutputStream(destFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        //结束截屏
                        stopScreenShot();

                        //保存到系统相册中
                        QZXTools.savePictureToSystemDCIM(mContext, destFile, "zhkt_3.0");

                        QZXTools.popToast(mContext, "截屏成功", false);

                        //发送通知
                        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        NotificationChannel channel = null;
                        if (Build.VERSION.SDK_INT >= 26) {
                            channel = new NotificationChannel("zhkt", "screen_shot", NotificationManager.IMPORTANCE_DEFAULT);
                            notificationManager.createNotificationChannel(channel);
                        }

                        NotificationCompat.Builder builder;
                        if (channel != null && Build.VERSION.SDK_INT >= 26) {
                            builder = new NotificationCompat.Builder(mContext, channel.getId());
                        } else {
                            builder = new NotificationCompat.Builder(mContext, null);
                        }

                        Intent intentClick = new Intent(mContext, NotificationBroadcastReceiver.class);
                        intentClick.setAction(NotificationBroadcastReceiver.ACTION_CLICK);
                        intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 7);
                        intentClick.putExtra("shot_path", destFile.getAbsolutePath());
                        PendingIntent pendingIntentClick = PendingIntent.getBroadcast(mContext, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
                        Intent intentCancel = new Intent(mContext, NotificationBroadcastReceiver.class);
                        intentCancel.setAction(NotificationBroadcastReceiver.ACTION_CANCEL);
                        intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 7);
                        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(mContext, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);

                        //小图标必加，否则闪退
                        builder.setSmallIcon(R.mipmap.ic_launcher);
                        builder.setContentTitle("截屏");
                        builder.setContentText("课堂互动的截屏信息");
                        builder.setWhen(System.currentTimeMillis());
                        builder.setVibrate(new long[]{1000});//震动1秒
                        builder.setDefaults(Notification.DEFAULT_LIGHTS);
                        builder.setContentIntent(pendingIntentClick);
                        builder.setDeleteIntent(pendingIntentCancel);

                        Notification notification = builder.build();
                        notification.flags = Notification.FLAG_ONGOING_EVENT;//发起正在运行事件
                        notificationManager.notify(7, notification);

                        QZXTools.logE("service screen shot to End..." + new Date().getTime(), null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        QZXTools.logE("保存的文件未发现异常", e);
                        CrashReport.postCatchedException(e);
                    } finally {
                        if (fos != null) {
                            try {
                                fos.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        if (bitmap != null) {
                            bitmap.recycle();
                        }

                        if (image != null) {
                            image.close();
                        }
                    }
                }
            }
        }, null);


        mediaProjection.registerCallback(new MediaProjection.Callback() {
            @Override
            public void onStop() {
                if (virtualDisplay != null) {
                    virtualDisplay.release();
                }
                if (imageReader != null) {
                    //close包含[setOnImageAvailableListener(null, null);]
                    imageReader.close();
                }

                if (mediaProjection != null) {
                    mediaProjection.unregisterCallback(this);
                }
            }
        }, null);
    }

    /**
     * 结束截屏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScreenShot() {
        isScreenShot = false;
        if (mediaProjection != null) {
            mediaProjection.stop();
        }
    }

    //在后台线程里保存文件--------暂未使用
    private Handler backgroundHandler;

    private Handler getBackgroundHandler() {
        if (backgroundHandler == null) {
            HandlerThread backgroundThread =
                    new HandlerThread("catwindow", android.os.Process
                            .THREAD_PRIORITY_BACKGROUND);
            backgroundThread.start();
            backgroundHandler = new Handler(backgroundThread.getLooper());
        }
        return backgroundHandler;
    }
}
