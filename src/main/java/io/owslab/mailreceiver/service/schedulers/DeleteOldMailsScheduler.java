package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.mail.DeleteMailsService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

@Component
public class DeleteOldMailsScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOldMailsScheduler.class);
    private static final int DELETE_OLD_MAIL_INTERVAL_IN_MINUTE = 10;
    private static final int DELETE_OLD_MAIL_START_UP_DELAY_IN_SECOND = 10;

    @Autowired
    private DeleteMailsService deleteMailsService;
    @Autowired
    private EnviromentSettingService enviromentSettingService;

    public void startDeleteOldMailInterval(){
        TimerTask repeatedTask = new TimerTask() {
            public void run() {
                if(isAllowDeleteOldMail()){
                    startDeleteOldMails();
                }
            }
        };
        Timer timer = new Timer("Timer");
        timer.scheduleAtFixedRate(repeatedTask, DELETE_OLD_MAIL_START_UP_DELAY_IN_SECOND, DELETE_OLD_MAIL_INTERVAL_IN_MINUTE * 60 * 1000);
    }

    private boolean isAllowDeleteOldMail(){
        return enviromentSettingService.getDeleteOldMail();
    }

    private void startDeleteOldMails(){
        Date now = new Date();
        int beforeDayRang = enviromentSettingService.getDeleteAfter();
        Date beforeDate = addDayToDate(now, -beforeDayRang);
        deleteMailsService.deleteOldMails(beforeDate);
    }

    private Date addDayToDate(Date date, int day){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return c.getTime();
    }
}
