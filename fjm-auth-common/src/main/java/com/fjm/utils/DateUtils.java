package com.fjm.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 *
 */
@Slf4j
public abstract class DateUtils {

    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MILLISCEND_FORMATE = "yyyy-MM-dd HH:mm:ss SSS";
    public static final String DATE_TIME_NO_SECOND_FORMAT = "yyyyMMdd";
    public static final String DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_HH_MI_SS = "HH:mm:ss";

    /**
     * 格式: yyyy-MM-dd HH:mm:ss.SSS.
     **/
    public static final String PATTERN_FULL = "yyyy-MM-dd HH:mm:ss.SSS";
    /**
     * 格式: yyyy-MM-dd
     **/
    public static final String PATTERN_DAY = "yyyy-MM-dd";
    /**
     * 格式:HH:mm:ss.SSS.
     **/
    public static final String PATTERN_TIME = "HH:mm:ss.SSS";

    /**
     * 自学习功能-每分钟60000毫秒
     */
    public static final long ONE_MINUTE = 60 * 1000;

    /**
     * 自学习功能-每5分钟分钟个桶
     */
    public static final int BASIC_BUCKET = 5;

    /**
     * 时间后缀
     */
    public static final String TIME_SUFFIX = " 00:00:00";


    /**
     * Private constructor
     */
    private DateUtils() {
    }

    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    public static String toDateTime(LocalDateTime date) {
        return toDateTime(date, DEFAULT_DATE_TIME_FORMAT);
    }

    public static String toDateTime(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE));
    }

    public static String timeStamp2Date(String timeStamp, String format) {
        if (timeStamp == null || timeStamp.isEmpty() || timeStamp.equals("null")) {
            return "";
        }
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(timeStamp)));
    }

    public static String toDateText(LocalDate date, String pattern) {
        if (date == null || pattern == null) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.SIMPLIFIED_CHINESE));
    }

    public static Date getDayStartTime() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat formater2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            return formater2.parse(formater.format(new Date()) + " 00:00:00");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Date getDayEndTime() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat formater2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            return formater2.parse(formater.format(new Date()) + " 23:59:59");
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }

    public static Date getNextDays(Date date, int nextDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, +nextDay);//+n今天的时间加n天
        date = calendar.getTime();
        return date;
    }

    public static Date toDateWithT(String str) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = sdf.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String toStringByDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_TIME_FORMAT);
        try {
            return sdf.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 格式化String时间
     *
     * @param time       String类型时间
     * @param timeFormat String类型格式
     * @return 格式化后的Date日期
     * @throws ParseException
     */
    public static Date parseStr2Date(String time, String timeFormat) throws ParseException {
        if (StringUtils.isBlank(time)) {
            return null;
        }
        return new SimpleDateFormat(timeFormat).parse(time);

    }

    /**
     * 指定格式当前时间字符串
     *
     * @return 指定格式当前时间字符串
     */
    public static String currentDate(String timeFormat) {
        if (StringUtils.isNotBlank(timeFormat)) {
            return new SimpleDateFormat(timeFormat).format(new Date());
        } else {
            return null;
        }
    }

    /**
     * 获取当前时间
     *
     * @return
     * @Author hw
     * @Description //TODO  获取当前时间修改 返回格式：yyyy-MM-dd HH:mm:ss.SSS
     * @Date 2020/4/20 0020
     * @Param
     **/
    public static String getLocalDateTime() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(PATTERN_FULL);
        return df.format(LocalDateTime.now());
    }

    /**
     * localDateTime转化为String
     *
     * @param time
     * @param PATTERN_DATE
     * @return
     */
    public static String parseLocalDateTimeToString(LocalDateTime time, String PATTERN_DATE) {
        if (time == null) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(PATTERN_DATE);
        return df.format(time);
    }

    /**
     * String转localDateTime
     *
     * @param timeStr
     * @param PATTERN_DATE
     * @return
     */
    public static LocalDateTime parseTimeStringToLocalDateTime(String timeStr, String PATTERN_DATE) {
        if (timeStr == null) {
            return null;
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern(PATTERN_DATE);
        return LocalDateTime.parse(timeStr, df);
    }

    /**
     * 返回时间的时间戳(毫秒) UTC-8
     *
     * @param time
     * @return
     */
    public static long parseLocalDateTimeToUTC8MillSecond(LocalDateTime time) {
        if (time == null) {
            return 0;
        }
        return time.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    public static LocalDateTime pareseDateTimeOfTimestamp(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        ZoneId zone = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zone);
    }


    /**
     * 当前时间字符串（yyyy-MM-dd HH:mm:ss）
     *
     * @return 当前时间字符串
     */
    public static String currentDate() {
        return new SimpleDateFormat(DATE_TIME_FORMAT_YYYY_MM_DD_HH_MI_SS).format(new Date());
    }

    /**
     * 时间格式化
     *
     * @param date   date
     * @param format format
     * @return 时间字符串
     */
    public static String format(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 验证数组中的值是否是合法的星期值，合法的值为【1-7】
     *
     * @param weekList [1,2,3,4,5,6,7]
     * @return
     */
    public static boolean isValidWeek(List<Integer> weekList) {
        for (Integer i : weekList) {
            if (i < 1 || i > 7) {
                return false;
            }
        }
        return true;
    }


    /**
     * 根据给定时间获取时分秒加起来的秒数总和
     *
     * @param date
     * @return
     */
    public static Long getSecondNumByDate(Date date) {
        DateFormat df = new SimpleDateFormat("HH:mm:ss");
        String[] dateArr = df.format(date).split(":");
        Long second = Long.valueOf(dateArr[0]) * 60 * 60 + Long.valueOf(dateArr[1]) * 60 + Long.valueOf(dateArr[2]);

        return second;
    }

    /**
     * 根据给定时间以及秒数 获取之前或之后的时间
     *
     * @param date
     * @return
     */
    public static Date getSecondDateBeforeOrAfter(Date date, int sceond) {
        Calendar c = new GregorianCalendar();
        Date now = new Date();
        Date startFixedTime = null;
        Date endFixedTime = null;
        c.setTime(now);
        //把日期往后增加SECOND 秒.整数往后推,负数往前移动
        c.add(Calendar.SECOND, sceond);
        return c.getTime();
    }

    /**
     * 获取当前时间的星期数
     *
     * @param
     * @return
     */
    public static int getWeek() {
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
    }


    /**
     * 检查是否是合法的24小时时间值
     *
     * @param time
     * @return
     */
    public static boolean isValidTime(String time) {
        try {
            return parseStr2Date(time, DATE_TIME_FORMAT_HH_MI_SS) != null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Long dateToStamp(String dateStr, String formate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formate);
        try {
            Date date = simpleDateFormat.parse(dateStr);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }

    /*
     * @Author hw
     * @Description // 获取指定日期 零点 的时间戳
     * @Date 2020/6/10 0010
     * @Param [date]
     * @return java.lang.Long
     **/
    public static long dayTimeInMillis(Calendar calendar) {
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long time = calendar.getTimeInMillis();
        return time;
    }

    /*
     * @Author hw
     * @Description // 获取当前日期 零点 的时间戳
     * @Date 2020/6/9
     * @Param []
     * @return java.lang.Long
     **/
    public static long dayTimeInMillis() {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long time = calendar.getTimeInMillis();
        return time;
    }

    /*
     * @Author      hw
     * @Description // TODO 获取当前日期的零点  的相对【日期、小时、秒数 的加/减】 时间
     * @param      [day, hour, second]
     * @return      long
     * @date        2020-6-24 15:16
     */
    public static long dayTimeInMillis(int day, int hour, int second) {
        Calendar calendar = Calendar.getInstance();// 获取当前日期
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        if (day != 0) {
            calendar.add(Calendar.DATE, day); // 正数 往后 ，负数 往前 倒推
        }
        if (hour != 0) {
            calendar.add(Calendar.HOUR, hour);// 正数 往后 ，负数 往前 倒推
        }
        if (second != 0) {
            calendar.add(Calendar.SECOND, day);// 正数 往后 ，负数 往前 倒推
        }
        long time = calendar.getTimeInMillis();
        return time;
    }

    /**
     * 以天分割时间段
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByDay(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            beginDateList.add(calendar.getTimeInMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            if (calendar.getTimeInMillis() < endDate.getTime()) {
                endDateList.add(calendar.getTimeInMillis());
            } else {
                endDateList.add(endDate.getTime());
                break;
            }
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }

    /**
     * 以周分割时间段
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByWeek(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {

        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        beginDateList.add(calendar.getTimeInMillis());
        if (Calendar.MONDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
            splitTimeByWeekAddTimeStamp(beginDateList, endDateList, startDate, endDate, calendar);
        } else {
            if (Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
                Calendar special = Calendar.getInstance();
                special.setTime(startDate);
                special.set(Calendar.HOUR_OF_DAY, 23);
                special.set(Calendar.MINUTE, 59);
                special.set(Calendar.SECOND, 59);
                special.set(Calendar.MILLISECOND, 999);
                endDateList.add(special.getTime().getTime());
            }
            splitTimeByWeekAddTimeStamp(beginDateList, endDateList, startDate, endDate, calendar);
        }
    }

    private static void splitTimeByWeekAddTimeStamp(List<Long> beginDateList, List<Long> endDateList, Date startDate, Date endDate, Calendar calendar) {
        while (startDate.getTime() < endDate.getTime()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            startDate = calendar.getTime();
            if (Calendar.MONDAY == calendar.get(Calendar.DAY_OF_WEEK)) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                beginDateList.add(calendar.getTimeInMillis());
                startDate = calendar.getTime();
            } else if (Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK) || startDate.getTime() >= endDate.getTime()) {
                if (startDate.getTime() <= endDate.getTime()) {
                    calendar.set(Calendar.HOUR_OF_DAY, 23);
                    calendar.set(Calendar.MINUTE, 59);
                    calendar.set(Calendar.SECOND, 59);
                    calendar.set(Calendar.MILLISECOND, 999);
                    endDateList.add(calendar.getTimeInMillis());
                    startDate = calendar.getTime();
                } else {
                    endDateList.add(endDate.getTime());
                }
            }
        }
    }

    /**
     * 按照月份分割一段时间
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByMonth(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            beginDateList.add(calendar.getTimeInMillis());
            calendar.add(Calendar.MONTH, 1);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            if (calendar.getTimeInMillis() < endDate.getTime()) {
                endDateList.add(calendar.getTimeInMillis());
            } else {
                endDateList.add(endDate.getTime());
                break;
            }
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }

    /**
     * 按照年份分割一段时间
     *
     * @param startTime     开始时间戳(毫秒)
     * @param endTime       结束时间戳(毫秒)
     * @param beginDateList 开始段时间戳 和 结束段时间戳 一一对应
     * @param endDateList   结束段时间戳 和 开始段时间戳 一一对应
     */
    public static void getIntervalTimeByYear(Long startTime, Long endTime, List<Long> beginDateList, List<Long> endDateList) {
        Date startDate = new Date(startTime);
        Date endDate = new Date(endTime);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        while (calendar.getTimeInMillis() < endDate.getTime()) {
            beginDateList.add(calendar.getTimeInMillis());
            calendar.add(Calendar.YEAR, 1);
            calendar.set(Calendar.DAY_OF_YEAR, 1);
            calendar.add(Calendar.DATE, -1);
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            if (calendar.getTimeInMillis() < endDate.getTime()) {
                endDateList.add(calendar.getTimeInMillis());
            } else {
                endDateList.add(endDate.getTime());
                break;
            }
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
        }
    }

    /*
     * @Author hw
     * @Description //TODO UTC 东八区时间戳 秒级  转换
     * @Date 2020/4/20
     * @Param [time]
     * @return java.lang.String  yyyy-MM-dd HH:mm:ss
     **/
    public static String timeFormat4UTC(String time) {
        String strTime = "";
        try {
            Long lTime = Long.parseLong(time);
            SimpleDateFormat sdf = new SimpleDateFormat(DateUtils.PATTERN_FULL);
            strTime = sdf.format(new Date(lTime * 1000));  //UTC 东八区时间戳 秒级  需要 * 1000 换算成毫秒
            return strTime;
        } catch (Exception e) {
            strTime = "";
        }
        return strTime;
    }

    public static void main(String[] args) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long dontDisturbStartTime = dayTimeInMillis(0, 22, 0);
        System.out.println("免打扰开启时间：" + dontDisturbStartTime + "；" + sdf.format(new Date(dontDisturbStartTime)));

        Long dontDisturbEndTime = dayTimeInMillis(1, 7, 0);
        System.out.println("免打扰结束时间：" + dontDisturbEndTime + "；" + sdf.format(new Date(dontDisturbEndTime)));

    }

}
