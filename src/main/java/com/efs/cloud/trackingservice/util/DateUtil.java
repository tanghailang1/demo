package com.efs.cloud.trackingservice.util;


import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 日期转换工具类
 * @author maxun
 */
@Slf4j
public class DateUtil {

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
}
