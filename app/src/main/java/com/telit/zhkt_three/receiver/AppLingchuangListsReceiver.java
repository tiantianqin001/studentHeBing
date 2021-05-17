package com.telit.zhkt_three.receiver;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.Service.AppInfoService;
import com.telit.zhkt_three.Utils.ApkListInfoUtils;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/3/29 15:26
 * <p>
 * 监听App的安装和卸载以及更新
 */
public class AppLingchuangListsReceiver extends BroadcastReceiver {
    private List<String> appsLists=new ArrayList<>();
    private  List<AppInfo> datas=new ArrayList<>();
    @Override
    public void onReceive(Context context, Intent intent) {
        List<String> hide_show_apps = intent.getStringArrayListExtra("hide_show_apps");

        appsLists.clear();
        //这个必须点击10次   也就是调10次home键才能收到广播  这个是获取第三方app 的应用
        if (hide_show_apps!=null){
            for (String app :hide_show_apps){
                appsLists.add(app);
            }
        }
        initlingchaungList(appsLists);
    }

    private void initlingchaungList(List<String> appsListss) {
        ApkListInfoUtils.getInstance().onStart();
        if (!datas.isEmpty()&& datas.size()>0){
            datas.clear();
        }
        appsListss.add("com.ndwill.swd.appstore");
        ApkListInfoUtils.getInstance().getAppSystem("lingchang", appsListss);

    }
}
