package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.EmailWordDAO;
import io.owslab.mailreceiver.model.EmailWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Service
public class EmailWordService {

    @Autowired
    private EmailWordDAO emailWordDAO;

    public void save(EmailWord emailWord){
        emailWordDAO.save(emailWord);
    }

}
