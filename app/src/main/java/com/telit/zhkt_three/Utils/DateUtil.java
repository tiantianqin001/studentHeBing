package com.telit.zhkt_three.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * 根据格式获取当前格式化时间
     * @param format 格式化方式，基础格式为yyyy-MM-dd HH:mm:ss
     * @return 当前时间
     */
    public static String getCurrentTimeByFormat(String format)
    {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date());
    }

    /**
     * 格式化时间
     * @param format 格式化格式，基础格式为yyyy-MM-dd HH:mm:ss
     * @param
     * @return
     */
    public static String formatTime(String format, long time)
    {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(new Date(time));
    }
}
