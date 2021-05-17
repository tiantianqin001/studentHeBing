package com.telit.zhkt_three.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;

/**
 * ***************************************************************************
 * author: Administrator
 * time: 2016/9/17 17:11
 * name:
 * overview:
 * usage:版本信息工具
 * ***************************************************************************
 */
public class VersionUtils {
    //获取版本号
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
    }

    //获取版本名
    public static String getVersionName(Context context) {
        String versionName="3.0.0";
        try {
            PackageInfo pi = context.getApplicationContext().getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName =  pi.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 版本号比较
     *
     * @param version1
     * @param version2
     * @return
     */
    public static int compareVersion(String version1, String version2) {
        if (version1.equals(version2)) {
            return 0;
        }
        String[] version1Array = version1.split("\\.");
        String[] version2Array = version2.split("\\.");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        int diff = 0;
        while (index < minLen && (diff = Integer.parseInt(version1Array[index]) - Integer.parseInt(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Integer.parseInt(version1Array[i]) > 0) {
                    return 1;
                }
            }
            for (int i = index; i < version2Array.length; i++) {
                if (Integer.parseInt(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

}
