package io.owslab.mailreceiver.service.settings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Created by khanhlvb on 7/18/18.
 */
@Service
public class ProcessService {

    @Autowired
    private MailReceiveRuleService mrrs;

    @Async("processExecutor")
    public void markPast() {
        mrrs.markPast();
    }
}
