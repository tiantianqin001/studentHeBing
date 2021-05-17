package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.telit.zhkt_three.CustomView.Download.DownloadProgressBar;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;

/**
 * author: qzx
 * Date: 2019/12/23 20:39
 */
public class TBSDownloadDialog extends DialogFragment {
    private DownloadProgressBar tbs_downloadBar;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_tbs_download, container, false);
        tbs_downloadBar = view.findViewById(R.id.tbs_downloadBar);
        return view;
    }

    @Override
    public void onDestroyView() {
        OkHttp3_0Utils.getInstance().cancelTagRequest("123");
        super.onDestroyView();
    }

    public void download(int value) {
        try {
            tbs_downloadBar.setProgress(value);
        }catch (Exception e){
            e.fillInStackTrace();
        }

    }
    @Override
    public void show(FragmentManager manager, String tag) {
        if (!isAdded()){
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            // 这里吧原来的commit()方法换成了commitAllowingStateLoss()
            ft.commitAllowingStateLoss();
        }
    }
}
