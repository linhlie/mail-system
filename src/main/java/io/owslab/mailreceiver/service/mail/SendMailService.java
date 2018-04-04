package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

/**
 * Created by khanhlvb on 4/4/18.
 */
@Service
public class SendMailService {

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private EmailAccountSettingService emailAccountSettingService;

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

        final String username = accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0 ? accountSetting.getUserName() : from;
        final String password = accountSetting.getPassword();

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", accountSetting.getMailServerAddress());
        props.put("mail.smtp.port", accountSetting.getMailServerPort());

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
                    InternetAddress.parse(form.getReceiver()));

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

            List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(form.getMessageId(), false);
            for (AttachmentFile attachmentFile : files){
                MimeBodyPart attachmentPart = new MimeBodyPart();
                String fullFilename = attachmentFile.getStoragePath() + "/" + attachmentFile.getFileName();
                DataSource source = new FileDataSource(fullFilename);
                attachmentPart.setDataHandler(new DataHandler(source));
                String filename = MimeUtility.encodeText(attachmentFile.getFileName(), "UTF-8", null);
                attachmentPart.setFileName(filename);
                multipart.addBodyPart(attachmentPart);
            }

            // Send the complete message parts
            message.setContent(multipart);

            // Send message
            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
