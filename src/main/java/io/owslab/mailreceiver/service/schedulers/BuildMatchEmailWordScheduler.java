package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.word.EmailWordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Component
public class BuildMatchEmailWordScheduler extends AbstractScheduler {
    private static final Logger logger = LoggerFactory.getLogger(BuildMatchEmailWordScheduler.class);
    private static final long INTERVAL_IN_SECOND = 10L;
    private static final int DELAY_IN_SECOND = 5;

    @Autowired
    private EmailWordService emailWordService;

    public BuildMatchEmailWordScheduler() {
        super(DELAY_IN_SECOND, INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() {
        logger.info("BuildMatchEmailWordScheduler doing stuff");
    }
}
