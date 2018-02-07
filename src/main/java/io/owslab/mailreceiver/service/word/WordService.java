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
        List<Word> wordList = wordDAO.findByWord(word);
        if (wordList.size() > 0){
            return wordList.get(0);
        }
        return null;
    }
}
