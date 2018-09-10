package io.owslab.mailreceiver.utils;

import com.mariten.kanatools.KanaConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 3/7/18.
 */
public class Utils {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    public static final SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("yyyy/MM/dd");

    public static final SimpleDateFormat GMT_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm z");
    public static final SimpleDateFormat GMT_FORMAT_2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void init(){
        DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        DATE_FORMAT_2.setTimeZone(TimeZone.getTimeZone("Asia/Tokyo"));
        GMT_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT+9"));
        GMT_FORMAT_2.setTimeZone(TimeZone.getTimeZone("GMT+9"));
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
    public synchronized static Date addMonthsToDate(Date date, int months){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, months);
        return c.getTime();
    }

    @Cacheable(key="\"Utils:parseDateStr:\"+#str")
    public synchronized static Date parseDateStr(final String str)
            throws ParseException {
        String replacedStr = str.replaceAll("/", "-");
        return DATE_FORMAT.parse(replacedStr);
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

    public synchronized static String formatTimestamp(SimpleDateFormat sdf, long ts) {
        Date date = new Date(ts);
        return sdf.format(date);
    }

    public synchronized static String formatGMT(Date date){
        return GMT_FORMAT.format(date);
    }
    public synchronized static String formatGMT2(Date date){
        return GMT_FORMAT_2.format(date);
    }

    public synchronized static Date atStartOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return localDateTimeToDate(startOfDay);
    }

    public synchronized static Date atEndOfDay(Date date) {
        LocalDateTime localDateTime = dateToLocalDateTime(date);
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return localDateTimeToDate(endOfDay);
    }

    private static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.of("GMT+9"));
    }

    private static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.of("GMT+9")).toInstant());
    }

    public synchronized static File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public synchronized static boolean matchingNumber(String content) {
        Pattern p = Pattern.compile("^\\d+(,\\d{3})*(\\.\\d+)?$");
        Matcher m = p.matcher(content);
        return m.matches();
    }
}
