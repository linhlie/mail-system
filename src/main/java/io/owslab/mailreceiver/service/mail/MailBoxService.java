package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.NumberTreatment;
import io.owslab.mailreceiver.service.replace.NumberRangeService;
import io.owslab.mailreceiver.service.replace.NumberTreatmentService;
import io.owslab.mailreceiver.service.settings.MailAccountsService;
import io.owslab.mailreceiver.utils.FullNumberRange;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
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

    public static String optimizeText(String original){
        String optimizedText = Jsoup.parse(original).text();
//        int conv_op_flags = 0;
//        conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
//        conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
//        String japaneseOptimizedText = KanaConverter.convertKana(optimizedText, conv_op_flags);
//        return japaneseOptimizedText.toLowerCase();
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

    public List<DetailMailDTO> getMailDetailWithReplacedRange(String messageId, String rangeStr, int replaceType){
        List<DetailMailDTO> results = new ArrayList<>();
        List<Email> emailList = emailDAO.findByMessageIdAndDeleted(messageId, false);
        List<FullNumberRange> fullNumberRanges = numberRangeService.buildNumberRangeForInput(rangeStr, rangeStr, false, false);
        FullNumberRange firstRange = fullNumberRanges.size() > 0 ? fullNumberRanges.get(0) : null;
        NumberTreatment numberTreatment = numberTreatmentService.getFirst();
        double ratio = 1;
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
            DetailMailDTO result = emailAccount == null ? new DetailMailDTO(email) : new DetailMailDTO(email, emailAccount.getAccount());
            String signature = emailAccount == null ? "" : "<br>--<br>" + emailAccount.getSignature();
            if(rangeStr != null && firstRangeStr != null){
                String rawBody = result.getOriginalBody();
                String replacedBody = replaceAllContent(rawBody, rangeStr, firstRangeStr);
                result.setReplacedBody(replacedBody);
            }
            result.setSignature(signature);
            List<AttachmentFile> files = fileDAO.findByMessageIdAndDeleted(messageId, false);
            for(AttachmentFile file : files){
                result.addFile(file);
            }
            results.add(result);
        }
        return results;
    }

    private String replaceAllContent(String source, String regex, String replacement){
        String styleReplacement = "<span style=\"color: " + HIGHLIGHT_RANGE_COLOR + ";\">" + replacement + "</span>";

        String replaced = source.replaceAll(regex, styleReplacement);
        return replaced;
    }
}
