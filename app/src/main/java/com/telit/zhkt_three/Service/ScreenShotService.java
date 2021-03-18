package com.telit.zhkt_three.Service;

import android.app.Service;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;

import com.telit.zhkt_three.Activity.InteractiveScreen.ScreenShotOperator;
import com.telit.zhkt_three.MyApplication;

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

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mediaProjection = mediaProjectionManager.getMediaProjection(code, data);
            }

            if (mediaProjection != null) {
                ScreenShotOperator screenShotOperator = new ScreenShotOperator(this, mediaProjection);
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
}
