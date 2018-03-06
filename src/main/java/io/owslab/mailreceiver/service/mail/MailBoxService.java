package io.owslab.mailreceiver.service.mail;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
public class MailBoxService {
    @Autowired
    private EmailDAO emailDAO;

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
        return optimizedText.toLowerCase();
    }
}
