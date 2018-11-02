package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.FuzzyWordDAO;
import io.owslab.mailreceiver.dto.FuzzyWordDTO;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
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

    @Autowired
    private WordService wordService;

    @CacheEvict(allEntries = true)
    public void deleteFuzzyWord(long id){
        fuzzyWordDAO.delete(id);
    }

    public List<FuzzyWord> findByAssociatedWord(Word word){
        return fuzzyWordDAO.findByAssociatedWord(word);
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

    @Cacheable(key="\"FuzzyWordService:findAllSameWord:\"+#word.id")
    public List<Word> findAllSameWord(Word word){
        if(word!=null && word.getGroupWord()!=null){
            return wordService.getListWordinGroup(word.getGroupWord());
        }
        return null;
    }

    public List<FuzzyWordDTO> getExclusion(List<Word> listWordSame){
        List<FuzzyWord> fuzzyWordList = (List<FuzzyWord>) fuzzyWordDAO.findAll();
        List<FuzzyWordDTO> listResult = new ArrayList<>();
        for(Word wordSame: listWordSame){
            for(FuzzyWord fuzzy : fuzzyWordList){
                if(fuzzy.getOriginalWord().getId() == wordSame.getId() && fuzzy.getFuzzyType() == FuzzyWord.Type.EXCLUSION){
                    listResult.add(new FuzzyWordDTO(fuzzy.getId(), fuzzy.getOriginalWord().getWord(), fuzzy.getAssociatedWord().getWord()));
                }
            }
        }
        return listResult;
    }

    public void addFuzzyWord(FuzzyWordDTO fuzzyWordDTO) throws Exception {
        Word originalWord = wordService.findOne(fuzzyWordDTO.getWord());
        Word associatedWord = wordService.findOne(fuzzyWordDTO.getWordExclusion());
        if(originalWord == null){
            throw new Exception("主ワードが存在しない、又は消除された");
        }
        if(associatedWord != null) {
            if(originalWord.getGroupWord().equals(associatedWord.getGroupWord())){
                throw new Exception("除外単語と主ワードは同じ類似グループにしてはいけません。");
            }
            FuzzyWord existFuzzyWord = findOne(originalWord, associatedWord);
            if(existFuzzyWord != null){
                System.out.println(existFuzzyWord.getId()+" "+existFuzzyWord.getFuzzyType());
                throw new Exception("データは既に存在します");
            }
        } else {
            associatedWord = new Word();
            associatedWord.setWord(fuzzyWordDTO.getWordExclusion());
            wordService.save(associatedWord);
        }
        FuzzyWord fuzzyWord = new FuzzyWord(originalWord, associatedWord, 0);
        save(fuzzyWord);
    }

}
