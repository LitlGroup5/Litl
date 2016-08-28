package com.litlgroup.litl.utils;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import timber.log.Timber;

/**
 * Created by Hari on 8/28/2016.
 */
public class DateUtils {

    public static Date getDateTime(String timeStampMillis) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(Long.parseLong(timeStampMillis));
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));

            Date currentTimeZone = calendar.getTime();
            return currentTimeZone;
        } catch (Exception ex) {
            Timber.e("Error getting date time from timestamp");
        }
        return null;
    }
}
