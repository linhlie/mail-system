package io.owslab.mailreceiver.service.mail;

import com.sun.mail.imap.IMAPFolder;
import com.vdurmont.emoji.EmojiParser;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.job.IMAPFetchMailJob;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.word.FuzzyWordService;
import io.owslab.mailreceiver.service.word.WordService;
import io.owslab.mailreceiver.utils.FullNumberRange;
import io.owslab.mailreceiver.utils.Utils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;
import javax.mail.search.MessageNumberTerm;
import javax.mail.search.SearchTerm;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class MailBoxService {
    private static final Logger logger = LoggerFactory.getLogger(MailBoxService.class);

    public static final String HIGHLIGHT_RANGE_COLOR = "#ff9900";
    public static final int USE_RAW = 0;
    public static final int USE_LOWER_LIMIT = 1;
    public static final int USE_UPPER_LIMIT = 2;
    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private FileDAO fileDAO;

    @Autowired
    private NumberTreatmentService numberTreatmentService;


    @Autowired
    private NumberRangeService numberRangeService;

    @Autowired
    private MailAccountsService mailAccountsService;

    @Autowired
    private EmailAccountSettingService emailAccountSettingService;

    @Autowired
    private WordService wordService;

    @Autowired
    private FuzzyWordService fuzzyWordService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;
    
    private List<Email> cachedEmailList = null;

    public long count(){
        return emailDAO.countByDeleted(false);
    }

    public Page<Email> list(PageRequest pageRequest) {
        Page<Email> list = emailDAO.findByDeleted(false, pageRequest);
        return list;
    }

    public Page<Email> listError(PageRequest pageRequest) {
        Page<Email> list = emailDAO.findByErrorLogNotNullAndDeleted(false, pageRequest);
        return list;
    }

    public Page<Email> searchContent(String search, PageRequest pageRequest) {
        if(search == null || search.length() == 0){
            return list(pageRequest);
        }
        String optimizeSearchText = optimizeText(search);
        Page<Email> list = emailDAO.findByOptimizedBodyIgnoreCaseContainingAndDeleted(optimizeSearchText, false, pageRequest);
        return list;
    }

    public Page<Email> searchError(String search, PageRequest pageRequest) {
        if(search == null || search.length() == 0){
            return listError(pageRequest);
        }
        String optimizeSearchText = optimizeText(search);
        Page<Email> list = emailDAO.findBySubjectIgnoreCaseContainingAndErrorLogNotNullAndDeleted(optimizeSearchText, false, pageRequest);
        return list;
    }

    public List<Email> filterDuplicate(List<Email> allMails, boolean filterSender, boolean filterSubject){
        if(filterSender && filterSubject) {
            Collections.sort(allMails, new SenderSubjectComparator());
            List<Email> withoutDuplicate = new ArrayList<>();
            String previousSubject = null;
            Email previous = null;
            for(Email mail : allMails) {
                boolean overlapSender = sameSender(previous, mail);
                if(!overlapSender) {
                    previous = mail;
                    previousSubject = mail.getSubject();
                    withoutDuplicate.add(mail);
                } else {
                    boolean overlapSubject = sameSubject(previousSubject, mail.getSubject());
                    if(overlapSubject) {
                        if(withoutDuplicate.size() > 0){
                            withoutDuplicate.remove(withoutDuplicate.size() -1);
                        }
                    } else {
                        previousSubject = mail.getSubject();
                    }
                    previous = mail;
                    withoutDuplicate.add(mail);
                }
            }
            return withoutDuplicate;
        } else if (filterSender && !filterSubject) {
            Collections.sort(allMails, new SenderComparator());
            List<Email> withoutDuplicateSender = new ArrayList<>();
            Email previous = null;
            for(Email mail : allMails) {
                if(!sameSender(previous, mail)) {
                    withoutDuplicateSender.add(mail);
                    previous = mail;
                }
            }
            return withoutDuplicateSender;
        } else if (!filterSender && filterSubject) {
            Collections.sort(allMails, new SubjectComparator());
            List<Email> withoutDuplicateSubject = new ArrayList<>();
            String previousSubject = null;
            for(Email mail : allMails) {
                boolean overlap = sameSubject(previousSubject, mail.getSubject());
                if(overlap) {
                    if(withoutDuplicateSubject.size() > 0){
                        withoutDuplicateSubject.remove(withoutDuplicateSubject.size() -1);
                    }
                } else {
                    previousSubject = mail.getSubject();
                }
                withoutDuplicateSubject.add(mail);
            }
            return withoutDuplicateSubject;
        } else {
            return allMails;
        }
    }

    private boolean sameSender(Email prev, Email next) {
        if(prev == null) {
            return next == null;
        }
        if(next == null) {
            return prev == null;
        }
        String prevSender = prev.getFrom();
        String nextSender = next.getFrom();
        return prevSender.equals(nextSender);
    }

    private boolean sameSubject(String prevSubject, String nextSubject) {
        if(prevSubject == null || nextSubject == null) {
            return false;
        }
        if(prevSubject.length() == 0 || nextSubject.length() == 0) {
            return false;
        }
        return nextSubject.endsWith(prevSubject);
    }

    public List<Email> getAll(){
        return getAll(false);
    }

    public List<Email> getAll(boolean forceUpdate){
        if(forceUpdate || cachedEmailList == null){
            cachedEmailList = emailDAO.findByErrorLogIsNullOrderByReceivedAtDesc();
        }
        return cachedEmailList;
    }

    @Cacheable(key="\"EmailWordJobService:find:\"+#original")
    public static String optimizeText(String original){
        Document jsoupDoc = Jsoup.parse(original);
        jsoupDoc.outputSettings(new OutputSettings().prettyPrint(false));
        jsoupDoc.select("br").after("\\n");
        jsoupDoc.select("div").before("\\n");
        jsoupDoc.select("p").before("\\n");
        String str = jsoupDoc.html().replaceAll("\\\\n", "\n");
        String optimizedText = Jsoup.clean(str, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
        //TODO: maybe need remove url and change optimizeText usages
        return optimizedText.toLowerCase();
    }

    public static String removeUrl(String commentstr)
    {
        String urlPattern = "((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(commentstr);
        int i = 0;
        while (m.find()) {
            commentstr = commentstr.replaceAll(m.group(i),"").trim();
            i++;
        }
        return commentstr;
    }

    public List<DetailMailDTO> getMailDetail(String messageId, String highlightWord, String matchRange, boolean spaceEffective, boolean distinguish){
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> emailList = emailDAO.findByMessageIdAndDeleted(messageId, false);
        if(emailList.size() == 0) return results;
        Email email = emailList.get(0);
        DetailMailDTO result = new DetailMailDTO(email);
        if(highlightWord != null) {
            List<String> highLightWords = result.getHighLightWords();
            List<String> excludeWords = result.getExcludeWords();
            highLightWords.add(highlightWord);
            Word word = wordService.findOne(highlightWord);
            if(word != null) {
                List<Word> exclusionWords = fuzzyWordService.findAllExclusionWord(word);
                List<Word> sameWords = fuzzyWordService.findAllSameWord(word);
                for(Word sameWord : sameWords){
                    highLightWords.add(sameWord.getWord());
                }
                for(Word exclusionWord : exclusionWords){
                    String exclusionWordStr = exclusionWord.getWord();
                    if(!highLightWords.contains(exclusionWordStr))
                        excludeWords.add(exclusionWordStr);
                }
            }
        }
        List<String> highLightRanges = result.getHighLightRanges();
        if(matchRange != null) {
            highLightRanges.add(matchRange);
        } else {
            String optimizedPart = email.getOptimizedText(false);
            List<FullNumberRange> fullNumberRanges = numberRangeService.buildNumberRangeForInput(email.getMessageId(), optimizedPart);
            for(FullNumberRange range : fullNumberRanges){
                String rangeStr = range.toString();
                if(!highLightRanges.contains(rangeStr)) {
                    highLightRanges.add(rangeStr);
                }
            }
        }
        List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
        for(AttachmentFile file : files){
            result.addFile(file);
        }
        results.add(result);
        return results;
    }

    public DetailMailDTO getContentRelyEmail(String replyId, String accountId) throws Exception {
        List<Email> replyList = emailDAO.findByMessageIdAndDeleted(replyId, false);
        Email replyEmail = replyList.size() > 0 ? replyList.get(0) : null;
        if(replyEmail == null) {
            throw new Exception("This mail has been deleted or does not exist");
        }
        List<EmailAccount> listAccount = accountId != null ? mailAccountsService.findById(Long.parseLong(accountId)) : mailAccountsService.list();
        EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
        if(emailAccount == null) {
            throw new Exception("Missing sender account info. Can't reply this email");
        }
        SendAccountForm sendAccountForm = emailAccountSettingService.getSendAccountForm(emailAccount.getId());
        if(sendAccountForm == null) {
            throw new Exception("Missing sender account info. Can't reply this email");
        }
        DetailMailDTO result = new DetailMailDTO(replyEmail, emailAccount);
        result.setExternalCC(sendAccountForm.getCc());
        String signature = emailAccount.getSignature().length() > 0 ? "<br>--<br>" + emailAccount.getSignature() : "";
        result.setSignature(signature);
        result.setExcerpt(getExcerpt(replyEmail));
        String replyText = getReplyContentFromEmail(replyEmail);
        result.setReplyOrigin(replyText);
        result.setSubject("Re: " + replyEmail.getSubject());
        return result;
    }

    public  DetailMailDTO getMailDetailWithReplacedRange(String messageId, String replyId, String rangeStr, String matchRangeStr, int replaceType, String accountId) throws Exception {
        List<Email> emailList = emailDAO.findByMessageIdAndDeleted(messageId, false);
        List<Email> replyList = emailDAO.findByMessageIdAndDeleted(replyId, false);
        Email originEmail = emailList.size() > 0 ? emailList.get(0) : null;
        Email replyEmail = replyList.size() > 0 ? replyList.get(0) : null;
        if(replyEmail == null) {
            throw new Exception("Reply mail has been deleted or does not exist");
        }
        if(originEmail == null) {
            throw new Exception("Email has been deleted or does not exist");
        }
        List<EmailAccount> listAccount = accountId != null ? mailAccountsService.findById(Long.parseLong(accountId)) : mailAccountsService.list();
        EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
        if(emailAccount == null) {
            throw new Exception("Missing sender account info. Can't reply this email");
        }
        SendAccountForm sendAccountForm = emailAccountSettingService.getSendAccountForm(emailAccount.getId());
        if(sendAccountForm == null) {
            throw new Exception("Missing sender account info. Can't reply this email");
        }
        String forBuildRangeStr = replaceType >= 3 ? matchRangeStr : rangeStr;
        List<FullNumberRange> fullNumberRanges = numberRangeService.buildNumberRangeForInput(forBuildRangeStr, forBuildRangeStr, false, false);
        FullNumberRange firstRange = fullNumberRanges.size() > 0 ? fullNumberRanges.get(0) : null;
        NumberTreatment numberTreatment = numberTreatmentService.getFirst();
        double ratio = 1;
        replaceType = replaceType >= 3 ? replaceType - 3 : replaceType;
        if(replaceType > USE_RAW && numberTreatment != null){
            ratio = replaceType == USE_UPPER_LIMIT ? numberTreatment.getUpperLimitRate() : numberTreatment.getLowerLimitRate();
        }
        String firstRangeStr = null;
        if(firstRange != null){
            firstRangeStr = firstRange.toString(ratio);
        }
        DetailMailDTO result = emailAccount == null ? new DetailMailDTO(originEmail) : new DetailMailDTO(originEmail, emailAccount);
        String signature = emailAccount != null && emailAccount.getSignature().length() > 0 ? "<br>--<br>" + emailAccount.getSignature() : "";
        result.setExternalCC(sendAccountForm.getCc());
        if(rangeStr != null && firstRangeStr != null){
            String rawBody = result.getOriginalBody();
            String replacedBody = replaceAllContent(rawBody, rangeStr, firstRangeStr);
            result.setReplacedBody(replacedBody);
        }
        result.setExcerpt(getExcerpt(replyEmail));
        result.setSignature(signature);
        if(replyEmail != null) {
            String replyText = getReplyContentFromEmail(replyEmail);
            result.setReplyOrigin(replyText);
            result.setSubject("Re: " + replyEmail.getSubject());
            result.setTo(replyEmail.getTo());
            result.setCc(replyEmail.getCc());
        }
        List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
        for(AttachmentFile file : files){
            result.addFile(file);
        }
        return result;
    }

    private String getExcerpt(Email email){
        String optimizedBody = email.getOptimizedBody();
        String[] optimizedBodyLines = optimizedBody.split("\n");
        String excerpt = "";
        int i = 0;
        for(String line: optimizedBodyLines) {
            line = line.trim();
            if(!line.isEmpty() && !line.equalsIgnoreCase("\r") && !line.equalsIgnoreCase("\n")) {
                excerpt = excerpt + getExceprtLine(line);
                i++;
                if(i == 5) {
                    break;
                }
            }
        }
//        excerpt = excerpt + getExceprtLine("---------------------");
//        excerpt = excerpt + "<br/><br/><br/><br/><br/>";
        return excerpt;
    }

    private String getExceprtLine(String line) {
        String exceprtLine = "<div class=\"gmail_extra\"><span style=\"color: #ff0000;\">" + line + "</span></div>\n";
        return exceprtLine;
    }

    private String getReplyContentFromEmail(Email replyEmail) {
        String replyText = "<div class=\"gmail_extra\"><br>";
        replyText += "<div class=\"gmail_quote\">"
                + Utils.formatGMT(replyEmail.getSentAt())
                + " <span dir=\"ltr\">&lt;<a href=\"mailto:"
                + replyEmail.getFrom()
                + "\" target=\"_blank\" rel=\"noopener\">"
                + replyEmail.getFrom()
                + "</a>&gt;</span>:<br />";
        replyText += "<blockquote class=\"gmail_quote\" style=\"margin: 0 0 0 .8ex; border-left: 1px #ccc solid; padding-left: 1ex;\">\n" +
                "<div dir=\"ltr\">"+ replyEmail.getOriginalBody() +"</div>\n" +
                "</blockquote>";
        replyText += "</div>\n" +
                "</div>";
        return replyText;
    }

    private String replaceAllContent(String source, String regex, String replacement){
        String styleReplacement = "<span style=\"color: " + HIGHLIGHT_RANGE_COLOR + ";\">" + replacement + "</span>";

        String replaced = source.replaceAll(regex, styleReplacement);
        return replaced;
    }

    public void retry(String messageId) {
        Email email = findOne(messageId);
        if(email == null) return;
        String messageNumber = email.getMessageNumber();
        if(messageNumber == null) return;
        long accountId = email.getAccountId();
        List<EmailAccount> listAccount = mailAccountsService.findById(accountId);
        EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
        if(emailAccount == null) return;
        EmailAccountSetting accountSetting = emailAccountSettingService.findOneSend(accountId);
        if(accountSetting == null) return;
        try {
            Store store = createStore(accountSetting);
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                store.connect(accountSetting.getMailServerAddress(), accountSetting.getUserName(), accountSetting.getPassword());
            } else {
                store.connect(accountSetting.getMailServerAddress(), emailAccount.getAccount(), accountSetting.getPassword());
            }

            //create the folder object and open it
            IMAPFolder emailFolder = (IMAPFolder) store.getFolder("INBOX");
            int openFolderFlag = Folder.READ_ONLY ;
            emailFolder.open(openFolderFlag);

            IMAPFetchMailJob.OwsMimeMessage message = IMAPFetchMailJob.getMessage(emailFolder, Integer.parseInt(email.getMessageNumber()));
            try {
                email = IMAPFetchMailJob.buildReceivedMail(message, email);
                boolean hasAttachments = saveFiles(message, email);
                email.setHasAttachment(hasAttachments);
                email = IMAPFetchMailJob.setMailContent(message, email);
                email.setErrorLog(null);
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
            logger.info("retry email: " + message.getSubject());
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
                if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition()) || Part.INLINE.equalsIgnoreCase(part.getDisposition())) {
                    // this part is attachment
                    String fileName = part.getFileName();
                    if(fileName == null) continue;
                    if(fileName.indexOf("=?") == -1) {
                        fileName = new String(fileName.getBytes("ISO-8859-1"));
                    } else {
                        fileName = MimeUtility.decodeText(part.getFileName());
                    }
                    String saveDirectoryPath = enviromentSettingService.getStoragePath();
                    String currentDateStr = IMAPFetchMailJob.getCurrentDateStr();
                    saveDirectoryPath = IMAPFetchMailJob.normalizeDirectoryPath(saveDirectoryPath) + "/" + currentDateStr;
                    File saveDirectory = new File(saveDirectoryPath);
                    if (!saveDirectory.exists()){
                        saveDirectory.mkdir();
                    }
                    saveDirectoryPath = IMAPFetchMailJob.normalizeDirectoryPath(saveDirectoryPath) + "/" + email.getMessageId().hashCode();
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

    private Email findOne(String messageId) {
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        return emailList.size() > 0 ? emailList.get(0) : null;
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

    public class SenderComparator implements Comparator<Email> {
        public int compare(Email o1, Email o2) {
            int value1 = o1.getFrom().compareTo(o2.getFrom());
            if (value1 == 0) {
                return o2.getReceivedAt().compareTo(o1.getReceivedAt());
            }
            return value1;
        }
    }

    public class SubjectComparator implements Comparator<Email> {
        public int compare(Email o1, Email o2) {
            String reversedSubject1 = reverseStringWithCache(o1.getSubject());
            String reversedSubject2 = reverseStringWithCache(o2.getSubject());
            int value1 = reversedSubject1.compareTo(reversedSubject2);
            if (value1 == 0) {
                return o1.getReceivedAt().compareTo(o2.getReceivedAt());
            }
            return value1;
        }
    }

    public class SenderSubjectComparator implements Comparator<Email> {
        public int compare(Email o1, Email o2) {
            int value1 = o1.getFrom().compareTo(o2.getFrom());
            if (value1 == 0) {
                String reversedSubject1 = reverseStringWithCache(o1.getSubject());
                String reversedSubject2 = reverseStringWithCache(o2.getSubject());
                int value2 = reversedSubject1.compareTo(reversedSubject2);
                if (value2 == 0) {
                    return o1.getReceivedAt().compareTo(o2.getReceivedAt());
                }
                return value2;
            }
            return value1;
        }
    }
    @Cacheable(key="\"EmailWordJobService:reverseStringWithCache:\"+#raw")
    public static String reverseStringWithCache(String raw) {
        return new StringBuilder(raw).reverse().toString();
    }
}
