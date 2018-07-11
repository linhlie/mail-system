package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.EmailWordJobDAO;
import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.EmailWord;
import io.owslab.mailreceiver.model.EmailWordJob;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.service.mail.EmailService;
import io.owslab.mailreceiver.utils.MatchingWordResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;
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
//        List<EmailWordJob> emailWordJobList = (List<EmailWordJob>) emailWordJobDAO.findAll();
//        for(EmailWordJob emailWordJob : emailWordJobList){
//            build(emailWordJob);
//        }
    }

    private void build(EmailWordJob emailWordJob){
        String messageId = emailWordJob.getMessageId();
        long wordId = emailWordJob.getWordId();
        Email email = emailService.findOne(messageId, false);
        if(email == null) return;
        Word word = wordService.findById(wordId);
        if(word == null) return;
//        ArrayList<Integer> result = find(email.getMessageId(), email.getOptimizedBody(), word.getWord());
//        if(result.size() > 0){
//            String resultStr = result.toString();
//            resultStr = resultStr.substring(1, resultStr.length()-1);
//            resultStr = resultStr.replaceAll("\\s","");
//            EmailWord emailWord = new EmailWord();
//            emailWord.setMessageId(messageId);
//            emailWord.setWordId(wordId);
//            emailWord.setAppearIndexs(resultStr);
//            emailWordService.save(emailWord);
//        }
        emailWordJobDAO.delete(emailWordJob.getId());
    }

    @Cacheable(key="\"EmailWordJobService:find:\"+#cacheId+'-'+toFind+'-'+spaceEffective")
    private ArrayList<Integer> find(String cacheId, String toSearch, String toFind, boolean spaceEffective){
        ArrayList<Integer> result = new ArrayList<Integer>();
        Matcher matcher = Pattern.compile(toFind, Pattern.LITERAL).matcher(toSearch);
        boolean bFound = matcher.find();
        while (bFound) {
            result.add(matcher.start());
            bFound = matcher.find(matcher.start() + 1);
        }
        return result;
    }

    //TODO: need test
    //TODO: process key fullwidth / halfwidth or not

    public boolean matchWord(String cacheId, String toSearch, String wordStr, boolean spaceEffective){
        Word word = wordService.findOne(wordStr);
        List<Integer> result;
        if(!spaceEffective){
            toSearch = toSearch.replaceAll(" ", "");
            wordStr = wordStr.replaceAll("　", "");
            wordStr = wordStr.replaceAll("\\s+","");
        }
        if(word == null){
            result = find(cacheId, toSearch, wordStr, spaceEffective);
        } else {
            result = find(cacheId, toSearch, wordStr, spaceEffective);
            List<Word> exclusionWords = fuzzyWordService.findAllExclusionWord(word);
            List<Word> sameWords = fuzzyWordService.findAllSameWord(word);
            List<Integer> exclusionResult = findWithWordList(cacheId, toSearch, exclusionWords, spaceEffective);
            List<Integer> sameResult = findWithWordList(cacheId, toSearch, sameWords, spaceEffective);
            for(Integer num : exclusionResult){
                int index = result.indexOf(num);
                if(index >= 0){
                    result.remove(index);
                }
            }
            result.addAll(sameResult);
        }
        return !result.isEmpty();
    }

    private ArrayList<Integer> findWithWordList(String cacheId, String toSearch, List<Word> wordList, boolean spaceEffective){
        ArrayList<Integer> result =  new ArrayList<Integer>();
        for(Word word : wordList){
            String toFind = word.getWord();
            if(!spaceEffective){
                toFind = toFind.replaceAll("　", "");
                toFind = toFind.replaceAll("\\s+","");
            }
            ArrayList<Integer> findResult = find(cacheId, toSearch, toFind, spaceEffective);
            result.addAll(findResult);
        }
        return result;
    }

    public MatchingWordResult matchWords(Email email, List<String> words, boolean spaceEffective, boolean distinguish){
        MatchingWordResult result = new MatchingWordResult(email);
        for(String word : words){
            if(matchWord(email.getMessageId(), email.getOptimizedText(distinguish), word, spaceEffective)){
                result.addMatchWord(word);
            }
        }
        return result;
    }
}
