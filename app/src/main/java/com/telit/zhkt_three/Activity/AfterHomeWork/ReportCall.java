package com.telit.zhkt_three.Activity.AfterHomeWork;

import android.webkit.JavascriptInterface;

import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

/**
 * author: qzx
 * Date: 2019/9/28 17:36
 */
public class ReportCall {

    @JavascriptInterface
    public void getReportJson(String json) {
        QZXTools.logE("js call android ===> json=" + json, null);
        EventBus.getDefault().post(json,"reportJson");
    }
}
