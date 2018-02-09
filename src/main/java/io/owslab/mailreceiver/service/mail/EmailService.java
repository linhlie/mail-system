package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Service
public class EmailService {
    @Autowired
    private EmailDAO emailDAO;

    public Email findOne(String messageId, boolean deleted){
        List<Email> emailList = emailDAO.findByMessageIdAndDeleted(messageId, deleted);
        return emailList.size() > 0 ? emailList.get(0) : null;
    }
}
