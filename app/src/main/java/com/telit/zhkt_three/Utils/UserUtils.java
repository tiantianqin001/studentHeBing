package com.telit.zhkt_three.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/5/13 16:11
 */
public class UserUtils {

    public static void setStringTypeSpInfo(SharedPreferences sharedPreferences, String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static void setBooleanTypeSpInfo(SharedPreferences sharedPreferences, String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static void setOauthId(SharedPreferences sharedPreferences, String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static void setTgt(SharedPreferences sharedPreferences, String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    /**
     * 获取登录的认证模式：一般登录 or 省平台登录
     */
    public static boolean getOauthMode() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("IsOauthMode", false);
    }

    public static String getStudentId() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("studentId", "");
    }
    public static String getShoolId(){
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("schoolId", "");
    }

    public static String getLoginName() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("loginName", "");
    }

    public static String getUserId() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("userId", "");
    }

    public static String getClassId() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("classId", "");
    }

    public static String getClassName() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("className", "");
    }

    public static String getStudentName() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("studentName", "");
    }

    public static String getTgt() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("tgtLogin", Context.MODE_PRIVATE);
        return sharedPreferences.getString("tgt", "");
    }

    public static void removeTgt() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("tgtLogin", Context.MODE_PRIVATE);
        sharedPreferences.edit().remove("tgt").commit();
    }

    public static String getAvatarUrl() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("avatarUrl", "");
    }

    public static String getShortClassId() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("short_classId", "");
    }

    public static boolean getAccessMode() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().getApplicationContext()
                .getSharedPreferences("access_mode", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("had_access", false);
    }

    public static String getToken() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getString("token", "");
    }


    /**
     * 课后作业的图片设置
     */
    public static int getSubjectIcon(int subjectId) {
        switch (subjectId) {
            case 0:
                return R.mipmap.chinese;
            case 1:
                return R.mipmap.math;
            case 2:
                return R.mipmap.english;
            default:
                return R.mipmap.icon_homework_unknow;
        }
    }

    /**
     * 是否已经成功登录
     */
    public static boolean isLoginIn() {
        SharedPreferences sharedPreferences = MyApplication.getInstance().
                getApplicationContext().getSharedPreferences("student_info", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("isLoginIn", false);
    }
}
