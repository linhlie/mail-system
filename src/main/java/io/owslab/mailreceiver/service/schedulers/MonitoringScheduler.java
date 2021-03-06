package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Component
public class MonitoringScheduler extends AbstractScheduler{
    private static final Logger logger = LoggerFactory.getLogger(MonitoringScheduler.class);
    private static final long CHECK_TIME_TO_MOTORING_FETCH_MAIL_INTERVAL_IN_SECEOND = 60L;
    private static final int CHECK_TIME_TO_SEND_EMAIL_WARNING_IN_MINUTE = 5;
    private DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private boolean isReadyReport = false;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private FetchMailScheduler fetchMailScheduler;

    private Date lastTimeUpdate;

    public MonitoringScheduler() {
        super(0, CHECK_TIME_TO_MOTORING_FETCH_MAIL_INTERVAL_IN_SECEOND);
    }

    @Override
    public void doStuff() {
        if(isNeedToCheck()){
            startUpdate();
        }
    }

    private boolean isNeedToCheck(){
        Optional<Date> lastTimeCheckFetchMailOpt = getLastTimeUpdate();
        if(lastTimeCheckFetchMailOpt.isPresent()){
            Date now = new Date();
            Date lastTimeCheckFetchMail = lastTimeCheckFetchMailOpt.get();
            int checkMailInMinute = enviromentSettingService.getCheckMailTimeInterval();
            Date nextTimeToFetchMail = addMinutesToADate(lastTimeCheckFetchMail, checkMailInMinute);
            return nextTimeToFetchMail.compareTo(now) <= 0;
        }
        return true;
    }

    private void startUpdate(){
        if(!isReadyReport){
            isReadyReport = true;
            return;
        }
        lastTimeUpdate = new Date();
        String timeFetchMailBefore = enviromentSettingService.getCheckTimeFetchMail();
        int checkMailInMinute = enviromentSettingService.getCheckMailTimeInterval();
        logger.info("Check fetch mail process ");
        try {
            if(timeFetchMailBefore!=null){
                Date TimeFetchMailLastest = df.parse(timeFetchMailBefore);
                Date nextTimeToFetchMail = addMinutesToADate(TimeFetchMailLastest, checkMailInMinute + CHECK_TIME_TO_SEND_EMAIL_WARNING_IN_MINUTE);
                Date now = new Date();
                if(nextTimeToFetchMail.compareTo(now) < 0){
                    logger.info("Send mail report");
                    ReportErrorService.report("Warning : [Auto fetch mail stopped working at "+timeFetchMailBefore+"]");
                    fetchMailScheduler.restartService();
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private Optional<Date> getLastTimeUpdate(){
        //TODO: find last time fetch mail;
        if(lastTimeUpdate == null){
            return Optional.empty();
        }
        return Optional.of(lastTimeUpdate);
    }

    private Date addMinutesToADate(Date date, int minute){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE, minute);
        return cal.getTime();
    }
}
