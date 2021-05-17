package com.telit.zhkt_three.Activity.HomeScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.OauthMy.ProviceActivity;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.Jpush.JpushApply;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.manager.AppManager;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 账号在另外一台设备登录退出通知框
 * <p>
 * notes:做了按返回键Dialog不消失
 */
public class OffLineWarningActivity extends BaseActivity {
    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Alter_Photo_Result = 2;
    private static final int OffLine_Success = 3;
    private static final int OffLine_Failed = 4;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                case OffLine_Failed:
                case OffLine_Success:
                    clearSetting();
                    break;
            }
        }
    };

    /**
     * 清空设置
     */
    private void clearSetting(){
        //设置未登录标志
        SharedPreferences sharedPreferences = getSharedPreferences("student_info", MODE_PRIVATE);
//                    UserUtils.setBooleanTypeSpInfo(sharedPreferences, "isLoginIn", false);
        UserUtils.setOauthId(sharedPreferences, "oauth_id", "");
        UserUtils.removeTgt();

        SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getTgt","");

        //领创管控唤起管理员
        lingChang();

        /**
         * 登出解除极光推送
         * */
        JpushApply.getIntance().unRegistJpush(MyApplication.getInstance());

        //撤销别名
        MiPushClient.unsetAlias(OffLineWarningActivity.this, UserUtils.getUserId(), null);

        //退出登录的埋点
        BuriedPointUtils.buriedPoint("2002","","","","");

        startActivity(new Intent(OffLineWarningActivity.this, ProviceActivity.class));

        AppManager.getAppManager().finishAllActivity();
    }

    private void lingChang() {
        Intent intent = new Intent("com.android.launcher3.mdm.OPEM_ADMIN");
        intent.setPackage("com.android.launcher3");

        sendBroadcast(intent);

        // Toast.makeText(mContext,"领创发com.linspirer.edu.homeaction广播",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                loginOut();
            }
        });
        tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
    }

    private void loginOut(){
        //领创管控的退出 todo
        Intent intent = new Intent("com.linspirer.edu.logout");
        intent.setPackage("com.android.launcher3");
        sendBroadcast(intent);

        String url = UrlUtils.BaseUrl + UrlUtils.LoginInOutRecord;
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("classid", UserUtils.getClassId());
        paramMap.put("userid", UserUtils.getUserId());
        paramMap.put("roletype", "1");
        paramMap.put("delflag", "1");
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paramMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("onFailure e=" + e, null);
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    QZXTools.logE("result=" + response.body().string(), null);
                    mHandler.sendEmptyMessage(OffLine_Success);
                } else {
                    mHandler.sendEmptyMessage(OffLine_Failed);
                }
            }
        });
    }
}
