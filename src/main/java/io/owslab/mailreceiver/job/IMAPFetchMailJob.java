package io.owslab.mailreceiver.job;

import antlr.StringUtils;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;
import io.owslab.mailreceiver.protocols.ReceiveMailProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class IMAPFetchMailJob implements Runnable {
    public static final boolean USING_POP3 = false;

    private final EmailDAO emailDAO;

    private final ReceiveEmailAccountSetting account;
    private static final Logger logger = LoggerFactory.getLogger(IMAPFetchMailJob.class);

    public IMAPFetchMailJob(EmailDAO emailDAO, ReceiveEmailAccountSetting account) {
        this.emailDAO = emailDAO;
        this.account = account;
    }

    @Override
    public void run() {
        start();
    }

    private void start(){
        List<Email> listEmail = emailDAO.findByAccountIdOrderBySentAtDesc(account.getId());
        int n = listEmail.size();
        if(n > 0){
            Email lastEmail = listEmail.get(0);
            Date lastEmailSentAt = lastEmail.getSentAt();
            check(account, lastEmailSentAt);
        } else {
            check(account, null);
        }
    }

    private SearchTerm buildSearchTerm(Date fromDate){
        Flags seen = new Flags(Flags.Flag.SEEN);
        SearchTerm searchTerm = new FlagTerm(seen, false);
        if(fromDate != null) {
            SentDateTerm minDateTerm = new SentDateTerm(ComparisonTerm.GE, fromDate);
            searchTerm = new AndTerm(searchTerm, minDateTerm);
        }
        return searchTerm;
    }

    private Store createStore(ReceiveEmailAccountSetting account) throws NoSuchProviderException {
        Properties properties = new Properties();
        properties.put("mail.imap.host", account.getMailServerAddress());
        properties.put("mail.imap.port", account.getMailServerPort());
        properties.put("mail.imap.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore("imaps");
        return store;
    }

    public void check(ReceiveEmailAccountSetting account, Date fromDate)
    {
        try {

            Store store = createStore(account);

            store.connect(account.getMailServerAddress(), account.getAccount(), account.getPassword());

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_ONLY);

            SearchTerm searchTerm = buildSearchTerm(fromDate);
            Message messages[] = emailFolder.search(searchTerm);

            System.out.println("messages.length---" + messages.length);
            fetchEmail(messages);
            //close the store and folder objects
            emailFolder.close(false);
            store.close();

        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fetchEmail(Message[] messages) {
        for (int i = 0, n = messages.length; i < n; i++) {
            try {
                MimeMessage message = (MimeMessage) messages[i];
                if(isEmailExist(message, account)) {
                    System.out.println("mail exist");
                    continue;
                }
                System.out.println(message.getSubject());
                Email email = buildReceivedMail(message, account);
                emailDAO.save(email);
            } catch (Exception e) {
//                e.printStackTrace();
            }
        }
    }

    private boolean isEmailExist(MimeMessage message, ReceiveEmailAccountSetting account) throws MessagingException {
        String messageId = buildMessageId(message, account);
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        return emailList.size() > 0;
    }

    private Email buildReceivedMail(MimeMessage message, ReceiveEmailAccountSetting account) {
        try {
            Email email =  new Email();
            String messageId = buildMessageId(message, account);
            email.setMessageId(messageId);
            email.setAccountId(account.getId());
            email.setFrom(getMailFrom(message));
            email.setSubject(message.getSubject());
            email.setTo(getRecipientsWithType(message, Message.RecipientType.TO));
            email.setSentAt(message.getSentDate());

            email.setReplyTo(getMailReplyTo(message));
            email.setReceivedAt(message.getReceivedDate());
            email.setCreatedAt(new Date());
            email.setBcc(getRecipientsWithType(message, Message.RecipientType.BCC));
            email.setCc(getRecipientsWithType(message, Message.RecipientType.CC));
            String originalContent = getContentText(message);
            if(originalContent != null){
                email.setOriginalBody(originalContent);
                email.setOptimizedBody(originalContent.toLowerCase()); //TODO: optimize japanese characters
            }
            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getContentText(Part p) throws MessagingException, IOException {

        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getContentText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getContentText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getContentText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getContentText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return null;
    }

    private String buildMessageId(MimeMessage message, ReceiveEmailAccountSetting account) throws MessagingException {
        return account.getAccount() + "+" + message.getMessageID();
    }

    private String getMailFrom(MimeMessage message) throws MessagingException {
        Address[] froms = message.getFrom();
        String fromEmail = froms == null ? "unknown" : ((InternetAddress) froms[0]).getAddress();
        return fromEmail;
    }

    private String getMailReplyTo(MimeMessage message) throws MessagingException {
        Address[] repplyTos = message.getReplyTo();
        String repplyTo = repplyTos == null ? getMailFrom(message) : ((InternetAddress) repplyTos[0]).getAddress();
        return repplyTo;
    }

    private String getRecipientsWithType(MimeMessage message, Message.RecipientType type) throws MessagingException {
        List<String> recipientAddresses = new ArrayList<>();
        Address[] recipients = message.getRecipients(type);
        if (recipients == null || recipients.length == 0) return  null;
        for (Address address : recipients) {
            if (address instanceof InternetAddress) {
                InternetAddress ia = (InternetAddress) address;
                recipientAddresses.add(ia.toUnicodeString());
            } else {
                recipientAddresses.add(address.toString());
            }
        }
        String recipientAddressesStr = String.join(";", recipientAddresses);
        return recipientAddressesStr;
    }
}
