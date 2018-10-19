package com.jiuzhang.yeyuan.dribbbo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtils {
    private static SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault());
    private static SimpleDateFormat newFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

    public static String dateToString (Date date) {
        return newFormat.format(date);
    }

    public static Date StringToDate (String dateString) throws ParseException {
        return originFormat.parse(dateString);
    }

    public static String formatDateString (String dateString) {
        try {
            Date date = StringToDate(dateString);
            return dateToString(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

}
