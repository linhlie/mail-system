package io.owslab.mailreceiver.service.mail;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 1/26/18.
 */
@Service
public class MailBoxService {
    @Autowired
    private EmailDAO emailDAO;

    public long count(){
        return emailDAO.count();
    }

    public List<Email> list() {
        List<Email> list = (List<Email>) emailDAO.findAll();
        return list;
    }

    public List<Email> searchContent(String search) {
        if(search == null){
            return list();
        }
        String optimizeSearchText = optimizeText(search);
        List<Email> list = emailDAO.findByOptimizedBodyIgnoreCaseContaining(optimizeSearchText);
        return list;
    }

    public static String optimizeText(String original){
        int conv_op_flags = 0;
        conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
        conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
        String optimizedText = KanaConverter.convertKana(original, conv_op_flags);
        return  optimizedText.toLowerCase();
    }
}
