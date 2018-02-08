package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.WordDAO;
import io.owslab.mailreceiver.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/7/18.
 */

@Service
public class WordService {

    @Autowired
    private WordDAO wordDAO;

    public Word findOne(String word){
        String normalizedWord = word == null ? word : word.toLowerCase();
        List<Word> wordList = wordDAO.findByWord(normalizedWord);
        if (wordList.size() > 0){
            return wordList.get(0);
        }
        return null;
    }

    public void save(Word word){
        wordDAO.save(word);
    }
}
