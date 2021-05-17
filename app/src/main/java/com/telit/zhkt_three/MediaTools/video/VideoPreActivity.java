package com.telit.zhkt_three.MediaTools.video;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dueeeke.videocontroller.StandardVideoController;
import com.dueeeke.videoplayer.player.IjkVideoView;
import com.dueeeke.videoplayer.player.PlayerConfig;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;


/**
 * 使用示例：
 * Intent intent_video = new Intent(getContext(), VideoPlayerActivity.class);
 * intent_video.putExtra("VideoFilePath", clickTV.getPreviewUrl());
 * intent_video.putExtra("VideoTitle", clickTV.getFileName());
 * intent_video.putExtra("VideoThumbnail", clickTV.getThumbnail());
 * <p>
 * intent_video.putExtra("shareId", clickTV.getShareId() + "");
 * intent_video.putExtra("shareTitle", clickTV.getShareTitle());
 * intent_video.putExtra("resId", clickTV.getResId());
 * intent_video.putExtra("resName", clickTV.getFileName());
 * <p>
 * getContext().startActivity(intent_video);
 */
public class VideoPreActivity extends BaseActivity {
    private String shareId;
    private String shareTitle;
    private String resId;
    private String resName;

    private IjkVideoView ijkVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_pre);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        ImageView video_close = findViewById(R.id.video_close);
        TextView tv_top_video_name = findViewById(R.id.tv_top_video_name);
        video_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
                finish();
            }
        });

        //传入音频地址
        Intent intent = getIntent();

        String videoFilePath = "http://vfx.mtime.cn/Video/2019/02/04/mp4/190204084208765161.mp4";
        String videoTitle = "美国大兵";
        String videoThumbnail = intent.getStringExtra("VideoThumbnail");
        tv_top_video_name.setText(videoTitle);

        //评论接口需要
        shareId = intent.getStringExtra("shareId");
        shareTitle = intent.getStringExtra("shareTitle");
        resId = intent.getStringExtra("resId");
        resName = intent.getStringExtra("resName");
        String currentVideo = intent.getStringExtra("currentVideo");

        //是否存在评论内容
        String resComment = intent.getStringExtra("resComment");

        if (TextUtils.isEmpty(videoFilePath)) {
            QZXTools.popCommonToast(this, "视频播放地址无效", false);
            return;
        }

        LinearLayout video_note = findViewById(R.id.video_note);

        if (TextUtils.isEmpty(shareId)) {
            //隐藏评论
            video_note.setVisibility(View.GONE);
        } else {
            if (resComment == null) {
                video_note.setVisibility(View.VISIBLE);
            } else {
                video_note.setVisibility(View.GONE);
            }
        }


        ijkVideoView = findViewById(R.id.player);

        ijkVideoView.setUrl(videoFilePath); //设置视频地址
        ijkVideoView.setTitle(videoTitle); //设置视频标题
        StandardVideoController controller = new StandardVideoController(this);



        ijkVideoView.setVideoController(controller); //设置控制器，如需定制可继承BaseVideoController

        //高级设置（可选，须在start()之前调用方可生效）
        PlayerConfig playerConfig = new PlayerConfig.Builder()
                .enableCache() //启用边播边缓存功能
                .enableMediaCodec()//启动硬解码，启用后可能导致视频黑屏，音画不同步
                .usingSurfaceView() //启用SurfaceView显示视频，不调用默认使用TextureView
                .savingProgress() //保存播放进度
                .disableAudioFocus() //关闭AudioFocusChange监听
                .build();
        ijkVideoView.setPlayerConfig(playerConfig);

        ijkVideoView.start(); //开始播放，不调用则不自动播放
    }

    @Override
    protected void onPause() {
        super.onPause();
        ijkVideoView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ijkVideoView.resume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ijkVideoView.release();
    }


    @Override
    public void onBackPressed() {
        if (!ijkVideoView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
