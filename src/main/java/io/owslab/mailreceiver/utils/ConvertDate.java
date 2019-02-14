package io.owslab.mailreceiver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertDate {
    private static final Logger logger = LoggerFactory.getLogger(ConvertDate.class);

    private static DateFormat fullDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");

    public static String convertDateToYYMMDDHHMM(Date date){
        String dateConvert = "unknow";
        try {
            dateConvert = fullDateFormat.format(date);
        }catch (Exception ex){
            logger.error(ex.toString());
        }
        return dateConvert;
    }
}
