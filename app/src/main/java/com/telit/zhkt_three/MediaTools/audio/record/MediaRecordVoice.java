package com.telit.zhkt_three.MediaTools.audio.record;

import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.File;
import java.io.IOException;

/**
 * author: qzx
 * Date: 2019/12/16 9:12
 * <p>
 * MP3音频录制
 * <p>
 * 保存的文件放在/storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/conclusionAudio.mp3
 */
public class MediaRecordVoice {
    private MP3Recorder mp3Recorder;

    /**
     * 录音反馈给界面的回调
     */
    private RecordInterface recordInterface;

    public void setRecordInterface(RecordInterface recordInterface) {
        this.recordInterface = recordInterface;
    }

    private static volatile MediaRecordVoice mediaRecordVoice;

    private MediaRecordVoice() {
    }

    public static MediaRecordVoice getInstance() {
        if (mediaRecordVoice == null) {
            synchronized (MediaRecordVoice.class) {
                if (mediaRecordVoice == null) {
                    mediaRecordVoice = new MediaRecordVoice();
                }
            }
        }
        return mediaRecordVoice;
    }

    /**
     * 返回保存的录音文件路径
     */
    public String getSaveRecordPath() {
        String toSaveFilePath = QZXTools.getExternalStorageForFiles(MyApplication.getInstance()
                .getApplicationContext(), null);
        toSaveFilePath = toSaveFilePath + File.separator + "conclusionAudio.mp3";
        return toSaveFilePath;
    }

    public MediaRecordVoice initRecord() {
        File file = new File(getSaveRecordPath());
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                QZXTools.logE("createNewFile Failed", null);
            }
        }

        mp3Recorder = new MP3Recorder(file);
        return mediaRecordVoice;
    }

    public void start() {
        try {
            if (mp3Recorder != null) {
                mp3Recorder.start();
                if (recordInterface != null) {
                    recordInterface.startRecord();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            QZXTools.logE("mp3录制发生错误", null);
        }
    }

    public void stop() {
        if (mp3Recorder != null) {
            mp3Recorder.stop();
            if (recordInterface != null) {
                recordInterface.stopRecord();
            }
        }
    }

    /**
     * 返回音量等级对应img动态图
     */
    public int byVolumnToLevel(int maxLevel) {
        if (maxLevel <= 0) {
            throw new IllegalArgumentException("maxLevel不能为零或者负数");
        }
        //放置一开始授权阶段获取音量
        if (mp3Recorder == null) {
            return 0;
        }
        //这里是2000
        int maxVolume = mp3Recorder.getMaxVolume();
        int avgLevelVolume = Math.round(maxVolume / maxLevel);
        //获取实际音量值
        int actualVolume = mp3Recorder.getVolume();

        QZXTools.logE("actualVolume=" + actualVolume + ";avgLevelVolume=" + avgLevelVolume, null);

        return actualVolume / avgLevelVolume;
    }

}
