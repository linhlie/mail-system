package io.owslab.mailreceiver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertDate {
    private static final Logger logger = LoggerFactory.getLogger(ConvertDate.class);

    private static DateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    private static DateFormat dateFormatMMdd = new SimpleDateFormat("MM/dd");
    private static DateFormat dateFormatHHmm = new SimpleDateFormat("HH:mm");

    public static String convertDateToYYMMDDHHMM(Date date){
        String dateConvert = "unknow";
        try {
            dateConvert = fullDateFormat.format(date);
        }catch (Exception ex){
            logger.error(ex.toString());
        }
        return dateConvert;
    }

    public static String convertDateToMMdd(Date date){
        String dateConvert = "unknow";
        try {
            dateConvert = dateFormatMMdd.format(date);
        }catch (Exception ex){
            logger.error(ex.toString());
        }
        String str[] = dateConvert.split("/");
        dateConvert = str[0]+ "月" +str[1]+ "日";
        return dateConvert;
    }

    public static String convertHourStatistic(Date date){
        String dateConvert = "unknow";
        try {
            dateConvert = dateFormatHHmm.format(date);
        }catch (Exception ex){
            logger.error(ex.toString());
        }
        String str[] = dateConvert.split(":");
        dateConvert = str[0]+ ":00";
        return dateConvert;
    }

    public static Date convertDateScheduler(String dateString){
        Date date = null;
        try {
            date = new SimpleDateFormat("MM-dd-yyyy HH:mm").parse(dateString);
        }catch (Exception ex){
            logger.error(ex.toString());
        }
        return date;
    }
}
