package com.telit.zhkt_three.MediaTools.video;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.MediaTools.CommentActivity;
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
public class VideoPlayerActivity extends BaseActivity implements NiceVideoPlayer.OnVideoCompletionListener, TxVideoPlayerController.OnBackClickListener {

    private NiceVideoPlayer nice_video_player;
    private String shareId;
    private String shareTitle;
    private String resId;
    private String resName;

    private TxVideoPlayerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
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

        String videoFilePath = intent.getStringExtra("VideoFilePath");
        String videoTitle = intent.getStringExtra("VideoTitle");
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
        nice_video_player = findViewById(R.id.nice_video_player);

        nice_video_player.setPlayerType(NiceVideoPlayer.TYPE_NATIVE);

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

        nice_video_player.setPlayerType(NiceVideoPlayer.TYPE_NATIVE);
        controller = new TxVideoPlayerController(this, this);
        controller.setOnBackClickListener(this);
        controller.setTitle(shareTitle);

        nice_video_player.setController(controller);
        nice_video_player.setUp(videoFilePath,null);

       // nice_video_player.setOnVideoCompletionListener(this);

        if (!TextUtils.isEmpty(videoThumbnail)) {
            Glide.with(this).load(videoFilePath).into(controller.imageView());
        }

        nice_video_player.continueFromLastPosition(false);

        //开始播放
        nice_video_player.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        NiceVideoPlayerManager.instance().resumeNiceVideoPlayer();
    }

    @Override
    protected void onDestroy() {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        super.onDestroy();
    }

    @Override
    public void onVideoCompletionListener() {
        if (TextUtils.isEmpty(shareId) || TextUtils.isEmpty(shareTitle)
                || TextUtils.isEmpty(resId) || TextUtils.isEmpty(resName)) {
          //  QZXTools.popToast(VideoPlayerActivity.this, "缺少评论所需的参数！", false);
            return;
        }

       /* Intent intent_comment = new Intent(VideoPlayerActivity.this, CommentActivity.class);
        intent_comment.putExtra("shareId", shareId);
        intent_comment.putExtra("shareTitle", shareTitle);
        intent_comment.putExtra("resId", resId);
        intent_comment.putExtra("resName", resName);
        startActivity(intent_comment);*/
    }

    @Override
    public void onBackClickListener() {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        finish();
    }
}
