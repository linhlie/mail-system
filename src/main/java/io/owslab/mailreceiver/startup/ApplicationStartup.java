package io.owslab.mailreceiver.startup;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.schedulers.BuildMatchEmailWordScheduler;
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

    @Autowired
    private BuildMatchEmailWordScheduler buildMatchEmailWordScheduler;

    @Autowired
    private NumberRangeService numberRangeService;

    private static final Logger logger = LoggerFactory.getLogger(ApplicationStartup.class);

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        enviromentSettingService.init();
        fetchMailScheduler.start();
        deleteOldMailsScheduler.start();
        buildMatchEmailWordScheduler.start();
//        Email testEmail = new Email();
//        testEmail.setOptimizedBody("              There         +. -. are more than 以上-2K and less than +12万YEN　    ~　   15円 numbers here +13.2千~.2千 2 ~ 000,000 ３４千");
//        numberRangeService.buildNumberRangeForEmail(testEmail);
        return;
    }
}
