package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.io.IOException;

/**
 * author: qzx
 * Date: 2019/11/13 10:54
 * <p>
 * Android5.0+
 * <p>
 * 录屏操作器
 */
public class RecordScreenOperator {
    private Context mContext;
    private MediaProjection mediaProjection;
    private MediaRecorder mediaRecorder;
    private VirtualDisplay virtualDisplay;
    private String recordFilePath;

    private boolean isScreenRecord = false;

    public boolean isScreenRecord() {
        return isScreenRecord;
    }

    public RecordScreenOperator(Context context, MediaProjection mediaProjection) {
        mContext = context;
        this.mediaProjection = mediaProjection;
    }


    /**
     * 创建VirtualDisplay以及MediaRecorder
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void createVirtualDisplayAndMediaRecorder() {
        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int dpi = dm.densityDpi;

        /**
         * 加上音频设置就出问题啦?
         * 解决了：没问题通过Service开线程后台操作
         * */
        mediaRecorder = new MediaRecorder();
        //设置视频来源
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        //设置声音的来源
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置视频格式
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //视频存储位置,考虑到文件比较大，放置在SD卡中
        recordFilePath = QZXTools.createSDCardDirectory("Movies",
                System.currentTimeMillis() + ".mp4");
        mediaRecorder.setOutputFile(recordFilePath);
        //设置视频的大小
        mediaRecorder.setVideoSize(width, height);
        mediaRecorder.setVideoFrameRate(60);
        //设置视频的编码
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置音频的编码
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
            QZXTools.logE("mediaRecorder happen exception ", e);
            QZXTools.popToast(mContext, "mediaRecorder 准备出错，录屏失败", false);
        }

        if (mediaProjection != null) {
            try {
                virtualDisplay = mediaProjection.createVirtualDisplay("interact_record", width, height, dpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mediaRecorder.getSurface(), null, null);
            } catch (Exception e) {
                e.printStackTrace();
                QZXTools.logE("createVirtualDisplay happen exception ", e);
                QZXTools.popToast(mContext, "createVirtualDisplay出错，录屏失败", false);
            }


            mediaProjection.registerCallback(new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    try {
                        if (virtualDisplay != null) {
                            virtualDisplay.release();
                            virtualDisplay = null;
                        }

                        if (mediaRecorder != null) {
                            mediaRecorder.stop();
                            mediaRecorder.reset();
                            mediaRecorder.release();
                            mediaRecorder = null;
                        }

                        if (mediaProjection != null) {
                            mediaProjection.unregisterCallback(this);
                            mediaProjection = null;
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                    }

                }
            }, null);
        }

    }

    /**
     * 开始录屏
     */
    public void startScreenRecord() {
        if (isScreenRecord) {
            return;
        }

        createVirtualDisplayAndMediaRecorder();

        mediaRecorder.start();
        isScreenRecord = true;
        QZXTools.logE("开始录屏了....", null);
        QZXTools.popToast(mContext, "开始录屏了....", false);
    }

    /**
     * 结束录屏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void stopScreenRecord() {
        if (isScreenRecord) {
            QZXTools.logE("结束录屏了....", null);
            QZXTools.popToast(mContext, "结束录屏了....", false);

            isScreenRecord = false;
            mediaProjection.stop();

            if (!TextUtils.isEmpty(recordFilePath)) {
                EventBus.getDefault().post(recordFilePath, Constant.Screen_Record_file);
            }
        }
    }

    /**
     * 暂停录屏
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    public void pauseScreenRecord() {
        if (isScreenRecord) {
            QZXTools.logE("暂停录屏了....", null);

            isScreenRecord = false;
            mediaRecorder.pause();
        }
    }

    /**
     * 继续录屏
     */
    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.N)
    public void resumeScreenRecord() {
        if (isScreenRecord) {
            QZXTools.logE("继续录屏了....", null);

            isScreenRecord = false;
            mediaRecorder.resume();
        }
    }
}
