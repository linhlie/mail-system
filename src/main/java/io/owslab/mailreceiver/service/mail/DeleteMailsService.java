package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.job.IMAPFetchMailJob;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import io.owslab.mailreceiver.protocols.ReceiveMailProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class DeleteMailsService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMailsService.class);

    @Autowired
    private EmailDAO emailDAO;

    public void deleteOldMails(Date beforeDate){
        //TODO: what if delete all mail in database, we have no email to refenrence last time sent -> fetch old mail;
        List<Email> emailList = emailDAO.findByCreatedAtBeforeOrderByCreatedAtAsc(beforeDate);
        System.out.println("Start delete Old mails - total: " + emailList.size());
        for(int i = 0, n = emailList.size(); i < n; i++){
            Email email = emailList.get(i);
            System.out.println("Should elete mail " + i  + ": " + email.getSubject());
            //TODO: delete mail
        }
    }
}
