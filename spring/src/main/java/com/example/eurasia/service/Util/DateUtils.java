package com.example.eurasia.service.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    //日期格式
    public static final String PRODUCT_DATE_FORMAT_1 = "yyyy/MM/dd";
    public static final String PRODUCT_DATE_FORMAT_2 = "yyyy-MM-dd";
    public static final String PRODUCT_DATE_FORMAT_3 = "yyyyMMdd";
    public static final String PRODUCT_DATE_FORMAT_4 = "yyyy/MM";
    public static final String PRODUCT_DATE_FORMAT_5 = "yyyy-MM-dd-HH-mm-ss";
    public static final String PRODUCT_DATE_FORMAT_6 = "yyyy_MM_dd";

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_1 = new SimpleDateFormat(DateUtils.PRODUCT_DATE_FORMAT_1);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_2 = new SimpleDateFormat(DateUtils.PRODUCT_DATE_FORMAT_2);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_3 = new SimpleDateFormat(DateUtils.PRODUCT_DATE_FORMAT_3);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_4 = new SimpleDateFormat(DateUtils.PRODUCT_DATE_FORMAT_4);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_5 = new SimpleDateFormat(DateUtils.PRODUCT_DATE_FORMAT_5);
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_6 = new SimpleDateFormat(DateUtils.PRODUCT_DATE_FORMAT_6);

    /**
     * 获取今天
     * @return String
     * */
    public static String getToday(String dateFormat){
        return new SimpleDateFormat(dateFormat).format(new Date());
    }
    /**
     * 获取昨天
     * @return String
     * */
    public static String getYesterday(String dateFormat){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.DATE,-1);
        Date time=cal.getTime();
        return new SimpleDateFormat(dateFormat).format(time);
    }
    /**
     * 获取本月开始日期
     * @return String
     * **/
    public static String getMonthStart(String dateFormat){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time=cal.getTime();
        return new SimpleDateFormat(dateFormat).format(time);
    }
    /**
     * 获取本月最后一天
     * @return String
     * **/
    public static String getMonthEnd(String dateFormat){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date time=cal.getTime();
        return new SimpleDateFormat(dateFormat).format(time);
    }
    /**
     * 获取本周的第一天
     * @return String
     * **/
    public static String getWeekStart(String dateFormat){
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date time=cal.getTime();
        return new SimpleDateFormat(dateFormat).format(time);
    }
    /**
     * 获取本周的最后一天
     * @return String
     * **/
    public static String getWeekEnd(String dateFormat){
        Calendar cal=Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time=cal.getTime();
        return new SimpleDateFormat(dateFormat).format(time);
    }
    /**
     * 获取本年的第一天(yyyy-MM-dd)
     * @return String
     * **/
    public static String getYearStart(){
        return new SimpleDateFormat("yyyy").format(new Date())+"-01-01";
    }
    /**
     * 获取本年的最后一天
     * @return String
     * **/
    public static String getYearEnd(String dateFormat){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH,calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH,calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date currYearLast = calendar.getTime();
        return new SimpleDateFormat(dateFormat).format(currYearLast);
    }

}
