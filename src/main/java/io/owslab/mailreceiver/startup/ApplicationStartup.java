package io.owslab.mailreceiver.startup;

import io.owslab.mailreceiver.service.schedulers.DeleteOldMailsScheduler;
import io.owslab.mailreceiver.service.schedulers.FetchMailScheduler;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartup {

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private FetchMailScheduler fetchMailScheduler;

    @Autowired
    private DeleteOldMailsScheduler deleteOldMailsScheduler;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        enviromentSettingService.init();
        fetchMailScheduler.startCheckTimeToFetchMailInterval();
        deleteOldMailsScheduler.startDeleteOldMailInterval();
        return;
    }
}
