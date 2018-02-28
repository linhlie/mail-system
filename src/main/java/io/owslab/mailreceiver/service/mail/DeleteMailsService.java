package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
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

    @Autowired
    private FileDAO fileDAO;

    public void deleteOldMails(Date beforeDate){
        List<Email> emailList = emailDAO.findBySentAtBeforeAndDeletedOrderBySentAtAsc(beforeDate, false);
        System.out.println("Start delete Old mails - total: " + emailList.size());
        for(int i = 0, n = emailList.size(); i < n; i++){
            Email email = emailList.get(i);
            logger.info("Start delete mail: " + email.getSubject());
            //Update: delete mail and mark as deleted
            email.setCc(null);
            email.setBcc(null);
            email.setReplyTo(null);
            email.setReceivedAt(null);
            email.setOptimizedBody(null);
            email.setOriginalBody(null);
            email.setHeader(null);
            email.setMetaData(null);
            email.setDeleted(true);
            email.setDeletedAt(new Date());
            emailDAO.save(email);
            //TODO: transac with delete mail and delete file?
            this.deleteFileBelongToMail(email.getMessageId());
        }
    }

    public void deleteFileBelongToMail(String messageId){
        List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
        for(int i = 0, n = files.size(); i < n; i++){
            AttachmentFile file = files.get(i);
            //TODO: delete file storaged
            file.setMetaData(null);
            file.setDeleted(true);
            file.setDeletedAt(new Date());
            fileDAO.save(file);
        }
    }
}
