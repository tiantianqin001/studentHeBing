package com.telit.zhkt_three.MediaTools.audio;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.telit.zhkt_three.Utils.QZXTools;

import java.util.Timer;
import java.util.TimerTask;

import com.telit.zhkt_three.R;

/**
 * Created by Administrator on 2018/5/21.
 * qzx
 * 使用示例：
 * CustomMyMusicView customMyMusicView= findViewById(R.id.musicView);
 * customMyMusicView.setMusicName(null);
 * customMyMusicView.setMusicSource(
 * "/storage/emulated/0/Android/data/com.telit.smartclass.desktop/files/01f2ae95f4ae4dd0991d10a42313222b.mp3", false);
 * 点击按钮播放音频
 */

public class CustomMyMusicView extends RelativeLayout implements View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {
    private Context mContext;
    private ImageView img_play;
    private TextView tv_musicName;
    private SeekBar seekBar_music;
    private TextView tv_musicTime;

    private MediaPlayer mediaPlayer;

    //音频的资源
    private boolean isAssets = false;
    private String sourcePath = null;
    private int sourceId = -1;
    private Uri sourceUri = null;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            int totalP = bundle.getInt("totalProgress");
            int curP = bundle.getInt("currentPosition");

            setTimeShow(curP, totalP);

            seekBar_music.setMax(totalP);
            seekBar_music.setProgress(curP);
        }
    };

    public CustomMyMusicView(Context context) {
        this(context, null);
    }

    public CustomMyMusicView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomMyMusicView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();

        //创建音频播放器对象
        mediaPlayer = new MediaPlayer();
    }

    private void initView() {
        View musicView = LayoutInflater.from(mContext).inflate(R.layout.music_view_layout, this, true);
        img_play = (ImageView) musicView.findViewById(R.id.music_img);
        tv_musicName = (TextView) musicView.findViewById(R.id.music_tvName);
        seekBar_music = (SeekBar) musicView.findViewById(R.id.music_sb);
        tv_musicTime = (TextView) musicView.findViewById(R.id.music_tvTime);
        img_play.setOnClickListener(this);
        seekBar_music.setOnSeekBarChangeListener(this);
    }

    private Timer timer;

    private void updatePlayInfo() {

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                //当前播放的进度
                int currentPosition = mediaPlayer.getCurrentPosition();

                Message msg = mHandler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putInt("totalProgress", mediaPlayer.getDuration());
                //会替代之前赋予的值---map
                bundle.putInt("currentPosition", currentPosition);

                //222015---毫秒
//                QZXTools.logD("zbv", "totalTime=" + mediaPlayer.getDuration() + ";curTime=" + currentPosition);

                //塞进消息中
                msg.setData(bundle);
                mHandler.sendMessage(msg);


            }
        }, 0, 1000);
    }

    //播放、暂停、继续和跳转到---播放成功就执行一次，因为没必要每次都加载资源
    private void playMusic(int resId) {

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            //重置到要初始化的状态，需要再次设置播放资源data以及prepare()
            mediaPlayer.reset();

//            //设置重复播放
//            mediaPlayer.setLooping(true);

            //设置raw文件---assets也是同样的，但是通过getAssets()打开
            AssetFileDescriptor afd = getResources().openRawResourceFd(resId);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());

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
                    QZXTools.logE("OnCompletionListener", null);
                    if (!mediaPlayer.isPlaying()) {
                        isPlayingMusic = false;
                        img_play.setImageResource(R.drawable.play_icon);
                    }
                }
            });

            //获取播放的进度信息，每隔1秒更新一次---设置定时器
            updatePlayInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void playMusic(String path, boolean isAssets) {

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            //重置到要初始化的状态，需要再次设置播放资源data以及prepare()
            mediaPlayer.reset();

            if (isAssets) {
                //设置raw文件---assets也是同样的，但是通过getAssets()打开
                AssetFileDescriptor afd = mContext.getAssets().openFd(path);
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

            //获取播放的进度信息，每隔1秒更新一次---设置定时器
            updatePlayInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void playMusic(Uri uri) {

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            //重置到要初始化的状态，需要再次设置播放资源data以及prepare()
            mediaPlayer.reset();

            //设置网络资源
            mediaPlayer.setDataSource(mContext, uri);

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

            //获取播放的进度信息，每隔1秒更新一次---设置定时器
            updatePlayInfo();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 设置音乐名称
     */
    public void setMusicName(String musicName) {
        if (TextUtils.isEmpty(musicName)) {
            tv_musicName.setVisibility(GONE);
        } else {
            tv_musicName.setVisibility(VISIBLE);
            tv_musicName.setText(musicName);
        }
    }

    /**
     * 设置音频播放源
     * String path---SD卡或者本地文件还有Assets
     */
    public void setMusicSource(String path, boolean isAssets) {

        this.isAssets = isAssets;
        sourcePath = path;
    }

    /**
     * 设置音频播放源
     * URI---网络资源
     */
    public void setMusicSource(Uri uri) {

        sourceUri = uri;
    }

    /**
     * 设置音频播放源
     * int---resId资源ID
     */
    public void setMusicSource(int resId) {

        sourceId = resId;
    }

    private boolean isInitMusic = false;
    private boolean isPlayingMusic = false;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.music_img) {
            //Log.d("zbv", "music button");
            if (!isInitMusic) {
                isInitMusic = true;
                if (sourceUri != null) {
                    playMusic(sourceUri);
                } else if (sourceId != -1) {
                    playMusic(sourceId);
                } else if (sourcePath != null) {
                    playMusic(sourcePath, isAssets);
                } else {
                    Toast.makeText(mContext, "您没有设置播放源", Toast.LENGTH_SHORT).show();
                    return;
                }
                img_play.setImageResource(R.drawable.pause_icon);
                isPlayingMusic = true;
            } else {
                if (isPlayingMusic) {
                    mediaPlayer.pause();
                    img_play.setImageResource(R.drawable.play_icon);
                    isPlayingMusic = false;
                } else {
                    img_play.setImageResource(R.drawable.pause_icon);
                    mediaPlayer.start();
                    isPlayingMusic = true;
                }
            }

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

        int progress = seekBar.getProgress();
        mediaPlayer.seekTo(progress);
    }

    //释放后需要重新创建对象
    public void releaseMusicResource() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        mHandler.removeCallbacksAndMessages(null);

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            //释放资源
            mediaPlayer.release();
            mediaPlayer = null;
        }

    }

    /**
     * 更新时间进度UI
     */
    private void setTimeShow(int curProgress, int totalProgress) {
        int minute_total = totalProgress / 1000 / 60;
        int second_total = totalProgress / 1000 % 60;

        int minute_cur = curProgress / 1000 / 60;
        int second_cur = curProgress / 1000 % 60;

//        QZXTools.logD("zbv", "minute_total=" + minute_total + ";second_total=" + second_total
//                + ";minute_cur=" + minute_cur + ";second_cur=" + second_cur);

        String totalMinute;
        String totalSecond;
        String curMinute;
        String curSecond;

        if (minute_total < 10) {
            totalMinute = "0" + minute_total;
        } else {
            totalMinute = "" + minute_total;
        }
        if (second_total < 10) {
            totalSecond = "0" + second_total;
        } else {
            totalSecond = "" + second_total;
        }

        if (minute_cur < 10) {
            curMinute = "0" + minute_cur;
        } else {
            curMinute = "" + minute_cur;
        }
        if (second_cur < 10) {
            curSecond = "0" + second_cur;
        } else {
            curSecond = "" + second_cur;
        }

        tv_musicTime.setText(curMinute + ":" + curSecond + "/" + totalMinute + ":" + totalSecond);
    }
}
