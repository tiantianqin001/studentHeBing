package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/5/30 19:26
 */
public class LockFragment extends DialogFragment {



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //设置背景透明
       View view= inflater.inflate(R.layout.fragment_lock_layout, container, false);
        RelativeLayout rl_sock=view.findViewById(R.id.rl_sock);

        return view;
    }


}
