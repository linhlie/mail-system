package io.owslab.mailreceiver.service.schedulers;

import io.owslab.mailreceiver.service.settings.MailReceiveRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by khanhlvb on 7/18/18.
 */
@Component
public class ReceiveMailScheduler extends AbstractScheduler {

    private static final Logger logger = LoggerFactory.getLogger(ReceiveMailScheduler.class);
    private static final long INTERVAL_IN_SECOND = 120L;
    private static final int DELAY_IN_SECOND = 10;

    @Autowired
    private MailReceiveRuleService mrrs;

    public ReceiveMailScheduler() {
        super(DELAY_IN_SECOND, INTERVAL_IN_SECOND);
    }

    @Override
    public void doStuff() {
        mrrs.checkMailStatus();
    }
}
