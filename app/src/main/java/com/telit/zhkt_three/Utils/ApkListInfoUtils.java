package com.telit.zhkt_three.Utils;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.greendao.AppInfoDao;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApkListInfoUtils {
    private ApkListInfoUtils(){}
    private static ApkListInfoUtils instance=new ApkListInfoUtils();

    //开线程获取APPINFO
    private ExecutorService executorService;
    //开线程遍历所有第三方应用

    public static ApkListInfoUtils getInstance() {
        synchronized (ApkListInfoUtils.class) {
            return instance;
        }
    }
    public ExecutorService onStart(){
        executorService = Executors.newSingleThreadExecutor();
        return executorService;
    }

    //获取领创的应用列表
    List<AppInfo> appInfos = null;
    public List<AppInfo> getAppSystem(String type, List<String> appsLists){

       if (type.equals("system")){
           executorService.execute(new Runnable() {
               @Override
               public void run() {
                   appInfos= getSystemApp(appsLists);
               }
           });
       }else if (type.equals("lingchang")){

           executorService.execute(new Runnable() {
               @Override
               public void run() {
                   appInfos= getLingchuangApp(appsLists);
               }
           });
       }else if (type.equals("hangziyun")){
           executorService.execute(new Runnable() {
               @Override
               public void run() {
                   appInfos= getHangzhiyunApp(appsLists);
               }
           });
       }
       return appInfos;
    }

    public List<AppInfo> getHangzhiyunApp(List<String> appsLists) {
        return getSystemApp(appsLists);
    }

    private List<AppInfo> getLingchuangApp(List<String> appsLists) {
     return getSystemApp(appsLists);
    }

    private  List<AppInfo> getSystemApp(List<String> appsLists) {
        List<AppInfo> appInfoList = new ArrayList<>();
        PackageManager pm = MyApplication.getInstance().getPackageManager();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        //去掉领创要不显示的应用
/*        if (!appsLists.isEmpty() && appsLists.size()>0){
            for (int i = 0; i < resolveInfos.size(); i++) {
                ResolveInfo resolveInfo = resolveInfos.get(i);
                String packageName = resolveInfo.activityInfo.packageName;
                for (int j = 0; j <appsLists.size() ; j++) {
                    if (packageName.equals(appsLists.get(j))){
                        resolveInfos.remove(i);
                        --i;
                    }
                }
            }
        }*/


        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo resolveInfo = resolveInfos.get(i);
            String packageName = resolveInfo.activityInfo.packageName;

            //去掉领创的app    //去掉领创的商店 去掉领创的app
            if (packageName.equals(MyApplication.getInstance().getPackageName())
                    || packageName.equals("com.android.launcher3")
                    || packageName.equals("com.android.camera2")
                    || packageName.equals("com.android.music")
                    || packageName.equals("com.android.calculator2")
                    || packageName.equals("com.android.soundrecorder")
                    || packageName.equals("com.android.documentsui")) {
                //是本应用剔除
                continue;
            }

            AppInfo appInfo = new AppInfo();

            if ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                //说明是系统应用
                appInfo.setIsSystemApp(true);
                //使用resolveInfo.activityInfo.applicationInfo.name获取的是null
//                QZXTools.logD("System AppInfo=" + appInfo + ";class name=" + resolveInfo.activityInfo.name);
            } else {
                appInfo.setIsSystemApp(false);
            }
            appInfo.setName(resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            appInfo.setPackageName(resolveInfo.activityInfo.packageName);
            appInfo.setOrderNum(i);
            //使用resolveInfo.activityInfo.applicationInfo.name获取的是null
//            QZXTools.logD("Third AppInfo=" + appInfo + ";class name=" + resolveInfo.activityInfo.name);
            appInfoList.add(appInfo);

            //插入GreenDao数据库
            AppInfoDao appInfoDao = MyApplication.getInstance().getDaoSession().getAppInfoDao();
            appInfoDao.insertOrReplaceInTx(appInfoList);
        }
        Log.i("qin", "getSystemApp: "+appInfoList);

        EventBus.getDefault().post(appInfoList, Constant.LINGCHUANG_APP_LIST);
        return appInfoList;
    }

    public void onStop(){

       if (executorService != null) {
           executorService.shutdown();
           executorService = null;
       }
   }


    public Drawable getAppIconByPackageName(String ApkTempPackageName){
        Drawable drawable;
        try{
            drawable = MyApplication.getInstance().getPackageManager().getApplicationIcon(ApkTempPackageName);
        }
        catch (PackageManager.NameNotFoundException e){
            e.printStackTrace();
            drawable = ContextCompat.getDrawable( MyApplication.getInstance(), R.mipmap.ic_launcher);
        }
        return drawable;
    }

}
