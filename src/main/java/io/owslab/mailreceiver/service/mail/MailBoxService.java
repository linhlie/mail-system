package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.form.SendAccountForm;
import io.owslab.mailreceiver.model.*;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.utils.FullNumberRange;
import io.owslab.mailreceiver.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.safety.Whitelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
@CacheConfig(cacheNames = "short_term_data")
public class MailBoxService {
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
    
    private List<Email> cachedEmailList = null;

    public long count(){
        return emailDAO.countByDeleted(false);
    }

    public Page<Email> list(PageRequest pageRequest) {
        Page<Email> list = emailDAO.findByDeleted(false, pageRequest);
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

    public List<Email> getAll(){
        return getAll(false);
    }

    public List<Email> getAll(boolean forceUpdate){
        if(forceUpdate || cachedEmailList == null){
            cachedEmailList = emailDAO.findByDeleted(false);
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

    public List<DetailMailDTO> getMailDetail(String messageId){
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> emailList = emailDAO.findByMessageIdAndDeleted(messageId, false);
        for(Email email : emailList) {
            DetailMailDTO result = new DetailMailDTO(email);
            List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
            for(AttachmentFile file : files){
                result.addFile(file);
            }
            results.add(result);
        }
        return results;
    }

    public List<DetailMailDTO> getContentRelyEmail(String replyId) throws Exception {
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> replyList = emailDAO.findByMessageIdAndDeleted(replyId, false);
        Email replyEmail = replyList.size() > 0 ? replyList.get(0) : null;
        if(replyEmail == null) {
            throw new Exception("This mail has been deleted or does not exist");
        }
        List<EmailAccount> listAccount = mailAccountsService.findById(replyEmail.getAccountId());
        EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
        if(emailAccount == null) {
            throw new Exception("Missing sender account info. Can't reply this email");
        }
        SendAccountForm sendAccountForm = emailAccountSettingService.getSendAccountForm(emailAccount.getId());
        if(sendAccountForm == null) {
            throw new Exception("Missing sender account info. Can't reply this email");
        }
        DetailMailDTO result = new DetailMailDTO(replyEmail, emailAccount.getAccount());
        result.setExternalCC(sendAccountForm.getCc());
        String signature = emailAccount.getSignature().length() > 0 ? "<br>--<br>" + emailAccount.getSignature() : "";
        result.setSignature(signature);
        result.setExcerpt("");
        String replyText = getReplyContentFromEmail(replyEmail);
        result.setReplyOrigin(replyText);
        result.setSubject("Re: " + replyEmail.getSubject());
        results.add(result);
        return results;
    }

    public List<DetailMailDTO> getMailDetailWithReplacedRange(String messageId, String replyId, String rangeStr, String matchRangeStr, int replaceType){
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> emailList = emailDAO.findByMessageIdAndDeleted(messageId, false);
        List<Email> replyList = emailDAO.findByMessageIdAndDeleted(replyId, false);
        Email replyEmail = replyList.size() > 0 ? replyList.get(0) : null;
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
        for(Email email : emailList) {
            List<EmailAccount> listAccount = mailAccountsService.findById(email.getAccountId());
            EmailAccount emailAccount = listAccount.size() > 0 ? listAccount.get(0) : null;
            SendAccountForm sendAccountForm = emailAccountSettingService.getSendAccountForm(emailAccount.getId());
            DetailMailDTO result = emailAccount == null ? new DetailMailDTO(email) : new DetailMailDTO(email, emailAccount.getAccount());
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
            }
            List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
            for(AttachmentFile file : files){
                result.addFile(file);
            }
            results.add(result);
        }
        return results;
    }

    private String getExcerpt(Email email){
        String optimizedBody = email.getOptimizedBody();
        String[] optimizedBodyLines = optimizedBody.split("\n");
        String excerpt = "";
        int i = 0;
        for(String line: optimizedBodyLines) {
            if(!line.isEmpty() && !line.equalsIgnoreCase("\r") && !line.equalsIgnoreCase("\n")) {
                excerpt = excerpt + getExceprtLine(line);
                i++;
                if(i == 5) {
                    break;
                }
            }
        }
        excerpt = excerpt + getExceprtLine("---------------------");
        excerpt = excerpt + "<br/><br/><br/><br/><br/>";
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
}
