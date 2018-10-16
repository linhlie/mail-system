package io.owslab.mailreceiver.service.transaction;

import io.owslab.mailreceiver.exception.EmailAccountException;
import io.owslab.mailreceiver.exception.EmailException;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.service.file.AttachmentFileService;
import io.owslab.mailreceiver.service.mail.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class EmailTransaction {
    @Autowired
    EmailService emailService;

    @Autowired
    AttachmentFileService attachmentFileService;

    @Transactional(propagation = Propagation.REQUIRES_NEW,  rollbackFor = EmailException.class)
    public void saveEmaiTransaction(Email email, List<AttachmentFile> attachmentFiles, boolean hasAttachments )throws EmailException{
        saveEmail(email);
        if(hasAttachments) saveFiles(attachmentFiles);
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public void saveEmail(Email email)throws EmailException{
        emailService.save(email);
    }

    @Transactional(propagation = Propagation.MANDATORY )
    public void saveFiles(List<AttachmentFile> attachmentFiles)throws EmailException{
        attachmentFileService.save(attachmentFiles);
    }

}
