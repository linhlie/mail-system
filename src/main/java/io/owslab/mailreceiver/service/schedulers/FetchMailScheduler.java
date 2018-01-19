package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.receive.FetchMailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class FetchMailScheduler {

    private static final Logger logger = LoggerFactory.getLogger(FetchMailScheduler.class);
    private static final long CHECK_TIME_TO_FETCH_MAIL_INTERVAL_IN_SECEOND = 5000L;
    private static final int FETCH_MAIL_INTERVAL_IN_MINUTE = 1;

    @Autowired
    private FetchMailsService fetchMailsService;

    private Date lastTimeFetchedMail;

    public void startCheckTimeToFetchMailInterval(){
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                if(isNeedToFetchNewMail()){
                    startFetchMail();
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, 0, CHECK_TIME_TO_FETCH_MAIL_INTERVAL_IN_SECEOND);
    }

    private boolean isNeedToFetchNewMail(){
        Optional<Date> lastTimeFetchMailOpt = getLastTimeFetchMail();
        if(lastTimeFetchMailOpt.isPresent()){
            Date now = new Date();
            Date lastTimeFetchMail = lastTimeFetchMailOpt.get();
            Date nextTimeToFetchMail = addMinutesToADate(lastTimeFetchMail, FETCH_MAIL_INTERVAL_IN_MINUTE);
            return nextTimeToFetchMail.compareTo(now) <= 0;
        }
        return true;
    }

    private void startFetchMail(){
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