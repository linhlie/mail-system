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
public class DeleteSentMailHistoryScheduler extends AbstractScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DeleteSentMailHistoryScheduler.class);
    private static final long INTERVAL_IN_SECOND = 600L;
    private static final int DELAY_IN_SECOND = 10;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private DeleteMailsService deleteMailsService;

    public DeleteSentMailHistoryScheduler() {
        super(DELAY_IN_SECOND, INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() {
        String keepSentMailHistoryDay = enviromentSettingService.getKeepSentMailHistoryDay();
        if(keepSentMailHistoryDay == null || keepSentMailHistoryDay.length() == 0) return;
        startDeleteHistory(Integer.parseInt(keepSentMailHistoryDay));
    }

    private void startDeleteHistory(int beforeDayRang) {
        Date now = new Date();
        Date beforeDate = addDayToDate(now, -beforeDayRang);
        deleteMailsService.deleteSentMailHistory(beforeDate);
    }

    private Date addDayToDate(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return c.getTime();
    }
}
