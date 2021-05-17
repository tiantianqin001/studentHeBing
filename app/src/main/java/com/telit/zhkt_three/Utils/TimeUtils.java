package com.telit.zhkt_three.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/1/15 16:44
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class TimeUtils {
    //格式化时间戳
    public static String timeStamp(long seconds, String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    //格式化时间戳
    public static String timeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return sdf.format(new Date(Long.valueOf(System.currentTimeMillis())));
    }

    public static String timeStamp_time() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(Long.valueOf(System.currentTimeMillis())));
    }

    /*
     * 将时间转换为时间戳
     */
    public static long timeToStamp(String s, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        return ts;
    }
    /**
     * 判断两次时间差
     */
    public static boolean checkTowTime(long startTime,long endTime){
        if (endTime-startTime>3000){
            return true;
        }
        return false;
    }
}
