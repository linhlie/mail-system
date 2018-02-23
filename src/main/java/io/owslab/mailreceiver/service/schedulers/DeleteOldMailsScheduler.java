package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.mail.DeleteMailsService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class DeleteOldMailsScheduler extends AbstractScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteOldMailsScheduler.class);
    private static final long INTERVAL_IN_SECOND = 600L;
    private static final int DELAY_IN_SECOND = 10;

    @Autowired
    private DeleteMailsService deleteMailsService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    public DeleteOldMailsScheduler() {
        super(DELAY_IN_SECOND, INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() {
        if(isAllowDeleteOldMail()){
            startDeleteOldMails();
        }
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
