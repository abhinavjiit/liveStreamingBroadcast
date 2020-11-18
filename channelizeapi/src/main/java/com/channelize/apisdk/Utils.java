/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;


public class Utils {

    public static String getCurrentTimestamp() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("dd/MM/yyyy/HH/mm/ss",
                Locale.getDefault());
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(new Date());
    }

    public static String getCurrentDate() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss",
                Locale.getDefault());
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatGmt.format(new Date());
    }

    public static String getTimestamp(long timestamp, Context context) {

        SimpleDateFormat sdf;

        if (DateFormat.is24HourFormat(context)) {
            sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        } else {
            sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        }

        String time = sdf.format(timestamp);

//        long millis = TimeZone.getDefault().getOffset(timestamp);
//        long hour = (millis / (1000 * 60 * 60)) % 24;
//        long minutes = (millis / (1000 * 60)) % 60;
//
//        String[] timestampPart = time.split("/");
//        long h = Long.parseLong(timestampPart[0]);
//        long m = Long.parseLong(timestampPart[1]);
//        h += hour;
//        h %= 24;
//        m += minutes;
//        m %= 60;
//
//        String output = h + ":" + m;
//        if (h < 10) {
//            if (m < 10) {
//                output = "0" + h + ":0" + m;
//            } else {
//                output = "0" + h + ":" + m;
//            }
//        } else if (m < 10) {
//            output = h + ":0" + m;
//        }
        return time;
    }

//    public static String getDate(String timestamp) {
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/mm");
//            Date date = sdf.parse(timestamp);
//
//            Log.d(Utils.class.getSimpleName(), "date " + date.toString());
//            long currentDate = date.getTime();
//
//            Log.d(Utils.class.getSimpleName(), "currentDate 1" + currentDate);
//            currentDate += TimeZone.getDefault().getOffset(currentDate);
//
//            Log.d(Utils.class.getSimpleName(), "currentDate 2" + currentDate);
//
//            SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
//            return sdfDate.format(currentDate);
//        } catch (ParseException e) {
//
//        }
//        return null;
//    }

    public static String getDateFromTimeStamp(long timestamp) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        return sdf.format(timestamp);
    }

    public static String getDateOfMessage(Context context, long timestamp) {

//        timestamp += TimeZone.getDefault().getOffset(timestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        if (DateUtils.isToday(timestamp)) {
            return context.getResources().getString(R.string.pm_today);
        } else if (DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS)) {
            return context.getResources().getString(R.string.pm_yesterday);
        } else {
            return sdf.format(timestamp);
        }
    }


    public static String getRelativeTimeString(long timestamp) {

        CharSequence output = null;

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd/HH/mm/ss",
                    Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date dt = formatter.parse(formatter.format(timestamp));
            output = DateUtils.getRelativeTimeSpanString(dt.getTime());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (output != null) {
            return output.toString();
        }

        return null;
    }

    /**
     * Convert milliseconds into time hh:mm:ss
     *
     * @param milliseconds
     * @return time in String
     */
    public static String getDuration(long milliseconds) {
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);

        String s = (sec < 10) ? "0" + sec : "" + sec;
        String m = (min < 10) ? "0" + min : "" + min;
        String h = "" + hour;

        String time = "";
        if (hour > 0) {
            time = h + ":" + m + ":" + s;
        } else {
            time = m + ":" + s;
        }
        return time;
    }

    /**
     * Convert milliseconds into time "h hr m min s sec".
     *
     * @param milliseconds Time in milliseconds which needs to be convert.
     * @return Returns the time in formatted String.
     */
    public static String getCallDuration(long milliseconds) {
        Context context = Channelize.getInstance().getContext();
        if (milliseconds <= 0) {
            return "0 " + context.getResources().getString(R.string.pm_second_text);
        }
        long sec = (milliseconds / 1000) % 60;
        long min = (milliseconds / (60 * 1000)) % 60;
        long hour = milliseconds / (60 * 60 * 1000);
        String time = "";

        // Adding hours.
        if (hour == 0) {
            time = "";
        } else {
            time = hour + " " + context.getResources().getString(R.string.pm_hour_text) + " ";
        }
        // Adding minutes.
        if (min > 0) {
            time = time + min + " " + context.getResources().getString(R.string.pm_minute_text) + " ";
        }
        // Adding seconds.
        if (sec > 0) {
            time = time + sec + " " + context.getResources().getString(R.string.pm_second_text);
        }
        return time;
    }

    /**
     * Method to get the time in formatted way.
     *
     * @param createdDate Date which needs to be format.
     * @return Returns the formatted date.
     */
    public static String getFormattedTime(long createdDate) {
        long timeNow = new Date().getTime();
        long timeElapsed = timeNow - createdDate;
        Context context = Channelize.getInstance().getContext();

        // Lengths of one week time durations in Long format.
        long oneWeek = 604800000L;

        String formattedTime;

        if (DateUtils.isToday(createdDate)) {
            formattedTime = Utils.getTimestamp(createdDate, context);

        } else if (timeElapsed < oneWeek) {
            SimpleDateFormat outFormat = new SimpleDateFormat("EEEE", Locale.getDefault());
            formattedTime = outFormat.format(createdDate);

        } else if (timeElapsed < 4 * oneWeek) {
            double weeks = (double) ((timeElapsed / 1000) / 60 / 60 / 24 / 7);
            weeks = Math.round(weeks);
            formattedTime = context.getResources().getQuantityString(R.plurals.pm_week_count,
                    (int) weeks, (int) weeks);

        } else {
            formattedTime = Utils.getRelativeTimeString(createdDate);
        }
        return formattedTime;
    }

}
