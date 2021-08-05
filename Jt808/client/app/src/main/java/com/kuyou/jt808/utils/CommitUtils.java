package com.kuyou.jt808.utils;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * action :
 * <p>
 * author: wuguoxian <br/>
 * date: 20-10-22 <br/>
 * <p>
 */
public class CommitUtils {

    public static String formatLocalTimeByMilSecond(String pattern) {
        String date = formatLocalTimeByMilSecond(pattern,System.currentTimeMillis());
        Log.d("123456"," formatLocalTimeByMilSecond > date = "+date);
        return date;
    }

    public static String formatLocalTimeByMilSecond(String pattern,long timeMillis) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08"));
        Date curDate = new Date(timeMillis);
        String date = formatter.format(curDate);
        Log.d("123456"," formatLocalTimeByMilSecond > date = "+date);
        return date;
    }
}
