package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.file.SentMailFileService;
import io.owslab.mailreceiver.service.file.UploadFileService;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

    @Autowired
    UploadFileService uploadFileService;

    @Autowired
    SentMailFileService sentMailFileService;

    public void deleteOldMails(Date beforeDate){
        List<Email> emailList = emailDAO.findBySentAtBeforeOrderBySentAtAsc(beforeDate);
        logger.info("Start delete Old mails - total: " + emailList.size());
        for(int i = 0, n = emailList.size(); i < n; i++){
            Email email = emailList.get(i);
            logger.info("Start delete mail: " + email.getSubject());
            //TODO: transac with delete mail and delete file?
            this.deleteFileBelongToMail(email.getMessageId());
            emailDAO.delete(email);
        }
        mailBoxService.getAll(true);
    }

    public void deleteSentMailHistory(Date beforeDate) {
        List<SentMailHistory> histories = sentMailHistoryDAO.findBySentAtLessThanEqual(beforeDate);
        for (SentMailHistory sentMail : histories){
            if(sentMail.isHasAttachment() && sentMail.isCanDelete()){
                List<SentMailFiles> sentMailFiles = sentMailFileService.getByMailId(sentMail.getId());
                for(SentMailFiles file : sentMailFiles){
                    uploadFileService.delete(file.getUploadFilesId());
                }
            }
            sentMailHistoryDAO.delete(sentMail);
        }
    }

    public void deleteFileBelongToMail(String messageId){
        List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
        for(int i = 0, n = files.size(); i < n; i++){
            AttachmentFile fileDoc = files.get(i);
            File file = new File(fileDoc.getStoragePath());
            try {
                if(file.isDirectory()){
                    FileUtils.cleanDirectory(file);
                }
                File parrent = file.getParentFile();
                file.delete();
                if(parrent.isDirectory() && parrent.listFiles().length==0){
                    FileUtils.deleteDirectory(parrent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            fileDAO.delete(fileDoc.getId());
        }
    }
}
