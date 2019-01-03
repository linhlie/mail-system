package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.SentMailHistoryDAO;
import io.owslab.mailreceiver.dao.UploadFileDAO;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.form.SendMultilMailForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.file.SentMailFileService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.statistics.ClickHistoryService;
import io.owslab.mailreceiver.utils.MailUtils;
import io.owslab.mailreceiver.utils.Utils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
/**
 * Created by khanhlvb on 4/4/18.
 */
@Service
public class SendMailService {

    private static final Logger logger = LoggerFactory.getLogger(SendMailService.class);

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private UploadFileDAO uploadFileDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private EmailAccountSettingService emailAccountSettingService;

    @Autowired
    private SentMailHistoryDAO sentMailHistoryDAO;

    @Autowired
    private EnviromentSettingService enviromentSettingService;

    @Autowired
    private ClickHistoryService clickHistoryService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SentMailFileService sentMailFileService;

    public void sendMail(SendMailForm form){
        Email email = emailService.findOne(form.getMessageId());
        if(email == null) return;
        String formAccountId = form.getAccountId();
        long accountId = formAccountId != null ? Long.parseLong(formAccountId) : email.getAccountId();
        List<EmailAccount> emailAccounts = mailAccountsService.findById(accountId);
        EmailAccount account = emailAccounts.size() > 0 ? emailAccounts.get(0) : null;
        if(account == null) return;
        EmailAccountSetting accountSetting = emailAccountSettingService.findOneSend(account.getId());
        boolean debugOn = enviromentSettingService.getDebugOn();
        if(debugOn){
            accountSetting = emailAccountSettingService.findOneSendByEmail("ows-test@world-link-system.com");
        }
        if(accountSetting == null) return;

        Email matchingEmail = null;
        if(form.getMatchingMessageId() != null) {
            matchingEmail = emailService.findOne(form.getMatchingMessageId());
        }

        // Sender's email ID needs to be mentioned
        String from = account.getAccount();
        String to = form.getReceiver();
        String cc = form.getCc();
        if(debugOn){
            to = enviromentSettingService.getDebugReceiveMailAddress();
            cc = "";
            from = "ows-test@world-link-system.com";
        }
        String replyTo = email.getReplyTo();

        final String username = accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0 ? accountSetting.getUserName() : from;
        final String password = accountSetting.getPassword();

        Properties props = MailUtils.buildProperties(accountSetting);

        to = selfEliminateDuplicates(to);
        cc = selfEliminateDuplicates(cc);

        cc = eliminateDuplicates(cc, to);

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {
            String encodingOptions = "text/html; charset=UTF-8";
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);
            message.setHeader("Content-Type", encodingOptions);
            // Set From: header field of the header.
            message.setFrom(new InternetAddress(from));

            // Set To: header field of the header.
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));

            //TODO: handle reply to group, save group and References for make complete Reply/ReplyAll;
//            a = this.getRecipients(MimeMessage.RecipientType.NEWSGROUPS);
//            if(a != null && a.length > 0) {
//                reply.setRecipients(MimeMessage.RecipientType.NEWSGROUPS, (Address[])a);
//            }

            String originMessageIdWrapped = email.getMessageId();
            String[] originMessageIdWrappedArray = originMessageIdWrapped.split("\\+");
            originMessageIdWrappedArray = (String[]) ArrayUtils.remove(originMessageIdWrappedArray, 0);
            String originMessageId = String.join("",originMessageIdWrappedArray);
            message.setHeader("In-Reply-To", originMessageId);

            // Set Subject: header field
            message.setSubject(form.getSubject(), "UTF-8");

            // Create the message part
            BodyPart messageBodyPart = new MimeBodyPart();

            // Now set the actual message
            messageBodyPart.setContent(form.getContent(), encodingOptions);

            // Create a multipar message
            Multipart multipart = new MimeMultipart();

            // Set text message part
            multipart.addBodyPart(messageBodyPart);

            List<Long> originAttachment = form.getOriginAttachment();
            List<Long> uploadAttachment = form.getUploadAttachment();
            boolean hasAttachment = false;
            if(originAttachment != null && originAttachment.size() > 0) {
                List<AttachmentFile> files = fileDAO.findByIdInAndDeleted(originAttachment, false);
                for (AttachmentFile attachmentFile : files){
                    MimeBodyPart attachmentPart = buildAttachmentPart(attachmentFile.getStoragePath(), attachmentFile.getFileName());
                    multipart.addBodyPart(attachmentPart);
                    hasAttachment = true;
                }
            }
            List<Long> uploadFileReality = new ArrayList<>();
            if(uploadAttachment != null && uploadAttachment.size() > 0) {
                List<UploadFile> uploadFiles = uploadFileDAO.findByIdIn(uploadAttachment);
                for (UploadFile uploadFile : uploadFiles){
                    uploadFileReality.add(uploadFile.getId());
                    MimeBodyPart attachmentPart = buildUploadAttachmentPart(uploadFile.getStoragePath(), uploadFile.getFileName());
                    multipart.addBodyPart(attachmentPart);
                    hasAttachment = true;
                }
            }

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            logger.info("Send email from "+from+" to "+to);
            SentMailHistory sentMail = saveSentMailHistory(email, matchingEmail, account, to, cc, null, replyTo, form, hasAttachment);
            if(sentMail!=null){
                sentMailFileService.saveSentMailFiles(uploadFileReality, sentMail.getId());
            }
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MimeBodyPart buildAttachmentPart(String storagePath, String fileName) throws MessagingException, UnsupportedEncodingException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        String fullFilename = normalizeDirectoryPath(storagePath);
        DataSource source = new FileDataSource(fullFilename);
        attachmentPart.setDataHandler(new DataHandler(source));
        String filename = MimeUtility.encodeText(fileName, "UTF-8", null);
        attachmentPart.setFileName(filename);
        return attachmentPart;
    }

    private MimeBodyPart buildUploadAttachmentPart(String storagePath, String fileName) throws MessagingException, IOException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        String fullFilename = normalizeDirectoryPath(storagePath) + File.separator + fileName;
        Path path = Paths.get(fullFilename);
        InputStream in = Files.newInputStream(path);
        DataSource source = new ByteArrayDataSource(in, Files.probeContentType(path));
        attachmentPart.setDataHandler(new DataHandler(source));
        String filename = MimeUtility.encodeText(fileName, "UTF-8", null);
        attachmentPart.setFileName(filename);
        return attachmentPart;
    }
    
    private String normalizeDirectoryPath(String path){
        if (path != null && path.length() > 0 && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String selfEliminateDuplicates(String raw) {
        String[] emails = raw.split(",");
        return String.join(",", new HashSet<String>(Arrays.asList(emails)));
    }

    private String eliminateDuplicates(String origin, String reference) {
        String[] emails = origin.split(",");
        List<String> refList = Arrays.asList(reference.split(","));
        List<String> result = new ArrayList<>();
        for(String email : emails){
            if(!refList.contains(email)){
                result.add(email);
            }
        }
        return String.join(",", result);
    }

    private SentMailHistory saveSentMailHistory(Email originalMail, Email matchingMail, EmailAccount emailAccount, String to, String cc, String bcc, String replyTo, SendMailForm form, boolean hasAttachment) {
        int sentType = form.getHistoryType();
        clickHistoryService.saveSent(sentType);
        String keepSentMailHistoryDay = enviromentSettingService.getKeepSentMailHistoryDay();
        if(keepSentMailHistoryDay != null && keepSentMailHistoryDay.length() > 0 && Integer.parseInt(keepSentMailHistoryDay) == 0) return null;
        long accountSentMailId = accountService.getLoggedInAccountId();
        SentMailHistory history = new SentMailHistory(originalMail, matchingMail, emailAccount, to, cc, bcc, replyTo, form, hasAttachment, accountSentMailId);
        return sentMailHistoryDAO.save(history);
    }

    public void sendReportMail(ReportErrorService.ReportErrorParams report) {
        EmailAccountSetting accountSetting = emailAccountSettingService.findOneSendByEmail(report.getUserName());
        Properties props = MailUtils.buildProperties(accountSetting);

        // Get the Session object.
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(report.getUserName(), report.getPassword());
                    }
                });
        String from = report.getFrom();
        String to = report.getTo();
        String subject = "[e!Helper] - SYSTEM ERROR REPORT @ " + Utils.formatGMT2(new Date());
        String content = report.getContent();
        String cc = report.getCc();
        cc = selfEliminateDuplicates(cc);
        cc = eliminateDuplicates(cc, to);
        try {
            String encodingOptions = "text/html; charset=UTF-8";
            MimeMessage message = new MimeMessage(session);
            message.setHeader("Content-Type", encodingOptions);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));
            message.setRecipients(Message.RecipientType.CC,
                    InternetAddress.parse(cc));
            message.setSubject(subject, "UTF-8");
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(content, encodingOptions);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);
            message.setContent(multipart);
            Transport.send(message);
            logger.info("Send email warning from "+from+" to "+to);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
