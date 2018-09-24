package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.service.mail.EmailService;
import io.owslab.mailreceiver.utils.MatchingWordResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
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
    private WordService wordService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private FuzzyWordService fuzzyWordService;

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

    private boolean matchWordAND(String cacheId, String toSearch, String wordStr, boolean spaceEffective){
        List<String> words = Arrays.asList(wordStr.split(","));
        for(String word : words){
            if(!matchWord(cacheId, toSearch, word, spaceEffective)){
                return false;
            }
        }
        return true;
    }

    private boolean matchWordOR(String cacheId, String toSearch, String wordStr, boolean spaceEffective) {
        List<String> words = Arrays.asList(wordStr.split("!!"));
        for(String word : words){
            if(matchWordAND(cacheId, toSearch, word, spaceEffective)){
                return true;
            }
        }
        return false;
    }

    public MatchingWordResult matchWords(Email email, List<String> words, boolean spaceEffective, boolean distinguish){
        MatchingWordResult result = new MatchingWordResult(email);
        for(String word : words){
            if(matchWordOR(email.getMessageId(), email.getOptimizedText(distinguish), word, spaceEffective)){
                result.addMatchWord(word);
            }
        }
        return result;
    }
    
    public boolean matchingWordEngineer(Email email, List<String> words, boolean spaceEffective, boolean distinguish){
        boolean matching = false;
        for(String word : words){
            if(matchWordOR(email.getMessageId(), email.getOptimizedText(distinguish), word, spaceEffective)){
            	return true;
            }
        }
        return matching;
    }
}
