package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/5/23 8:26
 */
public class ResponseResultDialog extends DialogFragment {

    private TextView response_name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_response_result_layout, container, false);

        response_name = view.findViewById(R.id.response_name);

        view.findViewById(R.id.response_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    /**
     * 显示抢答成功的同学的名字
     */
    public void setResponseSuccessName(String successName) {
        response_name.setText(successName);
    }

}
