package com.telit.zhkt_three.Utils;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/2/26 16:41
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class ViewUtils {
    // 两次点击按钮之间的点击间隔不能少于1000毫秒
    private static long lastClickTime;

    public static boolean isFastClick(int delayTime) {
        boolean flag = false;
        long curClickTime = System.currentTimeMillis();
        if ((curClickTime - lastClickTime) >= delayTime) {
            flag = true;
        }
        lastClickTime = curClickTime;
        return flag;
    }

    /**
     * 检测是否有emoji表情
     *
     * @param source
     * @return
     */
    public static boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情
                return true;
            }
        }
        return false;
    }

    /**
     * 判断是否是Emoji
     *
     * @param codePoint 比较的单个字符
     * @return
     */
    private static boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)
                && (codePoint <= 0x10FFFF));
    }
}
