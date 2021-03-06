package io.owslab.mailreceiver.service.mail;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.dto.MoreInformationMailContentDTO;
import io.owslab.mailreceiver.enums.ClickType;
import io.owslab.mailreceiver.enums.CompanyType;
import io.owslab.mailreceiver.form.MoreInformationMailContentForm;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.form.SendMailForm;
import io.owslab.mailreceiver.form.SendMultilMailForm;
import io.owslab.mailreceiver.job.FetchMailJob;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.expansion.*;
import io.owslab.mailreceiver.service.greeting.GreetingService;
import io.owslab.mailreceiver.service.matching.MatchingConditionService;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.security.AccountService;
import io.owslab.mailreceiver.service.settings.EnviromentSettingService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.service.word.FuzzyWordService;
import io.owslab.mailreceiver.service.word.WordService;
import io.owslab.mailreceiver.utils.FullNumberRange;
import io.owslab.mailreceiver.utils.MailUtils;
import io.owslab.mailreceiver.utils.Utils;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.ocpsoft.prettytime.PrettyTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeUtility;

import java.io.*;
import java.text.DecimalFormat;
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
    public static final String BODY_HEADEAR = "　ご担当者様";
    @Autowired
    private EmailDAO emailDAO;
    
    @Autowired
    private DomainService domainService;

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
    private MatchingConditionService matchingConditionService;

    @Autowired
    private EnviromentSettingService enviromentSettingService;
    
    @Autowired
    private BusinessPartnerService partnerService;

    @Autowired
    private PeopleInChargePartnerUnregisterService peopleInChargeUnregisterService;

    @Autowired
    private PeopleInChargePartnerService peopleInChargePartnerService;

    @Autowired
    AccountService accountService;

    @Autowired
    SendMailService sendMailService;

    @Autowired
    private GreetingService greetingService;
    
    private List<Email> cachedEmailList = null;

    private List<Email> cachedEmailListASC = null;

    private DecimalFormat df = new DecimalFormat("#,##0");

    private PrettyTime p = new PrettyTime();

    public long count(){
        return emailDAO.countByStatus(Email.Status.DONE);
    }

    public Page<Email> list(PageRequest pageRequest) {
        return listByStatus(pageRequest, Email.Status.DONE);
    }

    public Page<Email> listTrash(PageRequest pageRequest) {
        return listByStatus(pageRequest, Email.Status.SKIPPED);
    }

    private Page<Email> listByStatus(PageRequest pageRequest, int status) {
        Page<Email> list = emailDAO.findByStatus(status, pageRequest);
        return list;
    }

    public Page<Email> listError(PageRequest pageRequest) {
        Page<Email> list = listByStatus(pageRequest, Email.Status.ERROR_OCCURRED);
        return list;
    }

    public Page<Email> searchContent(String search, PageRequest pageRequest) {
        if(search == null || search.length() == 0){
            return list(pageRequest);
        }
        String optimizeSearchText = "%"+optimizeTextForSearch(search)+"%";
        List<Email> list = emailDAO.findByStatusAndFromOrToOrCcOrSubjectOrBody(Email.Status.DONE, optimizeSearchText, pageRequest.getOffset(), pageRequest.getPageSize());
        int size = emailDAO.countFindByStatusAndFromOrToOrCcOrSubjectOrBody(Email.Status.DONE, optimizeSearchText);
        Page<Email> result = new PageImpl<Email>(list, pageRequest, size);
        return result;
    }

    public Page<Email> searchTrash(String search, PageRequest pageRequest) {
        if(search == null || search.length() == 0){
            return listTrash(pageRequest);
        }
        String optimizeSearchText = "%"+optimizeTextForSearch(search)+"%";
        List<Email> list = emailDAO.findByStatusAndFromOrToOrCcOrSubjectOrBody(Email.Status.SKIPPED, optimizeSearchText, pageRequest.getOffset(), pageRequest.getPageSize());
        int size = emailDAO.countFindByStatusAndFromOrToOrCcOrSubjectOrBody(Email.Status.SKIPPED, optimizeSearchText);
        Page<Email> result = new PageImpl<Email>(list, pageRequest, size);
        return result;
    }

    public Page<Email> searchError(String search, PageRequest pageRequest) {
        if(search == null || search.length() == 0){
            return listError(pageRequest);
        }
        String optimizeSearchText = "%"+optimizeTextForSearch(search)+"%";
        List<Email> list = emailDAO.findByStatusAndFromOrToOrCcOrSubjectOrBodyOrLog(Email.Status.ERROR_OCCURRED, optimizeSearchText, pageRequest.getOffset(), pageRequest.getPageSize());
        int size = emailDAO.countFindByStatusAndFromOrToOrCcOrSubjectOrBodyOrLog(Email.Status.ERROR_OCCURRED, optimizeSearchText);
        Page<Email> result = new PageImpl<Email>(list, pageRequest, size);
        return result;
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
            cachedEmailList = emailDAO.findByStatusOrderByReceivedAtDesc(Email.Status.DONE);
        }
        return cachedEmailList;
    }

    public List<Email> getAllASC(){
        return getAllASC(false);
    }

    public List<Email> getAllASC(boolean forceUpdate){
        if(forceUpdate || cachedEmailListASC == null){
            cachedEmailListASC = emailDAO.findByStatusOrderByReceivedAtAsc(Email.Status.DONE);
        }
        return cachedEmailListASC;
    }

    public List<Email> getAllInBox(){
        return emailDAO.findByStatusOrStatusOrderByReceivedAtDesc(Email.Status.DONE, Email.Status.SKIPPED);
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

    public static String optimizeTextForSearch(String original){
        Document jsoupDoc = Jsoup.parse(original);
        jsoupDoc.outputSettings(new OutputSettings().prettyPrint(false));
        jsoupDoc.select("br").after("\\n");
        jsoupDoc.select("div").before("\\n");
        jsoupDoc.select("p").before("\\n");
        String str = jsoupDoc.html().replaceAll("\\\\n", "\n");
        String optimizedText = Jsoup.clean(str, "", Whitelist.none(), new OutputSettings().prettyPrint(false));
        //TODO: maybe need remove url and change optimizeText usages
        int conv_op_flags = 0;
        conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
        conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
        optimizedText = KanaConverter.convertKana(optimizedText, conv_op_flags);
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

    public List<DetailMailDTO> getMailDetail(String messageId) {
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        if(emailList.size() == 0) return results;
        Email email = emailList.get(0);
        DetailMailDTO result = new DetailMailDTO(email);
        List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
        for(AttachmentFile file : files){
            result.addFile(file);
        }
        results.add(result);
        return results;
    }

    public List<DetailMailDTO> getMailDetail(String messageId, String highlightWordsStr, String matchRange, boolean spaceEffective, boolean distinguish){
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        if(emailList.size() == 0) return results;
        Email email = emailList.get(0);
        DetailMailDTO result = new DetailMailDTO(email);
        String originalConvertBody = matchingConditionService.getOptimizedText(result.getOriginalBody(), false);
        if(originalConvertBody == null){
            originalConvertBody = "";
        }
        if(highlightWordsStr != null) {
            highlightWordsStr = highlightWordsStr.replace("!!", ",");
            List<String> hlWords = Arrays.asList(highlightWordsStr.split(","));
            List<String> hlWordsFinal = new ArrayList<>();
            for(String highlightWord : hlWords) {
                if(highlightWord!=null){
                    hlWordsFinal.add(highlightWord.trim());
                }
            }
            List<String> highLightWords = result.getHighLightWords();
            List<String> excludeWords = result.getExcludeWords();
            for(String highlightWord : hlWordsFinal) {
                if(highlightWord != null) {
                    Word word = wordService.findOne(highlightWord);
                    if(word != null) {
                        List<Word> exclusionWords = fuzzyWordService.findAllExclusionWord(word);
                        List<Word> sameWords = fuzzyWordService.findAllInGroup(word);
                        for(Word sameWord : sameWords){
                            highLightWords.addAll(findAllWord(sameWord.getWord(), result.getOriginalBody(), originalConvertBody));
                        }
                        for(Word exclusionWord : exclusionWords){
                            String exclusionWordStr = exclusionWord.getWord();
                            if(!highLightWords.contains(exclusionWordStr))
                                excludeWords.addAll(findAllWord(exclusionWordStr, result.getOriginalBody(), originalConvertBody));
                        }
                    }else{
                        highLightWords.addAll(findAllWord(highlightWord, result.getOriginalBody(), originalConvertBody));
                    }
                }
            }
        }
        List<String> highLightRanges = result.getHighLightRanges();
        if(matchRange != null) {
            highLightRanges.addAll(findAllWord(matchRange, result.getOriginalBody(), originalConvertBody));
        } else {
            String optimizedPart = email.getOptimizedText(false);
            List<FullNumberRange> fullNumberRanges = numberRangeService.buildNumberRangeForInput(email.getMessageId(), optimizedPart);
            for(FullNumberRange range : fullNumberRanges){
                String rangeStr = range.toString();
                if(!highLightRanges.contains(rangeStr)) {
                    highLightRanges.addAll(findAllWord(rangeStr, result.getOriginalBody(), originalConvertBody));
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

    public DetailMailDTO getContentRelyEmail(String replyId) throws Exception {
        List<Email> replyList = emailDAO.findByMessageId(replyId);
        Email replyEmail = replyList.size() > 0 ? replyList.get(0) : null;
        if(replyEmail == null) {
            throw new Exception("This mail has been deleted or does not exist");
        }
        DetailMailDTO result = new DetailMailDTO(replyEmail);
        result.setExcerpt(getExcerpt(replyEmail));
        String replyText = replyEmail.getOriginalBody();
        result.setReplyOrigin(replyText);
        result.setReplyFrom(replyEmail.getFrom());
        result.setReplySentAt(Utils.formatGMT(replyEmail.getSentAt()));
        result.setSubject("Re: " + replyEmail.getSubject());
        return result;
    }

	public  DetailMailDTO getMailDetailWithReplacedRange(String messageId, String replyId, String rangeStr, String matchRangeStr, int replaceType, String accountId) throws Exception {
        List<Email> emailList = emailDAO.findByMessageId(messageId);
        List<Email> replyList = emailDAO.findByMessageId(replyId);
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
        if(rangeStr != null && firstRangeStr != null && !rangeStr.equalsIgnoreCase("") && !firstRangeStr.equalsIgnoreCase("")){
            String rawBody = result.getOriginalBody();
            String replacedBody = replaceAllContent(rawBody, rangeStr, firstRangeStr);
            result.setReplacedBody(replacedBody);
        }
        result.setExcerpt(getExcerpt(replyEmail));
        result.setSignature(signature);
        if(replyEmail != null) {
            String replyText = replyEmail.getOriginalBody();
            result.setReplyOrigin(replyText);
            result.setReplyFrom(replyEmail.getFrom());
            result.setReplySentAt(Utils.formatGMT(replyEmail.getSentAt()));
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
        String exceprtLine = "<div class=\"gmail_extra\"><span>" + line + "</span></div>\n";
        return exceprtLine;
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
        EmailAccountSetting accountSetting = emailAccountSettingService.findOneReceive(accountId);
        if(accountSetting == null) return;
        try {
            Store store = MailUtils.createStore(accountSetting);
            if(accountSetting.getUserName() != null && accountSetting.getUserName().length() > 0){
                store.connect(accountSetting.getMailServerAddress(), accountSetting.getUserName(), accountSetting.getPassword());
            } else {
                store.connect(accountSetting.getMailServerAddress(), emailAccount.getAccount(), accountSetting.getPassword());
            }

            //create the folder object and open it
            Folder emailFolder = store.getFolder("INBOX");
            int openFolderFlag = Folder.READ_ONLY ;
            emailFolder.open(openFolderFlag);

            FetchMailJob.OwsMimeMessage message = FetchMailJob.getMessage(emailFolder, Integer.parseInt(email.getMessageNumber()));
            try {
                email = FetchMailJob.buildReceivedMail(message, email);
                boolean hasAttachments = saveFiles(message, email);
                email.setHasAttachment(hasAttachments);
                email = FetchMailJob.setMailContent(message, email);
                email.setStatus(Email.Status.NEW);
                email.setErrorLog(null);
                emailDAO.save(email);
            } catch (Exception e) {
                e.printStackTrace();
                Email errorEmail = findOne(email.getMessageId());
                if(errorEmail != null) {
                    String error = ExceptionUtils.getStackTrace(e);
                    errorEmail.setStatus(Email.Status.ERROR_OCCURRED);
                    errorEmail.setErrorLog(error);
                    emailDAO.save(errorEmail);
                }
            }
            logger.info("retry email: " + message.getSubject());
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
                    String currentDateStr = FetchMailJob.getCurrentDateStr();
                    saveDirectoryPath = FetchMailJob.normalizeDirectoryPath(saveDirectoryPath) + File.separator + currentDateStr;
                    File saveDirectory = new File(saveDirectoryPath);
                    if (!saveDirectory.exists()){
                        saveDirectory.mkdir();
                    }
                    saveDirectoryPath = FetchMailJob.normalizeDirectoryPath(saveDirectoryPath) + File.separator + email.getMessageId().hashCode();
                    saveDirectory = new File(saveDirectoryPath);
                    if (!saveDirectory.exists()){
                        saveDirectory.mkdir();
                    }
                    saveDirectoryPath = saveDirectoryPath + File.separator + FetchMailJob.getUniqueFileName();
                    File file = new File(saveDirectoryPath);
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

    public String getLatestReceive(String accountId) {
        try {
            List<Email> emailList;
            if(accountId == null) {
                emailList = emailDAO.findFirst1ByStatusOrderByReceivedAtDesc(Email.Status.DONE);
            } else {
                emailList = emailDAO.findFirst1ByAccountIdAndStatusOrderByReceivedAtDesc(Long.parseLong(accountId), Email.Status.DONE);
            }

            if(emailList.size() > 0) {
                Email email = emailList.get(0);
                p.setLocale(Locale.ENGLISH);
                return Utils.formatGMT2(email.getReceivedAt()) + " (" + p.format(email.getReceivedAt()) + ")";
            }
        } catch (Exception e) {
            ;
        }
        return "不詳";
    }

    public List<String> getReceiveMailNumberStats(Date now, String accountId) {
        List<String> stats = new ArrayList<>();
        Date fromDate = Utils.atStartOfDay(now);
        Date toDate = Utils.atEndOfDay(now);
        for(int i = 0; i < 8; i++){
            if(i > 0) {
                fromDate = Utils.addDayToDate(fromDate, -1);
                toDate = Utils.addDayToDate(toDate, -1);
            }
            long clicks = accountId == null ? emailDAO.countByReceivedAtBetweenAndStatus(fromDate, toDate, Email.Status.DONE)
                        : emailDAO.countByAccountIdAndReceivedAtBetweenAndStatus(Long.parseLong(accountId), fromDate, toDate, Email.Status.DONE);
            stats.add(df.format(clicks));
        }
        return stats;
    }

    @Cacheable(key="\"EmailWordJobService:reverseStringWithCache:\"+#raw")
    public static String reverseStringWithCache(String raw) {
        return new StringBuilder(raw).reverse().toString();
    }

    public void emptyTrashBox() {
        emailDAO.updateStatus(Email.Status.SKIPPED, Email.Status.DELETED);
    }

    public void deleteFromTrashBox(Collection<String> msgIds) {
        emailDAO.updateStatusByMessageIdIn(Email.Status.SKIPPED, Email.Status.DELETED, msgIds);
    }

    public void moveToInbox(List<String> msgIds) {
        emailDAO.updateStatusByMessageIdIn(Email.Status.SKIPPED, Email.Status.DONE, msgIds);
        List<Email> listEmailToCheckDomain = emailDAO.findByMessageIdIn(msgIds);
        boolean isAddNewDomainUnregister = enviromentSettingService.getAddNewDomainUnregister();
        if(isAddNewDomainUnregister){
            domainService.saveDomainUnregistered(listEmailToCheckDomain);
        }
        boolean isAddNewPeopleInChargeUnregister = enviromentSettingService.getAddNewPeopleInChargePartnerUnregister();
        if(isAddNewPeopleInChargeUnregister){
            peopleInChargeUnregisterService.savePeopleInChargeUnregistered(listEmailToCheckDomain);
        }
    }

    public void deleteFromInBox(Collection<String> msgIds) {
        emailDAO.updateStatusByMessageIdIn(Email.Status.DONE, Email.Status.DELETED, msgIds);
    }

    public void deleteFromErrorBox(Collection<String> msgIds) {
        emailDAO.updateStatusByMessageIdIn(Email.Status.ERROR_OCCURRED, Email.Status.DELETED, msgIds);
    }

    public void sendMultilMail(SendMultilMailForm form) throws Exception {
        List<String> listMailId = form.getListId();
        if(listMailId.size()<=0) return;
        if(form.getContent()==null) return;

        for(int i=0;i<listMailId.size();i++){
            SendMailForm sendMailForm = new SendMailForm();
            Email email = emailDAO.findOne(listMailId.get(i));
            if(email == null) continue;

            long emailAccountId = email.getAccountId();
            if(form.getAccountId() > -1){
                emailAccountId = form.getAccountId();
            }
            String emailBody = email.getOriginalBody();
            emailBody = wrapText(emailBody);
            emailBody = getReplyWrapper(Utils.formatGMT(email.getSentAt()), email.getFrom(), emailBody);
            emailBody = form.getContent() + "<br /><br /><br />" + emailBody;

            long userLoggedId = accountService.getLoggedInAccountId();
            String greeting = greetingService.getGreetings(emailAccountId, ClickType.REPLY_EMAIL_VIA_INBOX.getValue(), email.getFrom(), userLoggedId, -1);
            emailBody =  greeting + "<br /><br />" + emailBody;
            emailBody = emailBody + "<br />" + getSignature(emailAccountId);
            String cc = getEmailCc(email, emailAccountId);

            sendMailForm.setMessageId(email.getMessageId());
            sendMailForm.setSubject("Re: " + email.getSubject());
            sendMailForm.setReceiver(email.getFrom());
            sendMailForm.setCc(cc);
            sendMailForm.setContent(emailBody);
            sendMailForm.setOriginAttachment(form.getOriginAttachment());
            sendMailForm.setUploadAttachment(form.getUploadAttachment());
            sendMailForm.setAccountId(emailAccountId+"");
            sendMailForm.setSendType(form.getSendType());
            sendMailForm.setHistoryType(form.getHistoryType());

            email.setReplyTimes(email.getReplyTimes()+1);
            emailDAO.save(email);
            sendMailService.sendMail(sendMailForm);
        }
    }

    public String wrapText(String text){
        text = text.replaceAll("\\r\\n", "<br />");
        text = text.replaceAll("\\r", "<br />");
        text = text.replaceAll("\\n", "<br />");
        return text;
    }

    public String getReplyWrapper(String replySentAt, String replyFrom, String replyOrigin){
        String wrapperText = "<div class=\"gmail_extra\"><br>" +
                "<div class=\"gmail_quote\">" +
                replySentAt +
                "<span dir=\"ltr\">&lt;<a href=\"mailto:" +
                replyFrom +
                "\" target=\"_blank\" rel=\"noopener\">" +
                replyFrom +
                "</a>&gt;</span>:<br />" +
                "<blockquote class=\"gmail_quote\" style=\"margin: 0 0 0 .8ex; border-left: 1px #ccc solid; padding-left: 1ex;\">" +
                "<div dir=\"ltr\">" +
                replyOrigin + "</div></blockquote></div></div>";
        return wrapperText;
    }

    public String getSignature(long accountId){
        EmailAccount emailAccount = mailAccountsService.getEmailAccountById(accountId);
        if(emailAccount == null){
            return "";
        }else{
            return  emailAccount.getSignature();
        }
    }

    public String getEmailCc(Email email, long emailAccountId){
        String cc = email.getCc();
        EmailAccountSetting emailAccountSetting = emailAccountSettingService.findOneSend(emailAccountId);
        String ccByAccount = emailAccountSetting.getCc();
        if(cc != null && !cc.trim().equalsIgnoreCase("")){
            if(ccByAccount != null && !ccByAccount.trim().equalsIgnoreCase("")){
                cc = cc + ", " + ccByAccount;
            }
        }else{
            cc = ccByAccount;
        }

        cc = selfEliminateDuplicates(cc, emailAccountSetting.getUserName().trim());
        return cc;
    }

    public String selfEliminateDuplicates(String raw, String accSend) {
        String[] emails = raw.split(",");
        List<String> result = new ArrayList<>();
        for(int i=emails.length-1; i>=0; i--){
            emails[i] = emails[i].trim();
            emails[i] = emails[i].toLowerCase();
            if(!emails[i].equalsIgnoreCase(accSend)){
                result.add(emails[i]);
            }
        }
        return String.join(",", new HashSet<String>(result));
    }

    public List<String> findAllWord(String word, String content, String contentConvert){
        List<String> result = new ArrayList<>();
        if(content==null || contentConvert==null || content.length() != contentConvert.length()){
            result.add(word);
        }else{
            String optimizeWord = matchingConditionService.getOptimizedText(word, false);
            int index = contentConvert.indexOf(optimizeWord);
            while (index >= 0) {
                String highlightOriginalWord = content.substring(index, index + optimizeWord.length());
                result.add(highlightOriginalWord);
                index = contentConvert.indexOf(optimizeWord, index + optimizeWord.length());
            }
        }
        return result;
    }

    public List<Email> getEmailsByMessageId(List<String> listMessageId){
        return emailDAO.findByMessageIdIn(listMessageId);
    }
}
