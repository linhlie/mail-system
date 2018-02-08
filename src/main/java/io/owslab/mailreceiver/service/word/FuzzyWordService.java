package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.FuzzyWordDAO;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by khanhlvb on 2/7/18.
 */

@Service
public class FuzzyWordService {
    @Autowired
    private FuzzyWordDAO fuzzyWordDAO;

    public void delete(long id){
        fuzzyWordDAO.delete(id);
    }

    public void save(FuzzyWord fuzzyWord){
        fuzzyWordDAO.save(fuzzyWord);
    }

    public FuzzyWord findOne(Word originalWord, Word associatedWord){
        long wordId = originalWord.getId();
        long withWordId = associatedWord.getId();
        List<FuzzyWord> fuzzyWordList1 = fuzzyWordDAO.findByWordIdAndWithWordId(wordId, withWordId);
        List<FuzzyWord> fuzzyWordList2 = fuzzyWordDAO.findByWordIdAndWithWordId(withWordId, wordId);
        return fuzzyWordList1.size() > 0 ? fuzzyWordList1.get(0) : (fuzzyWordList2.size() > 0 ? fuzzyWordList2.get(0) : null);
    }
}
