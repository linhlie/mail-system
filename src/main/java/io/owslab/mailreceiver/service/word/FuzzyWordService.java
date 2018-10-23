package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.FuzzyWordDAO;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.utils.KeyWordItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by khanhlvb on 2/7/18.
 */

@Service
@CacheConfig(cacheNames = "short_term_data")
public class FuzzyWordService {
    @Autowired
    private FuzzyWordDAO fuzzyWordDAO;

    @CacheEvict(allEntries = true)
    public void delete(long id){
        fuzzyWordDAO.delete(id);
    }

    @CacheEvict(allEntries = true)
    public void save(FuzzyWord fuzzyWord){
        fuzzyWordDAO.save(fuzzyWord);
    }

    @Cacheable(key="\"FuzzyWordService:findOne:\"+#originalWord.id+'-'+#associatedWord.id")
    public FuzzyWord findOne(Word originalWord, Word associatedWord){
        long wordId = originalWord.getId();
        long withWordId = associatedWord.getId();
        List<FuzzyWord> fuzzyWordList1 = fuzzyWordDAO.findByWordIdAndWithWordId(wordId, withWordId);
        List<FuzzyWord> fuzzyWordList2 = fuzzyWordDAO.findByWordIdAndWithWordId(withWordId, wordId);
        return fuzzyWordList1.size() > 0 ? fuzzyWordList1.get(0) : (fuzzyWordList2.size() > 0 ? fuzzyWordList2.get(0) : null);
    }

    @Cacheable(key="\"FuzzyWordService:findAllExclusionWord:\"+#word.id")
    public List<Word> findAllExclusionWord(Word word){
        List<Word> result = new ArrayList<Word>();
        long wordId = word.getId();
        List<FuzzyWord> fuzzyWordList1 = fuzzyWordDAO.findByWordIdAndFuzzyType(wordId, FuzzyWord.Type.EXCLUSION);
        for(FuzzyWord fuzzyWord : fuzzyWordList1){
            result.add(fuzzyWord.getAssociatedWord());
        }
        return result;
    }

    @Cacheable(key="\"FuzzyWordService:findKeyWordDeep:\"+#word.id")
    public Word findKeyWordDeep(Word word){
        Word result = null;
        long wordId = word.getId();
        List<FuzzyWord> fuzzyWordList = fuzzyWordDAO.findByWithWordIdAndFuzzyType(wordId, FuzzyWord.Type.SAME);
        if(fuzzyWordList.size() > 0){
            result = fuzzyWordList.get(0).getOriginalWord();
        }
        return result;
    }

    @Cacheable(key="\"FuzzyWordService:findKeyWord:\"+#word.id")
    public Word findKeyWord(Word word){
        Word result = null;
        long wordId = word.getId();
        List<FuzzyWord> fuzzyWordList = fuzzyWordDAO.findByWithWordIdAndFuzzyType(wordId, FuzzyWord.Type.SAME);
        if(fuzzyWordList.size() > 0){
            result = fuzzyWordList.get(0).getOriginalWord();
            Word rootKeyWord = findKeyWordDeep(result);
            if(rootKeyWord != null) {
                result = rootKeyWord;
            }
        } else {
            result = word;
        }
        return result;
    }

    @Cacheable(key="\"FuzzyWordService:findAllSameWord:\"+#word.id")
    public List<Word> findAllSameWord(Word word){
        return findAllFuzzyWord(word, FuzzyWord.Type.SAME);
    }

    private List<Word> findAllFuzzyWord(Word word, int fuzzyType){
        List<Word> result = new ArrayList<Word>();
        long wordId = word.getId();
        List<FuzzyWord> fuzzyWordList1 = fuzzyWordDAO.findByWordIdAndFuzzyType(wordId, fuzzyType);
        List<FuzzyWord> fuzzyWordList2 = fuzzyWordDAO.findByWithWordIdAndFuzzyType(wordId, fuzzyType);
        for(FuzzyWord fuzzyWord : fuzzyWordList1){
            result.add(fuzzyWord.getAssociatedWord());
        }
        for(FuzzyWord fuzzyWord : fuzzyWordList2){
            result.add(fuzzyWord.getOriginalWord());
        }
        return result;
    }

    public List<KeyWordItem> searchWord(Word word){
        Set<Word> result = new LinkedHashSet<>();
        List<KeyWordItem> listWord = new ArrayList<>();
        result.add(word);
        getWords(word,result);
        for(Word w : result){
            listWord.add(new KeyWordItem(w.getWord(), word.getWord()));
        }
        return listWord;
    }

    public void getWords(Word word, Set<Word> result){
        for(FuzzyWord fuzzyWord : word.getOriginalWords()){
            if(fuzzyWord.getFuzzyType() == FuzzyWord.Type.SAME){
                if(!result.contains(fuzzyWord.getAssociatedWord())){
                    result.add(fuzzyWord.getAssociatedWord());
                    getWords(fuzzyWord.getAssociatedWord(), result);
                }
            }
        }
        for(FuzzyWord fuzzyWord : word.getAssociatedWords()){
            if(!result.contains(fuzzyWord.getOriginalWord())){
                result.add(fuzzyWord.getOriginalWord());
                getWords(fuzzyWord.getOriginalWord(), result);
            }
        }
    }

}
