package com.telit.zhkt_three.Activity.HomeScreen;

import android.os.Bundle;
import android.view.View;

import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.R;

public class ForgetPwdActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pwd);

        findViewById(R.id.forget_pwd_del).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
