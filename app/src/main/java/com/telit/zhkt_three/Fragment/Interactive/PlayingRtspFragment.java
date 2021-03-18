package com.telit.zhkt_three.Fragment.Interactive;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.MediaTools.video.CustomMedia.JZMediaIjk;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;


import cn.jzvd.JZMediaAliyun;

import cn.jzvd.JZMediaSystem;
import cn.jzvd.JzvdStdTuiLiu;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * author: qzx
 * Date: 2020/4/8 10:57
 */
public class PlayingRtspFragment extends Fragment {

    private JzvdStdTuiLiu texture_view;

    private CircleProgressDialogFragment circleProgressDialog;
    private SurfaceView surfaceView;


    private Handler myHandler = new Handler() {

    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_playing_rtsp, container, false);
        texture_view = view.findViewById(R.id.texture_view);
        circleProgressDialog = new CircleProgressDialogFragment();
        circleProgressDialog.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        String rtsp_url = null;

        Bundle bundle = getArguments();
        if (bundle != null) {
           rtsp_url = bundle.getString("rtsp_url");
            //  rtsp_url = "rtmp://192.168.11.31/live/tiantainqin";
        }

        if (TextUtils.isEmpty(rtsp_url)) {
            throw new NullPointerException("rtsp_url is null");
        }
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                circleProgressDialog.dismissAllowingStateLoss();
            }
        }, 1000);

        texture_view.setUp(rtsp_url, null);
     //   texture_view.setUp("rtmp://192.168.3.15/live/tiantainqin", null);

        //设置阿里云播放内核
        //texture_view.setMediaInterface(JZMediaAliyun.class);
        //  texture_view.setMediaInterface(JZMediaSystem.class);
      /*  String[] strings = rtsp_url.split(":");
        if (strings[0].equals("rtmp")){
            texture_view.setMediaInterface(JZMediaAliyun.class);
        }
        if (strings[0].equals("rtsp")){
            texture_view.setMediaInterface(JZMediaIjk.class);
        }*/

        texture_view.setMediaInterface(JZMediaIjk.class);

      //  texture_view.setMediaInterface(JZMediaAliyun.class);

        texture_view.startVideo();
        //隐藏不需要的view
        //学生端禁音
    /*    AudioManager mAudioManager = (AudioManager) MyApplication.getInstance().getSystemService(Context.AUDIO_SERVICE);
        if (mAudioManager != null) {
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
        }*/

        return view;
    }


    @Override
    public void onDestroyView() {
        if (texture_view != null) {

            texture_view.releaseAllVideos();
            texture_view=null;
        }
        super.onDestroyView();
    }
}
