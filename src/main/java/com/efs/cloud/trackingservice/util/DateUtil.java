package com.efs.cloud.trackingservice.util;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期转换工具类
 * @author maxun
 */
public class DateUtil {

    public static Date getStringGMT8Time(Date date) throws ParseException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return simpleDateFormat.parse(simpleDateFormat.format(date));
    }
}
