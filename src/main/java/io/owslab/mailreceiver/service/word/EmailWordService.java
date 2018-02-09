package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.EmailWordDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Service
public class EmailWordService {
    @Autowired
    private EmailWordDAO emailWordDAO;
}
