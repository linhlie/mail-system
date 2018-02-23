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
        String normalizedWord = this.normalize(word);
        if(!normalizedWord.isEmpty()){
            List<Word> wordList = wordDAO.findByWord(normalizedWord);
            if (wordList.size() > 0){
                return wordList.get(0);
            }
        }
        return null;
    }

    public Word findById(long id){
        List<Word> wordList = wordDAO.findById(id);
        return wordList.size() > 0 ? wordList.get(0) : null;
    }

    public void save(Word word){
        wordDAO.save(word);
    }

    public String normalize(String word){
        //TODO: we need better normalize for japanese
        return word == null ? "" : word.toLowerCase();
    }
}
