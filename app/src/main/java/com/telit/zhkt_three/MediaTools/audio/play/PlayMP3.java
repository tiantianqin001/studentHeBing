package com.telit.zhkt_three.MediaTools.audio.play;

import android.content.res.AssetFileDescriptor;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.media.SoundPool;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

/**
 * author: qzx
 * Date: 2019/12/16 15:34
 * <p>
 * MediaPlay播放MP3音频
 */
public class PlayMP3 {
    private static volatile PlayMP3 playMP3;

    private MediaPlayer mediaPlayer;

    private PlayMP3() {
        mediaPlayer = new MediaPlayer();
    }

    public static PlayMP3 getInstance() {
        if (playMP3 == null) {
            synchronized (PlayMP3.class) {
                if (playMP3 == null) {
                    playMP3 = new PlayMP3();
                }
            }
        }
        return playMP3;
    }

    public void playMusic(String path, boolean isAssets) {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            //重置到要初始化的状态，需要再次设置播放资源data以及prepare()
            mediaPlayer.reset();

            if (isAssets) {
                //设置raw文件---assets也是同样的，但是通过getAssets()打开
                AssetFileDescriptor afd = MyApplication.getInstance().getApplicationContext().getAssets().openFd(path);
                mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            } else {
                mediaPlayer.setDataSource(path);
            }

//            //同步资源加载如果资源大的话会卡顿--抛出IOException
//            mediaPlayer.prepare();
//            mediaPlayer.start();

            //异步加载资源
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    EventBus.getDefault().post("audio_completed", Constant.Play_Audio_Completed);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public int getAudioDuration() {
        if (mediaPlayer.isPlaying()) {
            return mediaPlayer.getDuration();
        } else {
            return 0;
        }
    }
}
