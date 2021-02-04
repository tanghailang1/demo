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

    /**
     *
     * <p>Description: 本地时间转化为UTC时间</p>
     * @param localTime
     * @return
     * @author wgs
     * @date  2018年10月19日 下午2:23:43
     *
     */
    public static String localToUTC(String localTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        Date localDate= null;
        try {
            localDate = sdf.parse(localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long localTimeInMillis=localDate.getTime();
        /** long时间转换成Calendar */
        Calendar calendar= Calendar.getInstance();
        calendar.setTimeInMillis(localTimeInMillis);
        /** 取得时间偏移量 */
        int zoneOffset = calendar.get(java.util.Calendar.ZONE_OFFSET);
        /** 取得夏令时差 */
        int dstOffset = calendar.get(java.util.Calendar.DST_OFFSET);
        /** 从本地时间里扣除这些差量，即可以取得UTC时间*/
        calendar.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        /** 取得的时间就是UTC标准时间 */
        Date utcDate=new Date(calendar.getTimeInMillis());
        return getDateToString(utcDate,"");
    }
}
