package io.owslab.mailreceiver.service.receive;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by khanhlvb on 1/19/18.
 */
@Service
public class FetchMailsService {
    private static final Logger logger = LoggerFactory.getLogger(FetchMailsService.class);

    public void start(){
        logger.info("Starting fetch mails");
    }
}
