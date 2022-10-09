package com.example.demo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CommonUtil {

    /**
     * 不做任何操作，空方法
     *
     * @param str
     * @return
     */
    public static String deAccent(String str) {
        return str;
    }

    /**
     * 不做任何操作，空方法
     *
     * @param str
     * @return
     */
    public static String deAccent2(String str) {
        return str;
    }

    /**
     * 将时间戳转换为时间
     *
     * @param s
     * @return
     */
    public static String stampToDate(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * 将时间转换为时间戳
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /**
     * 字符串转日期
     *
     * @param s
     * @return
     * @throws ParseException
     */
    public static Date StringToDate(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = sdf.parse(s);
        return date;
    }

    /**
     * 格式化日期到字符串
     *
     * @param date
     * @param s
     * @return
     * @throws ParseException
     */
    public static String getDate(Date date, String s) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat(s);//设置日期格式
        String format = df.format(date);// new Date()为获取当前系统时间
        return format;
    }

    /**
     * 查询Woe属性生成的key
     *
     * @param variable
     * @param v
     * @return
     */
    public static String generateWoeServiceKey(String variable, String v) {
        return variable + "-" + v;
    }

    public static String getString(Object o){
        if(o==null){
            return null;
        }
        return o.toString();
    }

    public static String getTrim(String s){
        if(s != null){
            return s.trim();
        }
        return null;
    }

    /**
     * 获取指原字符串指定位置的字符串
     * @param start 开始下标，含开始
     * @param end 结束下标，不含结束
     * @param source 源字符串
     * @return
     */
    public static String getSubString(int start, int end, String source){
        if(source!=null){
            int len = source.length();
            if(end < len){
                return source.substring(start,end);
            }else if(end ==len){
                return source.substring(start);
            }else{
                return null;
            }
        }
        return null;
    }

    /**
     * 获取指原字符串指定位置的字符串,如果是null，返回默认值
     * @param start
     * @param end
     * @param source
     * @return
     */
    public static String getSubStringDefault(int start, int end, String source){
        String sub = getSubString(start, end, source);
        return sub==null?"-999999":sub;
    }

    public static String getLower(String s){
        if(s != null){
            return s.toLowerCase();
        }
        return null;
    }

}
