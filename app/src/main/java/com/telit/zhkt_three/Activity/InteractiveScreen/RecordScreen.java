package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.webkit.JavascriptInterface;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

/**
 * author: qzx
 * Date: 2019/11/12 16:37
 */
public class RecordScreen {
    /**
     * @param command 1表示开始 0表示结束 2表示暂停
     * @param json    提交答案的Json,录制视频不需要传
     */
    @JavascriptInterface
    public void getRecordScreenCommand(int command, String json) {
        QZXTools.logE("js call android ===> json=" + json, null);
        JsRecordScreenBean jsRecordScreenBean = new JsRecordScreenBean(command, json);
        EventBus.getDefault().post(jsRecordScreenBean, Constant.Show_Js_Record);
    }
}
