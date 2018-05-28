package io.owslab.mailreceiver.job;

import antlr.StringUtils;
import com.mariten.kanatools.KanaConverter;
import com.vdurmont.emoji.EmojiParser;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;
import io.owslab.mailreceiver.service.BeanUtil;
import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.utils.Html2Text;
import io.owslab.mailreceiver.utils.Utils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.hibernate.annotations.Fetch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.search.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

public class IMAPFetchMailJob implements Runnable {
    public static final boolean USING_POP3 = false;

    private final EmailDAO emailDAO;
    private final FileDAO fileDAO;
    private final EnviromentSettingService enviromentSettingService;
    private final FetchMailsService fetchMailsService;
    private final EmailAccountSetting accountSetting;
    private final EmailAccount account;
    private final FetchMailsService.FetchMailProgress mailProgress;

    private static final Logger logger = LoggerFactory.getLogger(IMAPFetchMailJob.class);

    public IMAPFetchMailJob(EmailAccountSetting accountSetting, EmailAccount account) {
        this.emailDAO = BeanUtil.getBean(EmailDAO.class);
        this.fileDAO = BeanUtil.getBean(FileDAO.class);
        this.enviromentSettingService = BeanUtil.getBean(EnviromentSettingService.class);
        this.fetchMailsService = BeanUtil.getBean(FetchMailsService.class);
        this.accountSetting = accountSetting;
        this.account = account;
        this.mailProgress = this.fetchMailsService.getMailProgressInstance();
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
            Date lastEmailReceivedAtAt = lastEmail.getReceivedAt();
            if(lastEmailReceivedAtAt == null){
                lastEmailReceivedAtAt = lastEmail.getSentAt();
            }
            check(account, accountSetting, lastEmailReceivedAtAt);
        } else {
            check(account, accountSetting, null);
        }
    }

    private SearchTerm buildSearchTerm(Date fromDate){
//        Flags seen = new Flags(Flags.Flag.SEEN);
//        SearchTerm searchTerm = new FlagTerm(seen, false);
        if(fromDate != null) {
            ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, fromDate);
            return minDateTerm;
        }
        return null;
    }

    private Store createStore(EmailAccountSetting account) throws NoSuchProviderException {
        Properties properties = new Properties();
        properties.put("mail.imap.host", account.getMailServerAddress());
        properties.put("mail.imap.port", account.getMailServerPort());
        properties.put("mail.imap.starttls.enable", "true");
        Session emailSession = Session.getDefaultInstance(properties);
        Store store = emailSession.getStore("imaps");
        return store;
    }

    public void check(EmailAccount account, EmailAccountSetting accountSetting, Date fromDate)
    {
        try {

            Store store = createStore(accountSetting);
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                store.connect(accountSetting.getMailServerAddress(), accountSetting.getUserName(), accountSetting.getPassword());
            } else {
                store.connect(accountSetting.getMailServerAddress(), account.getAccount(), accountSetting.getPassword());
            }

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            boolean keepMailOnMailServer = enviromentSettingService.getKeepMailOnMailServer();
            boolean isDeleteOldMail = enviromentSettingService.getDeleteOldMail();
            if(isDeleteOldMail){
                Date now = new Date();
                int beforeDayRange = enviromentSettingService.getDeleteAfter();
                Date beforeDate = Utils.addDayToDate(now, -beforeDayRange);
                if(fromDate != null) {
                    if(fromDate.compareTo(beforeDate) < 0){
                        fromDate = beforeDate;
                    }
                } else {
                    fromDate = beforeDate;
                }
            }
            int openFolderFlag = keepMailOnMailServer ? Folder.READ_ONLY : Folder.READ_WRITE;
            emailFolder.open(openFolderFlag);

            SearchTerm searchTerm = buildSearchTerm(fromDate);
            Message messages[] = searchTerm == null ? emailFolder.getMessages() : emailFolder.search(searchTerm);

            fetchEmail(messages);

            if(!keepMailOnMailServer){
                for (int i = 0; i < messages.length; i++) {
                    Message message = messages[i];
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }
            //close the store and folder objects
            emailFolder.close(true);
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
        logger.info("start fetchEmail");
        mailProgress.setTotal(messages.length);
        for (int i = 0, n = messages.length; i < n; i++) {
            try {
                MimeMessage message = (MimeMessage) messages[i];
                if(isEmailExist(message, account)) {
                    mailProgress.decreaseTotal();
                    continue;
                }
                Email email = buildReceivedMail(message, account);
                emailDAO.save(email);
                try {
                    saveFiles(message, email);
                    email = setMailContent(message, email);
                    emailDAO.save(email);
                } catch (Exception e) {
                    e.printStackTrace();
                    Email errorEmail = findOne(email.getMessageId());
                    if(errorEmail != null) {
                        String error = ExceptionUtils.getStackTrace(e);
                        errorEmail.setErrorLog(error);
                        emailDAO.save(errorEmail);
                    }
                }
                logger.info("save email: " + message.getSubject());
                mailProgress.increase();
            } catch (Exception e) {
                mailProgress.decreaseTotal();
                e.printStackTrace();
            }
        }
        logger.info("stop fetchEmail");
    }

    private boolean isEmailExist(MimeMessage message, EmailAccount account) throws MessagingException {
        String messageId = buildMessageId(message, account);
        Email email = findOne(messageId);
        return email != null;
    }

    private Email findOne(String messageId) {
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        return emailList.size() > 0 ? emailList.get(0) : null;
    }

    private Email buildReceivedMail(MimeMessage message, EmailAccount account) {
        try {
            Email email =  new Email();
            int messageNumber = message.getMessageNumber();
            String messageId = buildMessageId(message, account);
            Date sentAt = message.getSentDate();
            Date receivedAt = message.getReceivedDate();
            sentAt = sentAt != null ? sentAt : receivedAt;
            email.setMessageNumber(Integer.toString(messageNumber));
            email.setMessageId(messageId);
            email.setAccountId(account.getId());
            email.setFrom(getMailFrom(message));
            email.setSubject("");
            email.setTo(getRecipientsWithType(message, Message.RecipientType.TO));
            email.setSentAt(sentAt);
            email.setReplyTo(getMailReplyTo(message));
            email.setReceivedAt(receivedAt);
            email.setCreatedAt(new Date());
            email.setBcc(getRecipientsWithType(message, Message.RecipientType.BCC));
            email.setCc(getRecipientsWithType(message, Message.RecipientType.CC));
            email.setHasAttachment(hasAttachments(message));
            email.setOriginalBody("");
            email.setOptimizedBody("");
            return email;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    private Email setMailContent(MimeMessage message, Email email) throws MessagingException, IOException {
        String subject = message.getSubject();
        subject = subject != null ? subject : "null";
        subject = EmojiParser.removeAllEmojis(subject);
        email.setSubject(subject);
        String originalContent = getContentText(message);
        originalContent = originalContent != null ? originalContent : "";
        originalContent = EmojiParser.removeAllEmojis(originalContent);
        email.setOriginalBody(originalContent);
        String beforeOptimizeContent = originalContent;
        String optimizedContent = MailBoxService.optimizeText(beforeOptimizeContent);
        email.setOptimizedBody(optimizedContent);
        return email;
    }

    private String getContentText(Part p) throws MessagingException, IOException {

        if (p.isMimeType("text/*")) {
            String s = getTextContent(p);
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

    private String getTextContent(Part p) throws IOException, MessagingException {
        try {
            return (String)p.getContent();
        } catch (UnsupportedEncodingException e) {
            OutputStream os = new ByteArrayOutputStream();
            p.writeTo(os);
            String raw = os.toString();
            os.close();

            //cp932 -> Windows-31J
            raw = raw.replaceAll("cp932", "ms932");

            InputStream is = new ByteArrayInputStream(raw.getBytes());
            Part newPart = new MimeBodyPart(is);
            is.close();

            return (String)newPart.getContent();
        }
    }

    private String buildMessageId(MimeMessage message, EmailAccount account) throws MessagingException {
        return account.getAccount() + "+" + message.getMessageID();
    }

    private String getMailFrom(MimeMessage message) throws MessagingException {
        Address[] froms = message.getFrom();
        String fromEmail = froms == null ? "unknown" : ((InternetAddress) froms[0]).getAddress();
        return fromEmail;
    }

    private String getMailReplyTo(MimeMessage message) throws MessagingException {
        Address[] replyTos = message.getReplyTo();
        String replyTo;
        if(replyTos != null && replyTos.length > 0) {
            replyTo = ((InternetAddress) replyTos[0]).getAddress();
            if(replyTos.length > 1) {
                for(int i = 1; i < replyTos.length; i++){
                    replyTo = replyTo + ", ";
                    replyTo = replyTo + ((InternetAddress) replyTos[i]).getAddress();
                }
            }
        } else {
            replyTo = getMailFrom(message);
        }
        return replyTo;
    }

    private String getRecipientsWithType(MimeMessage message, Message.RecipientType type) throws MessagingException {
        List<String> recipientAddresses = new ArrayList<>();
        Address[] recipients = message.getRecipients(type);
        if (recipients == null || recipients.length == 0) return  "";
        for (Address address : recipients) {
            if (address instanceof InternetAddress) {
                InternetAddress ia = (InternetAddress) address;
                recipientAddresses.add(ia.getAddress());
            }
        }
        String recipientAddressesStr = String.join(", ", recipientAddresses);
        return recipientAddressesStr == null ? "" : recipientAddressesStr;
    }

    private boolean hasAttachments(Message msg) throws MessagingException, IOException {
        if (msg.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart)msg.getContent();
            if (mp.getCount() > 1)
                return true;
        }
        return false;
    }

    private void saveFiles(MimeMessage message, Email email) throws MessagingException, IOException {
        String contentType = message.getContentType();
        if (contentType.contains("multipart")) {
            // content may contain attachments
            Multipart multiPart = (Multipart) message.getContent();
            int numberOfParts = multiPart.getCount();
            for (int partCount = 0; partCount < numberOfParts; partCount++) {
                //TODO: try catch if fails or transaction
                MimeBodyPart part = (MimeBodyPart) multiPart.getBodyPart(partCount);
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = MimeUtility.decodeText(part.getFileName());
                    String saveDirectoryPath = enviromentSettingService.getStoragePath();
                    String currentDateStr = getCurrentDateStr();
                    saveDirectoryPath = normalizeDirectoryPath(saveDirectoryPath) + "/" + currentDateStr;
                    File saveDirectory = new File(saveDirectoryPath);
                    if (!saveDirectory.exists()){
                        saveDirectory.mkdir();
                    }
                    saveDirectoryPath = normalizeDirectoryPath(saveDirectoryPath) + "/" + email.getMessageId().hashCode();
                    saveDirectory = new File(saveDirectoryPath);
                    if (!saveDirectory.exists()){
                        saveDirectory.mkdir();
                    }
                    File file = new File(saveDirectoryPath + File.separator + fileName);
                    logger.info("Start Save file: " + fileName + " " + file.length());
                    part.saveFile(file);
                    AttachmentFile attachmentFile = new AttachmentFile(
                            email.getMessageId(),
                            fileName,
                            saveDirectoryPath,
                            new Date(),
                            null,
                            file.length()
                    );
                    logger.info("Save file: " + attachmentFile.toString());
                    fileDAO.save(attachmentFile);
                }
            }
        }
    }

    private String normalizeDirectoryPath(String path){
        if (path != null && path.length() > 0 && path.charAt(path.length() - 1) == '/') {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String getTextFromMessage(Message message) throws MessagingException, IOException {
        String result = "";
        if (message.isMimeType("text/plain")) {
            result = message.getContent().toString();
        } else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }

    private String getTextFromMimeMultipart(
            MimeMultipart mimeMultipart)  throws MessagingException, IOException{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

    private String getCurrentDateStr(){
        String currentDateStr = "";
        LocalDateTime now = LocalDateTime.now();
        String year = Integer.toString(now.getYear());
        String month = Integer.toString(now.getMonthValue());
        if(month.length() == 1) month = "0" + month;
        String day = Integer.toString(now.getDayOfMonth());
        if(day.length() == 1) day = "0" + day;
        currentDateStr = currentDateStr + year;
        currentDateStr = currentDateStr + "-" + month;
        currentDateStr = currentDateStr + "-" + day;
        return currentDateStr;
    }
}
