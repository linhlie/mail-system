package io.owslab.mailreceiver.utils;

import com.mariten.kanatools.KanaConverter;
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

    public static final SimpleDateFormat GMT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm z");

    public static void init(){
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        GMT_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+9"));
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

    @Cacheable(key="\"Utils:normalize:\"+#raw")
    public synchronized static String normalize(String raw){
        int conv_op_flags = 0;
        conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
        conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
        String japaneseOptimizedText = KanaConverter.convertKana(raw, conv_op_flags);
        return japaneseOptimizedText.toLowerCase();
    }

    public synchronized static String formatGMT(Date date){
        return GMT_FORMAT.format(date);
    }
}
