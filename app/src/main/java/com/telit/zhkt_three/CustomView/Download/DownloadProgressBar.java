package com.telit.zhkt_three.CustomView.Download;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;

/**
 * 项目名称：desktop
 * 类名称：DownloadProgressBar
 * 类描述：下载进度条
 * 创建人：luxun
 * 创建时间：2017/5/5 0005 16:46JJJ
 * 修改人：luxun
 * 修改时间：2017/5/5 0005 16:46
 * 当前版本：v1.0
 */

public class DownloadProgressBar extends FrameLayout {
    private ImageView ivStatus;
    private CircleProgressBar cpbProgress;
    private TextView tvProgress;
    public static final int STATUS_READY = 0;
    public static final int STATUS_DOWNLOADING = 1;
    public static final int STATUS_WATING = 2;
    public static final int STATUS_ERROR = 3;
    public static final int STATUS_FINISH = 4;
    public static final int STATUS_PAUSE = 5;

    private int status = STATUS_READY;

    public DownloadProgressBar(@NonNull Context context) {
        this(context, null);
    }

    public DownloadProgressBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DownloadProgressBar(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        this.inflate(getContext(), R.layout.view_download_progressbar, this);
        ivStatus = (ImageView) this.findViewById(R.id.iv_status);
        cpbProgress = (CircleProgressBar) this.findViewById(R.id.cpb_progress);
        tvProgress = (TextView) this.findViewById(R.id.tv_progress);
        checkStatus();
    }

    /**
     * @describe 设置进度
     * @author luxun
     * create at 2017/5/5 0005 17:24
     */
    public void setProgress(float progress) {
        if (status != STATUS_DOWNLOADING) {
            this.status = STATUS_DOWNLOADING;
            checkStatus();
        }
        if (progress < 0) {
            progress = 0;
        }
        float p = progress * 100;
        cpbProgress.setProgress(p);
        tvProgress.setText((int) p + "%");
    }

    public void setProgress(int progress) {
        if (status != STATUS_DOWNLOADING) {
            this.status = STATUS_DOWNLOADING;
            checkStatus();
        }
        cpbProgress.setProgress(progress);
        tvProgress.setText(progress + "%");
    }

    /**
     * @describe 设置状态
     * @author luxun
     * create at 2017/5/10 0010 16:28
     */
    public void setStatus(int status) {
        this.status = status;
        checkStatus();
    }

    /**
     * 新增获取下载状态
     */
    public int getStatus() {
        return status;
    }

    /**
     * @describe 校检状态
     * @author luxun
     * create at 2017/5/10 0010 16:28
     */
    public void checkStatus() {
        restorView();
        switch (status) {
            case STATUS_READY:
                ivStatus.setVisibility(VISIBLE);
                ivStatus.setImageResource(R.mipmap.icon_download_ready);
                break;
            case STATUS_DOWNLOADING:
                cpbProgress.setVisibility(VISIBLE);
                tvProgress.setVisibility(VISIBLE);
                break;
            case STATUS_WATING:
                ivStatus.setVisibility(VISIBLE);
                ivStatus.setImageResource(R.mipmap.icon_download_wating);
                break;
            case STATUS_ERROR:
                ivStatus.setVisibility(VISIBLE);
                ivStatus.setImageResource(R.mipmap.icon_download_error);
                break;
            case STATUS_FINISH:
                ivStatus.setVisibility(VISIBLE);
                ivStatus.setImageResource(R.mipmap.icon_download_finish);
                break;
            case STATUS_PAUSE:
                ivStatus.setVisibility(VISIBLE);
                cpbProgress.setVisibility(VISIBLE);
                ivStatus.setImageResource(R.mipmap.icon_download_paruse);
                break;
        }
    }

    private void restorView() {
        ivStatus.setVisibility(INVISIBLE);
        cpbProgress.setVisibility(INVISIBLE);
        tvProgress.setVisibility(INVISIBLE);
    }


}
