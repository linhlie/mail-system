package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.EmailWordJobDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailWord;
import io.owslab.mailreceiver.model.EmailWordJob;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.service.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Service
public class EmailWordJobService {
    private static final Logger logger = LoggerFactory.getLogger(EmailWordJobService.class);
    @Autowired
    private EmailWordJobDAO emailWordJobDAO;

    @Autowired
    private WordService wordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailWordService emailWordService;

    @Autowired
    private FuzzyWordService fuzzyWordService;

    public void buildMatchData(){
        List<EmailWordJob> emailWordJobList = (List<EmailWordJob>) emailWordJobDAO.findAll();
        for(EmailWordJob emailWordJob : emailWordJobList){
            build(emailWordJob);
        }
    }

    private void build(EmailWordJob emailWordJob){
        String messageId = emailWordJob.getMessageId();
        long wordId = emailWordJob.getWordId();
        Email email = emailService.findOne(messageId, false);
        if(email == null) return;
        Word word = wordService.findById(wordId);
        if(word == null) return;
        ArrayList<Integer> result = find(email.getOptimizedBody(), word.getWord());
        if(result.size() > 0){
            String resultStr = result.toString();
            resultStr = resultStr.substring(1, resultStr.length()-1);
            resultStr = resultStr.replaceAll("\\s","");
            EmailWord emailWord = new EmailWord();
            emailWord.setMessageId(messageId);
            emailWord.setWordId(wordId);
            emailWord.setAppearIndexs(resultStr);
            emailWordService.save(emailWord);
        }
        emailWordJobDAO.delete(emailWordJob.getId());
    }

    private ArrayList<Integer> find(String toSearch, String toFind){
        ArrayList<Integer> result = new ArrayList<Integer>();
        Matcher matcher = Pattern.compile(toFind, Pattern.LITERAL).matcher(toSearch);
        boolean bFound = matcher.find();
        while (bFound) {
            result.add(matcher.start());
            bFound = matcher.find(matcher.start() + 1);
        }
        return result;
    }

    private boolean matchWord(String toSearch, String wordStr){
        Word word = wordService.findOne(wordStr);
        ArrayList<Integer> result =  new ArrayList<Integer>();
        if(word == null){
            result = find(toSearch, wordStr);
        } else {
            result = find(toSearch, wordStr);
            List<Word> exclusionWords = fuzzyWordService.findAllExclusionWord(word);
            List<Word> sameWords = fuzzyWordService.findAllSameWord(word);
            //TODO: find ngoai tru
            //TODO: find dong nghia
        }
        return !result.isEmpty();
    }
}
