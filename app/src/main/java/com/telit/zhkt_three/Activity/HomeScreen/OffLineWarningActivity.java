package com.telit.zhkt_three.Activity.HomeScreen;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.Jpush.JpushApply;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.manager.AppManager;

/**
 * 账号在另外一台设备登录退出通知框
 * <p>
 * notes:做了按返回键Dialog不消失
 */
public class OffLineWarningActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //------------------处理下线操作-------------------
        //第一步登录标记清除以及认证ID清除以及tgt
        SharedPreferences sp_student = OffLineWarningActivity.this.getSharedPreferences("student_info",
                Context.MODE_PRIVATE);
        UserUtils.setBooleanTypeSpInfo(sp_student, "isLoginIn", false);
        UserUtils.setOauthId(sp_student, "oauth_id", "");
        UserUtils.removeTgt();
        //第二步解除极光推送
        JpushApply.getIntance().unRegistJpush(MyApplication.getInstance());
        //第三步如果处于互动状态的话，还要清除互动Netty---这一步在Activity销毁时已实现
//        AppManager.getAppManager().finishAllActivity();
        //------------------处理下线操作-------------------

        TipsDialog tipsDialog = new TipsDialog();
        //使能按返回键dialog不消失
        tipsDialog.setBackNoMiss();
        tipsDialog.setTipsStyle("您的账号已在另一台平板上登录，您已被迫下线！",
                null, "确定", -1);
        tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
            @Override
            public void cancle() {

            }

            @Override
            public void confirm() {
                AppManager.getAppManager().finishAllActivity();
            }
        });
        tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
    }
}
