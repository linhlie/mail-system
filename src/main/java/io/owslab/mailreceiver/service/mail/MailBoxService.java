package io.owslab.mailreceiver.service.mail;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.dao.EmailDAO;
import io.owslab.mailreceiver.model.Email;
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
        return emailDAO.count();
    }

    public Page<Email> list(PageRequest pageRequest) {
        Page<Email> list = emailDAO.findAll(pageRequest);
        return list;
    }

    public Page<Email> searchContent(String search, PageRequest pageRequest) {
        if(search == null){
            return list(pageRequest);
        }
        String optimizeSearchText = optimizeText(search);
        Page<Email> list = emailDAO.findByOptimizedBodyIgnoreCaseContaining(optimizeSearchText, pageRequest);
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
