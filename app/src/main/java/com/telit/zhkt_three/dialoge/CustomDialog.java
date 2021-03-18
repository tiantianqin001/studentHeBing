package com.telit.zhkt_three.dialoge;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;

/**
 * Created by alany on 2017/4/12.
 */
public class CustomDialog extends Dialog {
    private Context context;



    public CustomDialog(Context context){
        super(context, R.style.dialogForgetPwd);
        this.context = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setCancelable(true);
        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.fragment_lock_layout, null);
        setContentView(view);
    }

}