package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.SentMailHistory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class DeleteMailsService {

    private static final Logger logger = LoggerFactory.getLogger(DeleteMailsService.class);

    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private SentMailHistoryDAO sentMailHistoryDAO;

    @Autowired
    private MailBoxService mailBoxService;

    public void deleteOldMails(Date beforeDate){
        List<Email> emailList = emailDAO.findBySentAtBeforeAndDeletedOrderBySentAtAsc(beforeDate, false);
        System.out.println("Start delete Old mails - total: " + emailList.size());
        for(int i = 0, n = emailList.size(); i < n; i++){
            Email email = emailList.get(i);
            logger.info("Start delete mail: " + email.getSubject());
            emailDAO.delete(email);
            //TODO: transac with delete mail and delete file?
            this.deleteFileBelongToMail(email.getMessageId());
        }
        mailBoxService.getAll(true);
    }

    public void deleteSentMailHistory(Date beforeDate) {
        List<SentMailHistory> histories = sentMailHistoryDAO.findBySentAtLessThanEqual(beforeDate);
        sentMailHistoryDAO.delete(histories);
    }

    public void deleteFileBelongToMail(String messageId){
        List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
        for(int i = 0, n = files.size(); i < n; i++){
            AttachmentFile fileDoc = files.get(i);
            File file = new File(fileDoc.getStoragePath());
            try {
                FileUtils.cleanDirectory(file);
                file.delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileDAO.delete(fileDoc.getId());
        }
    }
}
