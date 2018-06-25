package io.owslab.mailreceiver.job;

import antlr.StringUtils;
import com.mariten.kanatools.KanaConverter;
import com.sun.mail.iap.Argument;
import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.*;
import com.vdurmont.emoji.EmojiParser;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FetchMailErroDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.model.*;
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
    private final FetchMailErroDAO fetchMailErroDAO;
    private IMAPFolder emailFolder;
    private int openFolderFlag;

    private static final Logger logger = LoggerFactory.getLogger(IMAPFetchMailJob.class);

    public IMAPFetchMailJob(EmailAccountSetting accountSetting, EmailAccount account) {
        this.emailDAO = BeanUtil.getBean(EmailDAO.class);
        this.fileDAO = BeanUtil.getBean(FileDAO.class);
        this.fetchMailErroDAO = BeanUtil.getBean(FetchMailErroDAO.class);
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
            String msgnum = lastEmail.getMessageNumber();
            check(account, accountSetting, Integer.parseInt(msgnum) + 1);
        } else {
            check(account, accountSetting, 1);
        }
    }

    private SearchTerm buildSearchTerm(Date fromDate){
        if(fromDate != null) {
            ReceivedDateTerm minDateTerm = new ReceivedDateTerm(ComparisonTerm.GE, fromDate);
            return new AndTerm(minDateTerm, minDateTerm);
        }
        return null;
    }

    private Store createStore(EmailAccountSetting account) throws NoSuchProviderException {
        Properties properties = new Properties();
        properties.put("mail.imap.host", account.getMailServerAddress());
        properties.put("mail.imap.port", account.getMailServerPort());
        properties.put("mail.imap.starttls.enable", "true");
//        properties.put("mail.debug", "true");
        Session emailSession = Session.getDefaultInstance(properties);
//        emailSession.setDebug(true);
        Store store = emailSession.getStore("imaps");
        return store;
    }

    public void check(EmailAccount account, EmailAccountSetting accountSetting, int msgnum)
    {
        try {

            Store store = createStore(accountSetting);
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                store.connect(accountSetting.getMailServerAddress(), accountSetting.getUserName(), accountSetting.getPassword());
            } else {
                store.connect(accountSetting.getMailServerAddress(), account.getAccount(), accountSetting.getPassword());
            }

            //create the folder object and open it
            emailFolder = (IMAPFolder) store.getFolder("INBOX");
            boolean keepMailOnMailServer = enviromentSettingService.getKeepMailOnMailServer();
            boolean isDeleteOldMail = enviromentSettingService.getDeleteOldMail();
            Date beforeDate = null;
            if(isDeleteOldMail){
                Date now = new Date();
                int beforeDayRange = enviromentSettingService.getDeleteAfter();
                beforeDate = Utils.addDayToDate(now, -beforeDayRange);
            }
            openFolderFlag = keepMailOnMailServer ? Folder.READ_ONLY : Folder.READ_WRITE;
            emailFolder.open(openFolderFlag);

            OwsMimeMessage messages[] = getMessages(emailFolder, msgnum,beforeDate);
            logger.info("Must start fetch mail: " + messages.length + " mails");
            logger.info("start fetchEmail");
            mailProgress.setTotal(messages.length);
            for (int i = 0; i < messages.length; i++) {
                OwsMimeMessage message = (OwsMimeMessage) messages[i];
                fetchEmail(message, i);
            }

            if(!keepMailOnMailServer){
                for (int i = 0; i < messages.length; i++) {
                    Message message = messages[i];
                    message.setFlag(Flags.Flag.DELETED, true);
                }
            }
            //close the store and folder objects
            logger.info("start close mail folder");
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

    private void fetchEmail(OwsMimeMessage message, int index) {
        try{
            try {
                if(isEmailExist(message, account)) {
                    mailProgress.decreaseTotal();
                    logger.info("[" + index + "] mail exist: " + message.getSubject() + " | " + message.getReceivedDate());
                    return;
                }
                Email email = buildInitReceivedMail(message, account);
                emailDAO.save(email);
                try {
                    email = buildReceivedMail(message, email);
                    boolean hasAttachments = saveFiles(message, email);
                    email.setHasAttachment(hasAttachments);
                    email = setMailContent(message, email);
                    emailDAO.save(email);
                } catch (FolderClosedException e) {
                    e.printStackTrace();
                    Email errorEmail = findOne(email.getMessageId());
                    if(errorEmail != null) {
                        String error = ExceptionUtils.getStackTrace(e);
                        errorEmail.setErrorLog(error);
                        emailDAO.save(errorEmail);
                    }
                    if (!emailFolder.isOpen()) {
                        emailFolder.open(openFolderFlag);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Email errorEmail = findOne(email.getMessageId());
                    if(errorEmail != null) {
                        String error = ExceptionUtils.getStackTrace(e);
                        errorEmail.setErrorLog(error);
                        emailDAO.save(errorEmail);
                    }
                }
                logger.info("[" + index + "] save email: " + message.getSubject());
                mailProgress.increase();
            } catch (FolderClosedException ex) {
                logger.info("[" + index + "] FolderClosedException");
                ex.printStackTrace();
                if (!emailFolder.isOpen()) {
                    emailFolder.open(openFolderFlag);
                    this.fetchEmail(message, index);
                }
            }
        } catch (Exception e) {
            mailProgress.decreaseTotal();
            e.printStackTrace();
            String error = ExceptionUtils.getStackTrace(e);
            FetchMailError fetchMailError = new FetchMailError(new Date(), error);
            fetchMailErroDAO.save(fetchMailError);
        }
    }

    private boolean isEmailExist(OwsMimeMessage message, EmailAccount account) throws MessagingException {
        String messageId = buildMessageId(message, account);
        Email email = findOne(messageId);
        return email != null;
    }

    private Email findOne(String messageId) {
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        return emailList.size() > 0 ? emailList.get(0) : null;
    }

    private Email buildInitReceivedMail(OwsMimeMessage message, EmailAccount account) throws MessagingException {
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
        email.setOriginalBody("");
        email.setOptimizedBody("");
        email.setCreatedAt(new Date());
        email.setSentAt(sentAt);
        email.setReceivedAt(receivedAt);
        return email;
    }


    public static Email buildReceivedMail(MimeMessage message, Email email) throws MessagingException {
        email.setTo(getRecipientsWithType(message, Message.RecipientType.TO));
        email.setReplyTo(getMailReplyTo(message));
        email.setBcc(getRecipientsWithType(message, Message.RecipientType.BCC));
        email.setCc(getRecipientsWithType(message, Message.RecipientType.CC));
        return email;
    }
    
    public static Email setMailContent(MimeMessage message, Email email) throws MessagingException, IOException {
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

    private static String getContentText(Part p) throws MessagingException, IOException {

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

    private static String getTextContent(Part p) throws IOException, MessagingException {
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

    private String buildMessageId(OwsMimeMessage message, EmailAccount account) throws MessagingException {
        String msgId = message.getMessageID();
        if(msgId == null) {
            msgId = message.getMessageNumber() + "-" + message.getReceivedDate().toString() + "+" + msgId;
            return account.getAccount() + "-" + msgId;
        }
        return account.getAccount() + "+" + msgId;
    }

    private static String getMailFrom(MimeMessage message) throws MessagingException {
        Address[] froms = message.getFrom();
        String fromEmail = froms == null ? "unknown" : ((InternetAddress) froms[0]).getAddress();
        return fromEmail;
    }

    private static String getMailReplyTo(MimeMessage message) throws MessagingException {
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

    private static String getRecipientsWithType(MimeMessage message, Message.RecipientType type) throws MessagingException {
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

    private boolean saveFiles(MimeMessage message, Email email) throws MessagingException, IOException {
        boolean hasAttachments = false;
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
                    String fileName = part.getFileName();
                    if(fileName.indexOf("=?") == -1) {
                        fileName = new String(fileName.getBytes("ISO-8859-1"));
                    } else {
                        fileName = MimeUtility.decodeText(part.getFileName());
                    }
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
                    hasAttachments = true;
                }
            }
        }

        return hasAttachments;
    }

    public static String normalizeDirectoryPath(String path){
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

    public static String getCurrentDateStr(){
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

    private OwsMimeMessage[] getMessages(IMAPFolder emailFolder, int start, Date beforeDate) throws MessagingException {
        int end = emailFolder.getMessageCount();
        if(end == -1 && !emailFolder.isOpen()) {
            emailFolder.open(openFolderFlag);
            end = emailFolder.getMessageCount();
        }
        if (end > 0) {
            List<OwsMimeMessage> messages = new ArrayList<>();
            int msgnum = end;
            while (true) {
                if (msgnum == 0) break;
                OwsMimeMessage message = getMessage(emailFolder, msgnum);
                boolean exist = isEmailExist(message, account);
                if(exist) {
                    break;
                }
                messages.add(0, message);
                msgnum = msgnum - 1;
            }
            return messages.toArray(new OwsMimeMessage[messages.size()]);
        } else {
            return new OwsMimeMessage[0];
        }
    }

    public static OwsMimeMessage getMessage(IMAPFolder folder, final int msgnum) throws MessagingException
    {
        return (OwsMimeMessage) folder.doCommand(new IMAPFolder.ProtocolCommand()
        {
            public Object doCommand(IMAPProtocol protocol) throws ProtocolException
            {
                OwsMimeMessage mm = null;
                Response[] r = protocol.command("FETCH "  + Integer.toString(msgnum) + " (INTERNALDATE BODY.PEEK[])", null);
                Response response = r[r.length - 1];
                if (!response.isOK()) {
                    throw new ProtocolException("Unable to retrieve message " + msgnum);
                }
                Properties props = new Properties();
                props.setProperty("mail.store.protocol", "imap");
                props.setProperty("mail.mime.base64.ignoreerrors", "true");
                props.setProperty("mail.imap.partialfetch", "false");
                props.setProperty("mail.imaps.partialfetch", "false");
                Session session = Session.getInstance(props, null);

                FetchResponse fetchResponse = (FetchResponse) r[0];
                BODY body = (BODY) fetchResponse.getItem(com.sun.mail.imap.protocol.BODY.class);
                INTERNALDATE internaldate = (INTERNALDATE) fetchResponse.getItem(INTERNALDATE.class);
                ByteArrayInputStream is = body.getByteArrayInputStream();
                try {
                    mm = new OwsMimeMessage(new MimeMessage(session, is), msgnum, internaldate.getDate());
                    String subject = mm.getSubject();
                    if(subject != null) {
                        int index = subject.indexOf("=?UTF-8?B?");
                        if(index > -1) {
                            try {
                                subject = new String(subject.substring(0,index).getBytes("ISO-8859-1"))
                                        + MimeUtility.decodeText(subject.substring(index));
                                mm.setSubject(subject);
                            } catch (Exception e) {
                                ;
                            }
                        }
                    }
                } catch (MessagingException e) {
                    throw new ProtocolException("doCommand error", e);
                }
                return mm;
            }
        });
    }

    public static class OwsMimeMessage extends MimeMessage {
        private Date receivedDate;

        public OwsMimeMessage(MimeMessage source, int msgnum, Date receivedDate) throws MessagingException {
            super(source);
            this.msgnum = msgnum;
            this.receivedDate = receivedDate;
        }

        @Override
        public Date getReceivedDate() {
            return receivedDate;
        }
    }
}
