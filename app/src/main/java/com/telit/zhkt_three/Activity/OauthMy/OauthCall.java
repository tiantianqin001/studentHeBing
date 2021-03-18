package com.telit.zhkt_three.Activity.OauthMy;



import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import org.xwalk.core.JavascriptInterface;

/**
 * author: qzx
 * Date: 2019/6/5 19:45
 */
public class OauthCall {

    public String loginName;
    public String tgt;

    @JavascriptInterface
    public void loginSuccess(String loginName, String tgt) {
        // loginName=MX1@@S1;tgt=aa8366f53aee2033783c2430a88746ef
        QZXTools.logE("loginName=" + loginName + ";tgt=" + tgt, null);
        this.loginName = loginName;
        this.tgt = tgt;
        EventBus.getDefault().post(tgt, "getTgt");
    }

    @JavascriptInterface
    public void testJsCallAndroid(String msg) {
        QZXTools.logE("js call android ===> msg=" + msg, null);
    }
}
