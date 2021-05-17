package com.telit.zhkt_three.Activity.OauthMy;



import android.util.Log;

import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.zbv.meeting.util.SharedPreferenceUtil;

import org.xwalk.core.JavascriptInterface;

import java.util.Calendar;

/**
 * author: qzx
 * Date: 2019/6/5 19:45
 */
public class OauthCall {

    public String loginName;
    public String tgt;
    private long tem = 0;
    @JavascriptInterface
    public void loginSuccess(String loginName, String tgt) {
        SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getTgt",tgt);
        // loginName=MX1@@S1;tgt=aa8366f53aee2033783c2430a88746ef
        //获取系统的当前时间
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        if (tem != 0 && timeInMillis - tem < 2500) {
            return;

        } else {
            this.loginName = loginName;
            this.tgt = tgt;
            EventBus.getDefault().post(tgt, "getTgt");
            long loneTime = timeInMillis - tem;
            Log.i("login", "loginSuccess: loneTime="+loneTime );
            QZXTools.logD("tiantianqinLogin...........loginSuccess..."+"loginName=" + loginName + ";tem=" + tgt);

        }
        tem = timeInMillis;
    }

    @JavascriptInterface
    public void testJsCallAndroid(String msg) {
        QZXTools.logE("js call android ===> msg=" + msg, null);
    }
}
