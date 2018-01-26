package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
public class MailBoxService {
    @Autowired
    private EmailDAO emailDAO;

    public long count(){
        return emailDAO.count();
    }

    public List<Email> list() {
        List<Email> list = (List<Email>) emailDAO.findAll();
        return list;
    }
}
