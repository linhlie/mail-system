package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dao.UploadFileDAO;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.file.UploadFileService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import org.apache.commons.lang.ArrayUtils;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
/**
 * Created by khanhlvb on 4/4/18.
 */
@Service
public class SendMailService {

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
    private UploadFileService uploadFileService;

    public void sendMail(SendMailForm form){
        Email email = emailService.findOne(form.getMessageId(), false);
        if(email == null) return;
        long accountid = email.getAccountId();
        List<EmailAccount> emailAccounts = mailAccountsService.findById(accountid);
        EmailAccount account = emailAccounts.size() > 0 ? emailAccounts.get(0) : null;
        if(account == null) return;
        EmailAccountSetting accountSetting = emailAccountSettingService.findOneSend(account.getId());
        if(accountSetting == null) return;

        // Sender's email ID needs to be mentioned
        String from = account.getAccount();
        String to = form.getReceiver();
        String cc = form.getCc();
        String replyTo = email.getReplyTo();

        final String username = accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0 ? accountSetting.getUserName() : from;
        final String password = accountSetting.getPassword();

        //TODO using dynamic setting from DB
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", accountSetting.getMailServerAddress());
        props.put("mail.smtp.port", accountSetting.getMailServerPort());

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
            if(originAttachment != null && originAttachment.size() > 0) {
                List<AttachmentFile> files = fileDAO.findByIdInAndDeleted(originAttachment, false);
                for (AttachmentFile attachmentFile : files){
                    MimeBodyPart attachmentPart = buildAttachmentPart(attachmentFile.getStoragePath(), attachmentFile.getFileName());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            if(uploadAttachment != null && uploadAttachment.size() > 0) {
                List<UploadFile> uploadFiles = uploadFileDAO.findByIdIn(uploadAttachment);
                for (UploadFile uploadFile : uploadFiles){
                    MimeBodyPart attachmentPart = buildAttachmentPart(uploadFile.getStoragePath(), uploadFile.getFileName());
                    multipart.addBodyPart(attachmentPart);
                }
            }

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);
            uploadFileService.delete(uploadAttachment);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MimeBodyPart buildAttachmentPart(String storagePath, String fileName) throws MessagingException, IOException {
        MimeBodyPart attachmentPart = new MimeBodyPart();
        String fullFilename = normalizeDirectoryPath(storagePath) + File.separator + fileName;
        System.out.println("buildAttachmentPart: " + fullFilename + " | " + fileName);
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
}
