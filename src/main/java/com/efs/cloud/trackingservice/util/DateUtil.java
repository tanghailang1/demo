package com.efs.cloud.trackingservice.util;


import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期转换工具类
 * @author maxun
 */
@Slf4j
public class DateUtil {

    /**
     * 日期转字符串
     *
     * @param date
     * @param s
     * @return
     */
    public static String getDateToString(Date date, String s) {
        if (null == date) {
            return null;
        }
        if( s.equals("") ){
            s = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat datetimeFormat = new SimpleDateFormat(s);
        return datetimeFormat.format(date);
    }

    public static Date getStringGMT8Time(Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
            date = simpleDateFormat.parse(simpleDateFormat.format(date));
        }catch (ParseException e){
            log.error("日期转换异常" + e.getMessage());
        }
        return date;
    }

    public static String getSpecifiedDayBefore(String specifiedDay,String format,Integer fate){
        if( format.equals("") ){
            format = "yyyy-MM-dd";
        }
        Calendar c = Calendar.getInstance();
        Date date=null;
        try {
            date = new SimpleDateFormat(format).parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        c.setTime(date);
        int day=c.get(Calendar.DATE);
        c.set(Calendar.DATE,day-fate);

        String dayBefore=new SimpleDateFormat(format).format(c.getTime());
        return dayBefore;
    }
}
