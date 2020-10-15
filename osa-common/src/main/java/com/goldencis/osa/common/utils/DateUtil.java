package com.goldencis.osa.common.utils;

import org.apache.ibatis.annotations.Lang;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Created by limingchao on 2018/10/29.
 */
public class DateUtil {

    public static final String FMT_DATE = "yyyy-MM-dd HH:mm:ss";

    public static final String FMT_DAY = "yyyy-MM-dd";

    /**
     * 将日期字符串转成日期类型
     * @return
     */
    public static Date strToDate(String str, String pattern) {
        try {
            SimpleDateFormat date = new SimpleDateFormat(pattern);
            return date.parse(str);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 某个时间点 加减 N个月
     * @param format
     * @param n
     * @return
     */
    public static String getDateAdd(int n, String dateStr, String format) {

        if (Toolkit.isEmptyStr(format)) {
            format = FMT_DATE;
        }

        SimpleDateFormat df = new SimpleDateFormat(format);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(strToDate(dateStr, format));

        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + n);
        return df.format(calendar.getTime());
    }

    /**
     * 某个时间点 加减 几天
     * @param format
     * @param n
     * @return
     */
    public static String getDateAddDay(int n, String dateStr, String format) {

        if (Toolkit.isEmptyStr(format)) {
            format = FMT_DATE;
        }

        SimpleDateFormat df = new SimpleDateFormat(format);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(strToDate(dateStr, format));

        calendar.add(Calendar.DATE, n);
        return df.format(calendar.getTime());
    }

    /*
     * 获取系统当前日期时间(格式自定)
     *
     * @param format
     *            返回日期的格式 默认为 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getFormatDate(String format) {

        if (Toolkit.isEmptyStr(format)) {
            format = FMT_DATE;
        }
        Date d = new Date();
        DateFormat df = new SimpleDateFormat(format);

        return df.format(d);
    }

    /**
     * 时间戳转日期
     * @param seconds
     * @param format
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

    /**
     * 将时间差，转换为00:00:00
     * @param startDate
     * @param endDate
     * @param flag
     * @return
     */
    public static String formatSecondToTime(Date startDate, Date endDate, String flag) {
        long mSecond1 = endDate.getTime();
        long mSecond2 = startDate.getTime();
        long second = (mSecond1 - mSecond2)/1000;
        long hours = second / 3600;
        String hoursStr = "";
        if (hours == 0) {
            hoursStr = "00";
        } else if (hours > 0 && hours < 10) {
            hoursStr = "0" + hours;
        } else {
            hoursStr = hours + "";
        }
        second = second % 3600;
        long minutes = second /60;
        String minutesStr = "";
        if (minutes == 0) {
            minutesStr = "00";
        } else if (minutes > 0 && minutes < 10) {
            minutesStr = "0" + minutes;
        } else {
            minutesStr = minutes + "";
        }
        second = second % 60;
        String secondStr = "";
        if (second == 0) {
            secondStr = "00";
        } else if (second > 0 && second < 10) {
            secondStr = "0" + second;
        } else {
            secondStr = second + "";
        }
        if (flag == null || flag.equals("0")){
            return hoursStr + ":" + minutesStr + ":" + secondStr;
        } else {
            return (hours*60 + minutes) +"分" + second + "秒";
        }

    }

    /**
     * 时间类型转换
     * @param localDateTime
     * @return
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime){
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        Date date = Date.from(zdt.toInstant());
        return date;
    }

    /**
     * 判断当前日期是星期几
     * @param pTime 修要判断的时间 "2018-11-11 15:12:33"
     * @return dayForWeek 判断结果  周一 1;... 周日 7;
     */
    public static Integer dayForWeek(String pTime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("时间格式不正确");
        }
        int dayForWeek = 0;
        if(c.get(Calendar.DAY_OF_WEEK) == 1){
            dayForWeek = 7;
        }else{
            dayForWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        }
        return dayForWeek;
    }

    public static String formatSecondInPattern(long seconds) {
        if (0 > seconds) {
            return "00:00";
        } else if (seconds < 60) {
            return "00:" + (seconds<10 ? "0"+seconds : seconds);
        } else if (seconds < 3600) {
            return (seconds / 60 < 10 ? "0" + seconds / 60 : seconds / 60) + ":" + (seconds % 60 < 10 ? "0" + seconds % 60 : seconds % 60);
        } else {
            long minSec = seconds % 3600;
            return (seconds / 3600 < 10 ? "0" + seconds / 3600 : seconds / 3600) + ":" + (minSec / 60 < 10 ? "0" + minSec / 60 : minSec / 60) + ":" + (minSec % 60 < 10 ? "0" + minSec % 60 : minSec % 60);
        }
    }

    public static String getDateAddMinute(int n, String date, String format) {

        if (Toolkit.isEmptyStr(format)) {
            format = FMT_DATE;
        }

        Date date1 = strToDate(date, FMT_DATE);

        SimpleDateFormat df = new SimpleDateFormat(format);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);

        calendar.add(Calendar.MINUTE, n);
        return df.format(calendar.getTime());
    }

    public static String format(String date, String pattern) {
        Date date1 = strToDate(date, FMT_DATE);
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        return sf.format(date1);
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     * @param str1 时间参数 1 格式：1990-01-01 12:00:00
     * @param str2 时间参数 2 格式：2009-01-01 12:00:00
     * @return long[] 返回值为：{天, 时, 分, 秒}
     */
    public static long[] getDistanceTimes(String str1, String str2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(str1);
            two = df.parse(str2);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff ;
            if(time1<time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = (diff/1000-day*24*60*60-hour*60*60-min*60);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long[] times = {day, hour, min, sec};
        return times;
    }

    /**
     * String日期转换 LocalTime
     * @param strTime 时间 05:43
     * @return
     */
    public static LocalTime strTime2LocalTime(String strTime){
        LocalTime localTime = null;
        if (!StringUtils.isEmpty(strTime)){
            try {
                localTime =  LocalTime.parse(strTime);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return localTime;
    }

    /**
     * String日期时间转换 LocalTime
     * @param strDateTime  2017-09-28 17:07:05
     * @return
     */
    public static LocalTime strDateTime2LocalTime(String strDateTime){
        LocalTime localTime = null;
        if (!StringUtils.isEmpty(strDateTime)){
            SimpleDateFormat df =new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date date = df.parse(strDateTime);
                System.out.println("时间："+ date);
                Instant instant = date.toInstant();
                ZoneId zone = ZoneId.systemDefault();
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
                localTime = localDateTime.toLocalTime();
                System.out.println(localTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return localTime;
    }

    /**
     * 获取过去第几天的日期
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String result = format.format(today);
        System.out.println(result);
        return result;
    }

    /**
     * 获取过去或者未来 任意天内的日期数组
     * @param intervals      intervals天内
     * @return              日期数组
     */
    public static ArrayList<String> getPastDates(int intervals ) {
        ArrayList<String> pastDaysList = new ArrayList<>();
        for (int i = 0; i <intervals; i++) {
            pastDaysList.add(getPastDate(i));
        }
        return pastDaysList;
    }

    public static String getNowDate() {
        Date date = new Date();
        SimpleDateFormat fm = new SimpleDateFormat(FMT_DAY, Locale.US);
        fm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        return fm.format(date);
    }
    /**
     * 计算两个日期之间相差的天数
     *
     * @param startDate 较小的时间
     * @param endDate   较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(String startDate, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(startDate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(endDate));
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);
        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * LocalDateTime 转 时间戳
     */
    public static java.lang.Long getTimestampByLocalDateTime(LocalDateTime  localDateTime){
        if (Objects.isNull(localDateTime)){
            return null;
        }
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * LocalDateTime 转 String
     */
    public static java.lang.String getStringByLocalDateTime(LocalDateTime  localDateTime){
        if (Objects.isNull(localDateTime)){
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try{
            return df.format(localDateTime);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
    }

    public static void main(String[] args) {
        long[] distanceTimes = getDistanceTimes("2018-11-26 09:13:10", "2018-11-27 10:10:10");
        System.out.println(distanceTimes);
//        getPastDates(7);

    }





}
