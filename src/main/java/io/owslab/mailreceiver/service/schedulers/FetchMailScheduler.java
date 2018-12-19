package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FetchMailScheduler extends AbstractScheduler {
    private static final Logger logger = LoggerFactory.getLogger(FetchMailScheduler.class);
    private static final long CHECK_TIME_TO_FETCH_MAIL_INTERVAL_IN_SECONDS = 60L;
    private static final int TIMEOUT_TO_FETCH_MAIL_INTERVAL_IN_SECONDS = 300;
    private static final int FETCH_MAIL_INTERVAL_IN_MINUTE = 10;

    @Autowired
    private FetchMailsService fetchMailsService;
    @Autowired
    private EnviromentSettingService enviromentSettingService;

    private Date lastTimeFetchedMail;

    public FetchMailScheduler() {
        super(0, CHECK_TIME_TO_FETCH_MAIL_INTERVAL_IN_SECONDS, TIMEOUT_TO_FETCH_MAIL_INTERVAL_IN_SECONDS);

    }

    @Override
    public void doStuff() {
        if(isNeedToFetchNewMail()){
            startFetchMail();
        }
    }

    private boolean isNeedToFetchNewMail(){
        Optional<Date> lastTimeFetchMailOpt = getLastTimeFetchMail();
        if(lastTimeFetchMailOpt.isPresent()){
            Date now = new Date();
            Date lastTimeFetchMail = lastTimeFetchMailOpt.get();
            int checkMailInMinute = enviromentSettingService.getCheckMailTimeInterval();
            Date nextTimeToFetchMail = addMinutesToADate(lastTimeFetchMail, checkMailInMinute);
            return nextTimeToFetchMail.compareTo(now) <= 0;
        }
        return true;
    }

    private void startFetchMail(){
        int checkMailInMinute = enviromentSettingService.getCheckMailTimeInterval();
        if(checkMailInMinute<=2){
            this.setDoStuffTimeout(50);
        }else{
            this.setDoStuffTimeout((checkMailInMinute-2)*60);
        }
        lastTimeFetchedMail = new Date();
        fetchMailsService.start();
    }

    private Optional<Date> getLastTimeFetchMail(){
        //TODO: find last time fetch mail;
        if(lastTimeFetchedMail == null){
            return Optional.empty();
        }
        return Optional.of(lastTimeFetchedMail);
    }

    private Date addMinutesToADate(Date date, int minute){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }
}
