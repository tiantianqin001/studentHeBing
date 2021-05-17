package com.telit.zhkt_three.Activity.HomeScreen;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.MyApplication;
import com.zbv.basemodel.QZXTools;
import com.zbv.basemodel.TipsDialog;
import com.zbv.basemodel.UpdateBean;

/**
 * 版本更新通知框
 * <p>
 * notes:做了按返回键Dialog不消失
 */
public class VersionUpdateActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String extra = getIntent().getStringExtra("extra");

        UpdateBean updateBean = new Gson().fromJson(extra, UpdateBean.class);

        TipsDialog tipsDialog = new TipsDialog();
        tipsDialog.setTipsStyle("有新版本更新！\n最新版本：" + updateBean.getVersionName() +
                        "\n当前版本：" + QZXTools.getVerionName(MyApplication.getInstance()) +
                        "\n版本描述：" + updateBean.getDescription(),
                "忽略更新", "立即更新", -1);
        tipsDialog.setClickInterface(new com.zbv.basemodel.TipsDialog.ClickInterface() {
            @Override
            public void cancle() {
                tipsDialog.dismissAllowingStateLoss();
                finish();
            }

            @Override
            public void confirm() {
                tipsDialog.dismissAllowingStateLoss();
                finish();

                //进入下载商店
                lingChang();
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.ndwill.swd.appstore");
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        });
        tipsDialog.show(getSupportFragmentManager(),
                TipsDialog.class.getSimpleName());
    }

    private void lingChang() {
        Intent intent = new Intent("com.linspirer.edu.homeaction");
        intent.setPackage("com.android.launcher3");
        sendBroadcast(intent);
    }
}
