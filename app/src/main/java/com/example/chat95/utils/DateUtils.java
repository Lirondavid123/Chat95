package com.example.chat95.utils;

import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtils {
    private static final String TAG ="DateUtils" ;

    public DateUtils() {
    }

    public static boolean isEventCurrentlyOccurring(String strBeginDate, String strFinishDate, String strBeginTime, String strFinishTime) {
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date beginDate = df.parse(strBeginDate);
            Date finishDate = df.parse(strFinishDate);
            Date currentDate = df.parse(df.format(Calendar.getInstance().getTime()));

            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);
            String strCurrentTime = new StringBuilder().append("").append(checkDigit(hour)).append(":").append(checkDigit(minute)).toString();

            //the current event is relevant to date
            if (isDateBetweenTwoGivenDate(currentDate, beginDate, finishDate)) {
                //the event is occurring for one day only
                if (currentDate.compareTo(beginDate) == 0 && currentDate.compareTo(finishDate) == 0) {
                    Log.d(TAG, "the current event is relevant to date");
                    return isTimeBetweenTwoGivenTimes(strCurrentTime, strBeginTime, strFinishTime);
                }
                //the beginning date is today and the event will occur for more than one day
                else if (currentDate.compareTo(beginDate) == 0 && currentDate.compareTo(finishDate) < 0) {
                    Log.d(TAG, "the beginning date is today and the event will occur for more than one day");
                    return isTimeBeforeGivenTime(strBeginTime, strCurrentTime);
                }
                //the finishing date is today and the event has been occurring for more than one day
                else if (currentDate.compareTo(beginDate) > 0 && currentDate.compareTo(finishDate) == 0) {
                    Log.d(TAG, "the finishing date is today and the event has been occurring for more than one day");
                    return isTimeBeforeGivenTime(strCurrentTime, strFinishTime);
                }
                //the event has started some day before and will finish some day after today
                else if (currentDate.compareTo(beginDate) > 0 && currentDate.compareTo(finishDate) < 0) {
                    Log.d(TAG, "the event has started some day before and will finish some day after today");
                    return true;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "the event is irrelevant");

        return false;
    }

    public static boolean isDateBetweenTwoGivenDate(Date date, Date beginDate, Date finishDate) {
        if (!date.before(beginDate) && !date.after(finishDate)) {
            return true;
        }
        return false;
    }

    public static boolean isTimeBetweenTwoGivenTimes(String time, String beginTime, String finishTime) {
        if (!isTimeBeforeGivenTime(time, beginTime) && isTimeBeforeGivenTime(time, finishTime)) {
            return true;
        }
        return false;
    }

    //return true if @time comes before @givenTime, false otherwise
    public static boolean isTimeBeforeGivenTime(String time, String givenTime) {
        int isTimeBeforeGivenTime = time.compareTo(givenTime);

        if (isTimeBeforeGivenTime <= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static String checkDigit(int number) {
        return number <= 9 ? "0" + number : String.valueOf(number);
    }
    public static String getCurrentTimeString(){
        Date date = new Date(); // This object contains the current date value
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return formatter.format(date);
    }
}
