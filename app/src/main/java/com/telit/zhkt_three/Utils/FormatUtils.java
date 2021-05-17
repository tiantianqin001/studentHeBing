package com.telit.zhkt_three.Utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Formatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2017/10/10 14:18
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */

public class FormatUtils {
    /**
     * 验证手机格式
     */
    public static boolean isMobile(String number) {
        /**
         * 运营商号段如下：
         * 中国联通号码：130、131、132、145（无线上网卡）、155、156、185（iPhone5上市后开放）、186、176（4G号段）、
         *               175（2015年9月10日正式启用，暂只对北京、上海和广东投放办理）
         * 中国移动号码：134、135、136、137、138、139、147（无线上网卡）、150、151、152、157、158、159、182、183、187、188、178
         * 中国电信号码：133、153、180、181、189、177、173、149 虚拟运营商：170、1718、1719
         * 手机号前3位的数字包括：
         * 1 :1
         * 2 :3,4,5,7,8
         * 3 :0,1,2,3,4,5,6,7,8,9
         * 总结： 目前java手机号码正则表达式有：
         * a :"^1[3|4|5|7|8][0-9]\\d{4,8}$"    一般验证情况下这个就可以了
         * b :"^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(17[013678])|(18[0,5-9]))\\d{8}$"
         * Pattern和Matcher详解（字符串匹配和字节码）http://blog.csdn.net/u010700335/article/details/44616451
         */
        String num = "^1[3|4|5|7|8|9][0-9]\\d{4,8}$";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    public static String formatMobile(String number) {
        return number.substring(0, 3) + " " + number.substring(3, 7) + " " + number.substring(7, 11);
    }

    /**
     * 获取完整11位手机号
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            if (m.find()) {
                dest = m.replaceAll("");
            }
        }
        return dest;
    }

    /**
     * 验证邮箱格式
     */
    public static boolean isEmail(String number) {
        String num = "^[A-Za-z0-9\\u4e00-\\u9fa5]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
        if (TextUtils.isEmpty(number)) {
            return false;
        } else {
            //matches():字符串是否在给定的正则表达式匹配
            return number.matches(num);
        }
    }

    /**
     * 判断是否是整数
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 格式化钱
     *
     * @param num
     * @return
     */
    public static String getFormatMoney(float num) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String moneys = decimalFormat.format(num);
        return moneys;
    }

    /**
     * 格式化钱
     *
     * @param num
     * @return
     */
    public static String getFormatMoney(double num) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String moneys = decimalFormat.format(num);
        return moneys;
    }

    /**
     * 格式化钱
     *
     * @param num
     * @return
     */
    public static String getFormatMoney(String num) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.00");
        String moneys = decimalFormat.format(Double.valueOf(num));
        return moneys;
    }

    /**
     * 格式化距离
     *
     * @param num
     * @return
     */
    public static String getFormatDistance(double num) {
        DecimalFormat decimalFormat = new DecimalFormat("##.00");
        String distance = decimalFormat.format(num);
        return distance;
    }

    /**
     * 格式化分数
     *
     * @param d
     * @return
     */
    public static String getFormatScore(double d) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        // 保留两位小数
        nf.setMaximumFractionDigits(2);
        // 如果不需要四舍五入，可以使用RoundingMode.DOWN
        nf.setRoundingMode(RoundingMode.UP);
        return nf.format(d);
    }

    public static float getTwoValidNumber(float number) {
        BigDecimal b = new BigDecimal(number);
        float num = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();  //表明四舍五入，保留两位小数
        return num;
    }

    public static float getThreeValidNumber(float number) {
        BigDecimal b = new BigDecimal(number);
        float num = b.setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();  //表明四舍五入，保留两位小数
        return num;
    }

    public static String getTwoValidNumber(String number) {
        if (TextUtils.isEmpty(number)) {
            return "";
        } else {
            float n = Float.valueOf(number);
            BigDecimal b = new BigDecimal(n);
            float num = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();  //表明四舍五入，保留两位小数
            return num + "";
        }
    }

    public static String getTwoValidNumber2(float f) {
        if (f == 0) {
            return "0";
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            return df.format(f);
        }
    }

    public static String getOneValidNumber(float f) {
        if (f == 0) {
            return "0";
        } else {
            DecimalFormat df = new DecimalFormat("#.0");
            return df.format(f);
        }
    }

    public static boolean isIP(String ip) {
        if (!TextUtils.isEmpty(ip)) {
            // 定义正则表达式
            String regex = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\." +
                    "(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
            // 判断ip地址是否与正则表达式匹配
            if (ip.matches(regex)) {
                // 返回判断信息
                return true;
            } else {
                // 返回判断信息
                return false;
            }
        }
        return false;
    }

    public static String formatResultTwoValid(double value) {
        /*
         * %.2f % 表示 小数点前任意位数 2 表示两位小数 格式后的结果为 f 表示浮点型
         */
        return new Formatter().format("%.2f", value).toString();
    }

    public static String formatResultTwoValid(String value) {
        if (TextUtils.isEmpty(value)) {
            return "";
        } else {
            double v = Double.valueOf(value);
            /*
             * %.2f % 表示 小数点前任意位数 2 表示两位小数 格式后的结果为 f 表示浮点型
             */
            return new Formatter().format("%.2f", v).toString();
        }
    }

    /**
     * @param number
     * @return
     */
    public static String getValidToPercent(float number) {
        NumberFormat nf = NumberFormat.getPercentInstance();
        nf.setMaximumFractionDigits(1);
        return nf.format(number);
    }

    /**
     * 判断奇偶
     *
     * @param a
     * @return
     */
    public static int oddOrEven(int a) {
        if (a % 2 != 0) {
            return 1;
        } else {
            return 2;
        }
    }

    /**
     * 格式化时间展示为05’10”
     */
    public static String formatRecordTime(long recTime, long maxRecordTime) {
        int time = (int) ((maxRecordTime - recTime) / 1000);
        int minute = time / 60;
        int second = time % 60;
        return String.format("%02d:%02d", minute, second);
    }

    /**
     * 格式化时间展示为05’10”
     */
    public static String formatTime(long recTime) {
        long time = recTime/1000;
        int minute = (int) time / 60;
        int second = (int) time % 60;
        return String.format("%02d:%02d", minute, second);
    }

    public static String formatTime(int recTime) {
        int minute = recTime / 60;
        int second = recTime % 60;
        if (minute == 0) {
            return String.format("%02d”", second);
        } else {
            return String.format("%02d%02d”", minute, second);
        }
    }

    public static String getHM(int recTime) {
        int hour = recTime / 60;
        int minute = recTime % 60;
        if (hour == 0) {
            if (minute == 0) {
                return "0m";
            } else {
                return minute + "m";
            }
        } else {
            if (minute == 0) {
                return hour + "h";
            } else {
                return hour + "h" + minute + "m";
            }
        }
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
