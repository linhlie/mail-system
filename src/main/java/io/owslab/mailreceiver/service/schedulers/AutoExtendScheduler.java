package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.expansion.EngineerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by khanhlvb on 8/22/18.
 */
@Component
public class AutoExtendScheduler extends AbstractScheduler {
    private static final Logger logger = LoggerFactory.getLogger(FetchMailScheduler.class);
    private static final long AUTO_EXTEND_INTERVAL_IN_SECOND = 60L;

    @Autowired
    private EngineerService engineerService;

    public AutoExtendScheduler() {
        super(0, AUTO_EXTEND_INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() {
        startAutoExtend();
    }

    private void startAutoExtend(){
        engineerService.autoExtend();
    }
}
