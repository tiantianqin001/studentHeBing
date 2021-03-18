package com.telit.zhkt_three.MediaTools.video;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import cn.jzvd.JzvdStd;

/**
 * author: qzx
 * Date: 2019/5/15 9:36
 * <p>
 * 示例一：直接全屏播放
 * <p>
 * //采用bilibili引擎
 * JZVideoPlayer.setMediaInterface(new IjkMediaEngine());
 * JZVideoPlayerStandard.NORMAL_ORIENTATION = getRequestedOrientation();
 * JZVideoPlayer.startFullscreen(this, CustomeJZVideoPlayerStandard.class,
 * "http://112.27.234.67:8089/inetsoft-ccard/upload/tnews/15358108733277.mp4", "采访嘉禾苑小学");
 * </p>
 * 实例二：正常播放
 * <p>
 * //采用bilibili引擎
 * JZVideoPlayer.setMediaInterface(new IjkMediaEngine());
 * JZVideoPlayerStandard.NORMAL_ORIENTATION = getRequestedOrientation();
 * CustomeJZVideoPlayerStandard jzVideoPlayerStandard = findViewById(R.id.jz_video);
 * jzVideoPlayerStandard.setUp("http://112.27.234.67:8089/inetsoft-ccard/upload/tnews/15358108733277.mp4",
 * JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "采访");
 * Glide.with(this).load("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640").into(jzVideoPlayerStandard.thumbImageView);
 * </p>
 * <p>
 * 小窗口播放可参考github中饺子demo实例;
 * https://github.com/lipangit/JiaoZiVideoPlayer/wiki/%E5%B0%8F%E7%AA%97%E6%92%AD%E6%94%BE
 * </p>
 *
 * NOTES：全屏播放结束后自动退出
 */
public class CustomJZVideoPlayerStandard extends JzvdStd {
    private Context context;

    public CustomJZVideoPlayerStandard(Context context) {
        this(context, null);
    }

    public CustomJZVideoPlayerStandard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.back) {
            QZXTools.logE("video back", null);
            fullscreenButton.performClick();
        }
    }

    @Override
    public int getLayoutId() {
        return super.getLayoutId();
    }
}
