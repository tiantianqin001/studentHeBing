package com.telit.zhkt_three.dialoge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.telit.zhkt_three.R;

/**
 * Created by alany on 2017/4/12.
 */
public class LoadDialog extends Dialog {
    private Context context;



    public LoadDialog(Context context){
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
        View view = inflater.inflate(R.layout.fragment_load_layout, null);
        setContentView(view);
    }

}