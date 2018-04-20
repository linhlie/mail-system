package io.owslab.mailreceiver.utils;

import org.springframework.cache.annotation.Cacheable;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * Created by khanhlvb on 3/7/18.
 */
public class Utils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public static void init(){
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
    }
    public synchronized static Date trim(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR_OF_DAY, 0);

        return calendar.getTime();
    }

    public synchronized static Date addDayToDate(Date date, int day){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return c.getTime();
    }

    @Cacheable(key="\"Utils:parseDateStr:\"+#str")
    public synchronized static Date parseDateStr(final String str)
            throws ParseException {
        return DATE_FORMAT.parse(str);
    }

    public synchronized static String formatNumber(double number){
        return java.text.NumberFormat.getNumberInstance(Locale.US).format(number);
    }
}
