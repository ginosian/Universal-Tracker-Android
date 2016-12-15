package com.margin.mgms.util;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created on June 09, 2016.
 *
 * @author Marta.Ginosyan
 */
public class DateUtils {

    private static final DateFormat sDateFormat = new SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
    private static final DateFormat sTodayFormat = new SimpleDateFormat("HH:mm", Locale.US);
    private static final DateFormat sThisYearFormat = new SimpleDateFormat("MMM d", Locale.US);
    private static final DateFormat sLastYearFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);

    /**
     * Formats string like 'yyyy-MM-ddTHH:mm:ss.SSSZ' and returns it as formatted string
     *
     * @return "HH:mm" if date == today,
     * <p>"MMM d" if date < today & year == this year,
     * <p>"MMM d, yyyy", if date < today & year < this year
     */
    public static String getFormattedDate(String dateString) {
        Date date = getDateFromString(dateString);
        return getFormattedDate(date);
    }

    /**
     * Formats {@link Date} and returns it as formatted string
     *
     * @return "HH:mm" if date == today,
     * <p>"MMM d" if date < today & year == this year,
     * <p>"MMM d, yyyy", if date < today & year < this year
     */
    public static String getFormattedDate(@NonNull Date date) {
        Calendar dateTime = Calendar.getInstance();
        dateTime.setTimeInMillis(date.getTime());
        Calendar now = Calendar.getInstance();
        if (now.get(Calendar.YEAR) == dateTime.get(Calendar.YEAR)) {
            if (now.get(Calendar.DAY_OF_YEAR) == dateTime.get(Calendar.DAY_OF_YEAR)) {
                return sTodayFormat.format(date);
            } else return sThisYearFormat.format(date);
        } else return sLastYearFormat.format(date);
    }

    /**
     * Formats {@link Date} to formatted string
     *
     * @return string like 'yyyy-MM-ddTHH:mm:ss.SSSZ'
     */
    public static String formatDate(Date date) {
        return sDateFormat.format(date);
    }

    /**
     * Parses {@link Date} from formatted string like 'yyyy-MM-ddTHH:mm:ss.SSSZ'
     */
    public static Date getDateFromString(String dateString) {
        return getDateFromString(sDateFormat, dateString);
    }

    /**
     * Parses {@link Date} from formatted string with dateFormat
     */
    public static Date getDateFromString(DateFormat dateFormat, String dateString) {
        if (dateString != null) {
            try {
                return dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
                LogUtils.e("Error parsing " + dateString + " : " + e.getMessage());
            }
        }
        return new Date(0);
    }
}
