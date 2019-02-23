package io.owslab.mailreceiver.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertDomain {
    private static final Logger logger = LoggerFactory.getLogger(ConvertDomain.class);

    public static String convertEmailToDomain(String email){
        int index = email.indexOf("@");
        if(index<=0) return "";

        return  email.substring(index+1).toLowerCase();
    }
}
