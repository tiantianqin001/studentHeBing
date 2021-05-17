package com.telit.zhkt_three.MediaTools.audio;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.MediaTools.CommentActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 使用示例：
 * Intent intent = new Intent(getContext(), AudioPlayActivity.class);
 * intent.putExtra("AudioFilePath", clickTV.getPreviewUrl());
 * intent.putExtra("AudioFileName", clickTV.getFileName());
 * <p>
 * intent.putExtra("shareId", clickTV.getShareId() + "");
 * intent.putExtra("shareTitle", clickTV.getShareTitle());
 * intent.putExtra("resId", clickTV.getResId());
 * intent.putExtra("resName", clickTV.getFileName());
 * <p>
 * getContext().startActivity(intent);
 */
public class AudioPlayActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.audio_musicView)
    CustomMyMusicView customMyMusicView;
    @BindView(R.id.audio_note)
    LinearLayout audio_note;
    @BindView(R.id.audio_close)
    TextView audio_close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_play);
        unbinder = ButterKnife.bind(this);

        audio_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (customMyMusicView!=null){
                    customMyMusicView.releaseMusicResource();
                }

                finish();
            }
        });

        //传入音频地址
        Intent intent = getIntent();

        String audioFilePath = intent.getStringExtra("AudioFilePath");
        String audioFileName = intent.getStringExtra("AudioFileName");

        //评论接口需要
        String shareId = intent.getStringExtra("shareId");
        String shareTitle = intent.getStringExtra("shareTitle");
        String resId = intent.getStringExtra("resId");
        String resName = intent.getStringExtra("resName");

        //是否存在评论内容
        String resComment = intent.getStringExtra("resComment");

        if (TextUtils.isEmpty(audioFilePath)) {
            QZXTools.popCommonToast(this, "音频播放地址无效", false);
            return;
        }

    /*    if (TextUtils.isEmpty(shareId)) {
            //隐藏评论
            audio_note.setVisibility(View.GONE);
        } else {
            if (resComment == null) {
                audio_note.setVisibility(View.VISIBLE);
            } else {
                audio_note.setVisibility(View.GONE);
            }
        }*/

        if (TextUtils.isEmpty(audioFileName)) {
            customMyMusicView.setMusicName(null);
        } else {
            customMyMusicView.setMusicName(audioFileName);
        }
        customMyMusicView.setMusicSource(audioFilePath, false);

        audio_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(shareId) || TextUtils.isEmpty(shareTitle)
                        || TextUtils.isEmpty(resId) || TextUtils.isEmpty(resName)) {
                    QZXTools.popToast(AudioPlayActivity.this, "缺少评论所需的参数！", false);
                    return;
                }

                Intent intent_comment = new Intent(AudioPlayActivity.this, CommentActivity.class);
                intent_comment.putExtra("shareId", shareId);
                intent_comment.putExtra("shareTitle", shareTitle);
                intent_comment.putExtra("resId", resId);
                intent_comment.putExtra("resName", resName);
                startActivity(intent_comment);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (customMyMusicView != null) {
            customMyMusicView.releaseMusicResource();
        }
        super.onDestroy();
    }

    /**
     * 监听返回事件
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            finish();

            if (customMyMusicView != null) {
                customMyMusicView.releaseMusicResource();
            }

            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

}
