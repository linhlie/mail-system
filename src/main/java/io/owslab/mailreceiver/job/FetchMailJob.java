package io.owslab.mailreceiver.job;


import com.sun.mail.iap.ProtocolException;
import com.sun.mail.iap.Response;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.protocol.BODY;
import com.sun.mail.imap.protocol.FetchResponse;
import com.sun.mail.imap.protocol.IMAPProtocol;
import com.sun.mail.imap.protocol.INTERNALDATE;
import com.vdurmont.emoji.EmojiParser;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FetchMailErroDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.BeanUtil;
import io.owslab.mailreceiver.service.mail.FetchMailsService;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.errror.ReportErrorService;
import io.owslab.mailreceiver.service.transaction.EmailTransaction;
import io.owslab.mailreceiver.utils.MailUtils;
import io.owslab.mailreceiver.utils.Utils;
import jp.co.worksap.message.decoder.HeaderDecoder;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

public class FetchMailJob implements Runnable {

    private final EmailDAO emailDAO;
    private final FileDAO fileDAO;
    private final EmailTransaction emailTransaction;
    private final EnviromentSettingService enviromentSettingService;
    private final FetchMailsService fetchMailsService;
    private final EmailAccountSetting accountSetting;
    private final EmailAccount account;
    private final FetchMailsService.FetchMailProgress mailProgress;
    private final FetchMailErroDAO fetchMailErroDAO;
    private Folder emailFolder;
    private int openFolderFlag;

    private static final Logger logger = LoggerFactory.getLogger(FetchMailJob.class);

    public FetchMailJob(EmailAccountSetting accountSetting, EmailAccount account) {
        this.emailDAO = BeanUtil.getBean(EmailDAO.class);
        this.fileDAO = BeanUtil.getBean(FileDAO.class);
        this.emailTransaction = BeanUtil.getBean(EmailTransaction.class);
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

    public void check(EmailAccount account, EmailAccountSetting accountSetting, int msgnum)
    {
        try {
            logger.info("Connect to mail store");
            Store store = MailUtils.createStore(accountSetting);
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                store.connect(accountSetting.getMailServerAddress(), accountSetting.getUserName(), accountSetting.getPassword());
            } else {
                store.connect(accountSetting.getMailServerAddress(), account.getAccount(), accountSetting.getPassword());
            }
            logger.info("Create the INBOX folder");
            emailFolder = store.getFolder("INBOX");
            logger.info("Create the INBOX folder done");
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

            logger.info("Get message from INBOX folder");
            OwsMimeMessage messages[] = getMessages(emailFolder, msgnum, beforeDate);
            logger.info("Must start fetch mail: " + messages.length + " mails");
            logger.info("start fetchEmail");
            enviromentSettingService.saveCheckTimeFetchMail();
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
            logger.info("closed mail folder");
            store.close();
            logger.info("closed store");
            logger.info("closed all");
        } catch (Exception e) {
            String errorDetail = account.getAccount() + ": " + ExceptionUtils.getStackTrace(e);
            logger.error(errorDetail);
            ReportErrorService.reportOnlyAdministrator(errorDetail);
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
                logger.info("[" + index + "] start save email: " + message.getSubject());
                Email email = buildInitReceivedMail(message, account);
                try {
                    email = buildReceivedMail(message, email);
                    List<AttachmentFile> attachmentFiles = saveAttachments(message, email);
                    boolean hasAttachments = attachmentFiles.size() > 0;
                    email.setHasAttachment(hasAttachments);
                    email = setMailContent(message, email);
                    emailTransaction.saveEmaiTransaction(email, attachmentFiles, hasAttachments);
                } catch (Exception e) {
                    e.printStackTrace();
                    Email errorEmail = email;
                    logger.error(e.toString());
                    if(errorEmail != null) {
                        String error = ExceptionUtils.getStackTrace(e);
                        int indexError = error.indexOf("Duplicate entry");
                        if(indexError<0){
                            errorEmail.setStatus(Email.Status.ERROR_OCCURRED);
                            errorEmail.setErrorLog(error);
                            emailDAO.save(errorEmail);
                        }
                    }
                }
                mailProgress.increase();
            } catch (FolderClosedException ex) {
                logger.warn("[" + index + "] FolderClosedException");
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
            msgId = message.getMessageNumber() + "-" + message.getSentDate().toString() + "+" + msgId;
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
        try {
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
        } catch (AddressException e) {
            return "";
        }
    }

    private boolean hasAttachments(Message msg) throws MessagingException, IOException {
        if (msg.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart)msg.getContent();
            if (mp.getCount() > 1)
                return true;
        }
        return false;
    }

    public List<AttachmentFile> saveAttachments(Part part, Email email) throws Exception {
        List<AttachmentFile> attachmentFiles = new ArrayList<>();
        if (part.isMimeType("multipart/*")) {
            Multipart mp = (Multipart) part.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                MimeBodyPart mpart = (MimeBodyPart)mp.getBodyPart(i);
                String disposition = mpart.getDisposition();
                List<AttachmentFile> partFiles = new ArrayList<>();
                if ((disposition != null)
                        && ((disposition.equals(Part.ATTACHMENT)) || (disposition
                        .equals(Part.INLINE)))) {
                    AttachmentFile attachmentFile = saveFile(mpart, email);
                    if(attachmentFile != null) {
                        partFiles.add(attachmentFile);
                    }
                } else if (mpart.isMimeType("multipart/*")) {
                    partFiles = saveAttachments(mpart, email);
                } else {
                    String contentType = mpart.getContentType();
                    if (contentType.indexOf("name") != -1 || contentType.indexOf("application") != -1) {
                        AttachmentFile attachmentFile = saveFile(mpart, email);
                        if(attachmentFile != null) {
                            partFiles.add(attachmentFile);
                        }
                    }
                }
                attachmentFiles.addAll(partFiles);
            }
        } else if (part.isMimeType("message/rfc822")) {
            attachmentFiles = saveAttachments((Part) part.getContent(), email);
        }

        return attachmentFiles;
    }

    private AttachmentFile saveFile(MimeBodyPart mpart, Email email) throws MessagingException, IOException {
        String fileName = mpart.getFileName();
        if(fileName == null) return null;
        if(fileName.indexOf("=?") == -1) {
            fileName = new String(fileName.getBytes("ISO-8859-1"));
        } else {
            fileName = MimeUtility.decodeText(fileName);
        }

        String saveDirectoryPath = enviromentSettingService.getStoragePath();
        String currentDateStr = getCurrentDateStr();
        saveDirectoryPath = normalizeDirectoryPath(saveDirectoryPath) + File.separator + currentDateStr;
        File saveDirectory = new File(saveDirectoryPath);
        if (!saveDirectory.exists()){
            saveDirectory.mkdir();
        }
        saveDirectoryPath = normalizeDirectoryPath(saveDirectoryPath) + File.separator + email.getMessageId().hashCode();
        saveDirectory = new File(saveDirectoryPath);
        if (!saveDirectory.exists()){
            saveDirectory.mkdir();
        }
        saveDirectoryPath = saveDirectoryPath + File.separator + getUniqueFileName();
        File file = new File(saveDirectoryPath);
        mpart.saveFile(file);
        AttachmentFile attachmentFile = new AttachmentFile(
                email.getMessageId(),
                fileName,
                saveDirectoryPath,
                new Date(),
                null,
                file.length()
        );
        logger.info("Save file: " + attachmentFile.toString());
        return  attachmentFile;
    }

    public static String getUniqueFileName() {
        return System.currentTimeMillis() + "" + UUID.randomUUID().toString();
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

    private OwsMimeMessage[] getMessages(Folder emailFolder, int start, Date beforeDate) throws MessagingException {
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
                if(beforeDate != null) {
                    Date receivedDate = message.getReceivedDate();
                    if (receivedDate.before(beforeDate)) {
                        break;
                    }
                }
                messages.add(0, message);
                msgnum = msgnum - 1;
            }
            return messages.toArray(new OwsMimeMessage[messages.size()]);
        } else {
            return new OwsMimeMessage[0];
        }
    }

    public static OwsMimeMessage getMessage(Folder folder, final int msgnum) throws MessagingException
    {
        if(folder instanceof IMAPFolder) {
            return getIMAPMessage((IMAPFolder) folder, msgnum);
        } else {
            return getRegularMessage(folder, msgnum);
        }
    }

    public static OwsMimeMessage getRegularMessage(Folder folder, final int msgnum) throws MessagingException
    {
        MimeMessage message = (MimeMessage) folder.getMessage(msgnum);
        OwsMimeMessage owsMimeMessage = new OwsMimeMessage(message, msgnum, message.getSentDate());
        return  owsMimeMessage;
    }

    public static OwsMimeMessage getIMAPMessage(IMAPFolder folder, final int msgnum) throws MessagingException
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
                    String subject = mm.getHeader("Subject", null);
                    if(subject != null) {
                        try {
                            HeaderDecoder decoder = new HeaderDecoder();
                            if(subject.startsWith("=?")) {
                                mm.setSubject(decoder.decodeSubject(subject));
                            } else {
                                int index = subject.indexOf("=?");
                                if(index > 0) {
                                    mm.setSubject(new String(subject.substring(0,index).getBytes("ISO-8859-1")) + decoder.decodeSubject(subject.substring(index)));
                                }
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
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
        private Date sentDate;

        public OwsMimeMessage(MimeMessage source, int msgnum, Date receivedDate) throws MessagingException {
            super(source);
            this.msgnum = msgnum;
            Date sentDate = source.getSentDate() != null ? source.getSentDate() : new Date();
            this.sentDate = sentDate;
            this.receivedDate = receivedDate != null ? receivedDate : (source.getReceivedDate() != null ? source.getReceivedDate() : sentDate );
        }

        @Override
        public Date getReceivedDate() {
            return receivedDate;
        }

        @Override
        public Date getSentDate() {
            return sentDate;
        }
    }
}
