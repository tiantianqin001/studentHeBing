package com.telit.zhkt_three.Service;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.greendao.AppInfoDao;


/**
 * author: qzx
 * Date: 2019/4/2 9:34
 */
public class AppInfoService extends JobIntentService {

    /**
     * Unique job ID for this service.
     */
    static final int JOB_ID = 1000;

    /**
     * Convenience method for enqueuing work in to this service.
     */
    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, AppInfoService.class, JOB_ID, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        QZXTools.logE("AppInfoService onHandleWork", null);
        //这个是子线程中执行的，可以操作耗时处理
        int appInfoType = intent.getIntExtra("app_type", -1);
        String packageName = intent.getStringExtra("package_name");
        switch (appInfoType) {
            case Constant.APP_NEW_ADD:
                AppInfoDao appInfoDao_add = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                int num = (int) appInfoDao_add.queryBuilder().buildCount().count();
                QZXTools.logE("num=" + num, null);
                //依据包名获取相应的信息
                AppInfo newAppInfo = getAppInfoFromPackageName(num, packageName);
                appInfoDao_add.insertOrReplace(newAppInfo);

                EventBus.getDefault().post(Constant.APP_NEW_ADD, Constant.EVENT_TAG_APP);
                break;
            case Constant.APP_UPDATE:
                AppInfoDao appInfoDao_update = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                //依据包名获取相应的信息
                if (packageName.equals(getPackageName())) {
                    return;
                }
                AppInfo update_info = appInfoDao_update.queryBuilder().where(AppInfoDao.Properties.
                        PackageName.eq(packageName)).unique();
                AppInfo updateAppInfo = getAppInfoFromPackageName(update_info.getOrderNum(), packageName);
                appInfoDao_update.insertOrReplace(updateAppInfo);

                EventBus.getDefault().post(Constant.APP_UPDATE, Constant.EVENT_TAG_APP);
                break;
            case Constant.APP_DELETE:
                AppInfoDao appInfoDao_del = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                //直接依据主键删除
                appInfoDao_del.deleteByKey(packageName);

                EventBus.getDefault().post(Constant.APP_DELETE, Constant.EVENT_TAG_APP);
                break;
            default:
                break;
        }
    }

    private AppInfo getAppInfoFromPackageName(int orderNum, String packageName) {
        AppInfo appInfo = new AppInfo();
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
                appInfo.setIsSystemApp(true);
            } else {
                appInfo.setIsSystemApp(false);
            }
            appInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
            appInfo.setPackageName(packageName);
            appInfo.setOrderNum(orderNum);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return appInfo;
    }
}
