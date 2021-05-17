package com.telit.zhkt_three.Utils;

import android.content.Context;
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
                    || packageName.equals("com.huawei.systemmanager")
                    || packageName.equals("com.android.soundrecorder")
                    || packageName.equals("com.SSI.UnityAndroid")
                    //华为要隐藏的pad头像
                    || packageName.equals("com.huawei.browser")
                    || packageName.equals("com.huawei.calendar")
                    || packageName.equals("com.huawei.music")
                    || packageName.equals("com.huawei.notepad")
                    || packageName.equals("com.huawei.himovie")
                    || packageName.equals("com.huawei.calculator")
                    || packageName.equals("com.huawei.camera")
                    || packageName.equals("com.huawei.photos")
                    || packageName.equals("com.huawei.HwMultiScreenShot")
                    || packageName.equals("com.huawei.android.hwouc")
                    || packageName.equals("com.huawei.android.totemweather")
                    || packageName.equals("com.huawei.compass")
                    || packageName.equals("com.huawei.gameassistant")
                    || packageName.equals("com.huawei.mycenter")
                    || packageName.equals("com.huawei.soundrecorder")
                    || packageName.equals("com.huawei.stylus.mpenzone")
                    || packageName.equals("com.myscript.nebo.huawei")
                    || packageName.equals("com.huawei.contacts")
                    || packageName.equals("com.huawei.deskclock")
                    || packageName.equals("com.ndwill.swd.appstore")){
                //是本应用剔除
                continue;
            }

            QZXTools.logE("获取系统app包名"+packageName+"获取app名字"+ resolveInfo.activityInfo.name,null);
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
        }

        //添加文件或资源管理器
//        addFileManager(appInfoList,appInfoList.size());

        //插入GreenDao数据库
        AppInfoDao appInfoDao = MyApplication.getInstance().getDaoSession().getAppInfoDao();
        appInfoDao.insertOrReplaceInTx(appInfoList);


        EventBus.getDefault().post(appInfoList, Constant.LINGCHUANG_APP_LIST);
        return appInfoList;
    }

    /**
     * 添加文件或资源管理器
     *
     * @param appInfoList
     * @param i
     */
    private void addFileManager(List<AppInfo> appInfoList,int i){
        boolean hasFileM = false;

        if (checkApplication(MyApplication.getInstance(),"com.android.rk")&&!appHasAdd("com.android.rk",appInfoList)){
            AppInfo appInfo = new AppInfo();
            appInfo.setIsSystemApp(true);
            appInfo.setName("资源管理器");
            appInfo.setPackageName("com.android.rk");
            appInfo.setOrderNum(i);
            appInfoList.add(appInfo);
            hasFileM = true;
            QZXTools.logE("资源管理器",null);
        }

        if (checkApplication(MyApplication.getInstance(),"com.android.documentsui")&&!appHasAdd("com.android.documentsui",appInfoList)){
            AppInfo appInfo = new AppInfo();
            appInfo.setIsSystemApp(true);
            appInfo.setName("文件");
            appInfo.setPackageName("com.android.documentsui");

            if (hasFileM){
                appInfo.setOrderNum(i+1);
            }else {
                appInfo.setOrderNum(i);
            }

            appInfoList.add(appInfo);

            QZXTools.logE("存在文件",null);
        }
    }

    /**
     * 是否已经添加
     *
     * @param packageName
     * @param appInfoList
     * @return
     */
    private boolean appHasAdd(String packageName,List<AppInfo> appInfoList){
        for (AppInfo appInfo:appInfoList){
            if (packageName.equals(appInfo.getPackageName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据报名判断应用是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean checkApplication(Context context,String packageName) {
        if (packageName == null || "".equals(packageName)){
            return false;
        }
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
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
