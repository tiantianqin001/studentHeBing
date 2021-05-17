package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.FileReceive.FileReceiveProgressView;
import com.telit.zhkt_three.JavaBean.Communication.FileReceive;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/5/15 19:57
 * <p>
 * Dialog的布局必须要布局下的每个孩子都是宽度Match_parent,这样才能保证显示还不错
 * <p>
 * 文件接收一律放入/storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/SaveFile/下
 */
public class FileReceiveDialog extends DialogFragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.file_receive_title)
    TextView receive_title;
    @BindView(R.id.file_receive_name)
    TextView receive_name;
    @BindView(R.id.file_receive_size)
    TextView receive_size;
    @BindView(R.id.file_receive_progress)
    FileReceiveProgressView receive_progressBar;
    @BindView(R.id.file_receive_imgs)
    LinearLayout receive_layout;
    @BindView(R.id.file_receive_close)
    ImageView receive_close;
    @BindView(R.id.file_receive_open)
    ImageView receive_open;

    private String fileBodyString;

    private FileReceive fileReceive;

    private String localFilePath;

    /**
     * 更新App的下载地址
     */
    private String downloadUrl;

    public void setFileBodyString(boolean isUpdateApp, String downloadUrl, String fileBodyString) {
        if (isUpdateApp) {
            this.downloadUrl = downloadUrl;
        } else {
            this.fileBodyString = fileBodyString;
            Gson gson = new Gson();
            fileReceive = gson.fromJson(fileBodyString, FileReceive.class);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwdTwo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apk_install_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        receive_close.setOnClickListener(this);
        receive_open.setOnClickListener(this);

        if (downloadUrl != null) {
            receive_title.setText("下载新版本");

            receive_name.setVisibility(View.GONE);
            receive_size.setVisibility(View.GONE);


            OkHttp3_0Utils.getInstance().downloadSingleFileForOnce(downloadUrl, null, new OkHttp3_0Utils.DownloadCallback() {
                @Override
                public void downloadProcess(int value) {
                    //todo nullPoint
                    receive_progressBar.setCurProgress(value);
                }

                @Override
                public void downloadComplete(String filePath) {

                    localFilePath = filePath;
                    SharedPreferenceUtil.getInstance(MyApplication.getInstance())
                            .setString("localFilePathApk",filePath);

                    EventBus.getDefault().post(localFilePath, Constant.CAN_INSTALL);



                    QZXTools.installApk(getActivity(), new File(localFilePath));

                    dismiss();
                }

                @Override
                public void downloadFailure() {
                    QZXTools.popToast(getContext(), "文件下载失败", false);
                    dismiss();
                }
            });
        }


        if (fileReceive != null) {
            receive_name.setText("文件名：" + fileReceive.getFileName());
            long fileSize = Long.parseLong(fileReceive.getFileSize());
            receive_size.setText("文件大小：" + QZXTools.transformBytes(fileSize));

            //下载文件
            String downloadUrl = fileReceive.getDownloadPath();
            OkHttp3_0Utils.getInstance().downloadSingleFileForOnce(downloadUrl, "SaveFile", new OkHttp3_0Utils.DownloadCallback() {
                @Override
                public void downloadProcess(int value) {
                    //首先弹吐司会影响进度显示，会出现卡顿现象，所以删除弹吐司操作
                    receive_progressBar.setCurProgress(value);
                }

                @Override
                public void downloadComplete(String filePath) {
                    localFilePath = filePath;
                    receive_layout.setVisibility(View.VISIBLE);
                }

                @Override
                public void downloadFailure() {
                    QZXTools.popToast(getContext(), "文件下载失败", false);
                }
            });

        }
        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        //消除泄露
        QZXTools.setmToastNull();
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.file_receive_close:
                dismiss();
                break;
            case R.id.file_receive_open:
                QZXTools.openFile(new File(localFilePath), getContext());
                dismiss();
                break;
        }
    }
}
