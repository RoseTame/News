package com.newsultimate.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtil {

    /**
     * 将时间转换为“几分钟前”、“几小时前”、“昨天”、“几天前”、“几个月前”或“几年前”的格式。
     *
     * @param timeStr 时间字符串
     * @param format 时间字符串的格式，例如 "MM/dd/yyyy HH:mm:ss"
     * @return 返回表示时间差的字符串
     */
    public static String format(String timeStr, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai")); // 设置时区为东八区

        try {
            Date givenDate = sdf.parse(timeStr);
            long now = System.currentTimeMillis();
            sdf.setTimeZone(TimeZone.getDefault()); // 设置当前系统默认时区用于获取当前时间
            long then = givenDate.getTime();
            long diffInMillis = now - then;

            long diffInSeconds = diffInMillis / 1000;
            long diffInMinutes = diffInSeconds / 60;
            long diffInHours = diffInMinutes / 60;
            long diffInDays = diffInHours / 24;

            if (diffInMinutes < 60) {
                return diffInMinutes + "分钟前";
            } else if (diffInHours < 24) {
                return diffInHours + "小时前";
            } else if (diffInDays == 1) {
                return "昨天";
            } else if (diffInDays < 30) {
                return diffInDays + "天前";
            } else {
                long diffInMonths = diffInDays / 30; // 近似值
                long diffInYears = diffInDays / 365; // 近似值

                if (diffInMonths < 12) {
                    return diffInMonths + "个月前";
                } else if (diffInYears < 2) {
                    return "1年前";
                } else {
                    return diffInYears + "年前";
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return "未知时间";
        }
    }
}