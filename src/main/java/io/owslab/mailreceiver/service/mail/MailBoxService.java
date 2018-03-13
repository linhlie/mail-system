package io.owslab.mailreceiver.service.mail;

import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.dao.FileDAO;
import io.owslab.mailreceiver.dto.DetailMailDTO;
import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;
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
    @Autowired
    private EmailDAO emailDAO;

    @Autowired
    private FileDAO fileDAO;

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
        return emailDAO.findByDeleted(false);
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
}
