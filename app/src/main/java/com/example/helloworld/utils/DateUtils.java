package com.example.helloworld.utils;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

    public static String getNowDateString(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// HH:mm:ss
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public Date getNowDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date nowDate = calendar.getTime();
        return nowDate;
    }

    public String transDateToString(Date date){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date);
    }

    public Date transStringToDate(String text){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
             date = simpleDateFormat.parse(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public boolean equalsDateForNow(Date date){
        if (getNowDate() == date)
            return true;
        else
            return false;
    }

    public static String getTimeString(Long timestamp) {
        String result = "";
        String[] weekNames = {"星期日", "星期一", "星期日二", "星期三", "星期四", "星期五", "星期六"};
        String hourTimeFormat = "HH:mm";
        String monthTimeFormat = "M月d日 HH:mm";
        String yearTimeFormat = "yyyy年M月d日 HH:mm";
        try {
            Calendar todayCalendar = Calendar.getInstance();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp);

            if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {//当年
                if (todayCalendar.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)) {//当月
                    int temp = todayCalendar.get(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH);
                    switch (temp) {
                        case 0://今天
                            result = getTime(timestamp, hourTimeFormat);
                            break;
                        case 1://昨天
                            result = "昨天 " + getTime(timestamp, hourTimeFormat);
                            break;
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
                            result = weekNames[dayOfWeek - 1] + " " + getTime(timestamp, hourTimeFormat);
                            break;
                        default:
                            result = getTime(timestamp, monthTimeFormat);
                            break;
                    }
                } else {
                    result = getTime(timestamp, monthTimeFormat);
                }
            } else {
                result = getTime(timestamp, yearTimeFormat);
            }
            return result;
        } catch (Exception e) {
            Log.e("getTimeString", e.getMessage());
            return "";
        }
    }

    public static String getTime(long time, String pattern) {
        Date date = new Date(time);
        return dateFormat(date, pattern);
    }

    public static String dateFormat(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static boolean DateBetween(String start,String now) {
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startTime = null;
        try {
            startTime = ft.parse(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date nowTime = null;
        try {
            nowTime = ft.parse(now);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date endTime = new Date(startTime.getTime() + 60000);
        boolean effectiveDate = isEffectiveDate(nowTime, startTime, endTime);
        if (effectiveDate) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param nowTime   当前时间
     * @param startTime	开始时间
     * @param endTime   结束时间
     * @return
     * @author sunran   判断当前时间在时间区间内
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }

}
