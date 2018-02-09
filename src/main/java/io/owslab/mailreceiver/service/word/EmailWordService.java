package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.EmailWordDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailWord;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.service.mail.EmailService;
import io.owslab.mailreceiver.types.EmailWordState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Service
public class EmailWordService {
    private static final Logger logger = LoggerFactory.getLogger(EmailWordService.class);
    @Autowired
    private EmailWordDAO emailWordDAO;

    @Autowired
    private WordService wordService;

    @Autowired
    private EmailService emailService;

    public void buildMatchData(){
        List<EmailWord> emailWordList = emailWordDAO.findByState(EmailWordState.NEW);
        for(EmailWord emailWord : emailWordList){
            build(emailWord);
        }
    }

    private void build(EmailWord emailWord){
        String messageId = emailWord.getMessageId();
        long wordId = emailWord.getWordId();
        Email email = emailService.findOne(messageId, false);
        if(email == null) return;
        Word word = wordService.findById(wordId);
        if(word == null) return;
        logger.info("Start build match data: " + email.getSubject() + "|||" + word.getWord());
        //TODO: build array match and update state of emailWord to DONE
    }
}
