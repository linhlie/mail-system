package io.owslab.mailreceiver.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by khanhlvb on 3/7/18.
 */
public class Utils {
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
}
