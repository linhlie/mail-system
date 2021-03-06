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

    public FuzzyWord findById(Long id){
        return fuzzyWordDAO.findOne(id);
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
        List<Word> words = new ArrayList<>();
        if(word!=null && word.getGroupWord()!=null){
            words = wordService.getListWordinGroup(word.getGroupWord());
            int index = words.indexOf(word);
            words.remove(index);
        }
        return words;
    }

    public List<Word> findAllInGroup(Word word){
        List<Word> words = new ArrayList<>();
        if(word!=null && word.getGroupWord()!=null){
            words = wordService.getListWordinGroup(word.getGroupWord());
        }
        return words;
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

    public void deleteFuzzyWordAPI(long id){
        FuzzyWord fuzzyWord = findById(id);
        if(fuzzyWord!=null){
            Word wordExclusion = fuzzyWord.getAssociatedWord();
            if(wordExclusion.getGroupWord()==null){
                List<FuzzyWord> checkWord = findByAssociatedWord(wordExclusion);
                if(checkWord.size()==1){
                    wordService.delete(wordExclusion);
                }else{
                    deleteFuzzyWord(id);
                }
            }else{
                deleteFuzzyWord(id);
            }
        }
    }

    public void getAllSameWordOld(Word word, LinkedHashMap<Long, Word> result){
        List<FuzzyWord> fuzzyWordSame1 = fuzzyWordDAO.findByWordIdAndFuzzyType(word.getId(),FuzzyWord.Type.SAME);
        for(FuzzyWord fuzzyWord : fuzzyWordSame1){
                if(!result.containsKey(fuzzyWord.getAssociatedWord().getId())){
                    result.put(fuzzyWord.getAssociatedWord().getId(), fuzzyWord.getAssociatedWord());
                    getAllSameWordOld(fuzzyWord.getAssociatedWord(), result);
                }
        }

        List<FuzzyWord> fuzzyWordSame2 = fuzzyWordDAO.findByWithWordIdAndFuzzyType(word.getId(),FuzzyWord.Type.SAME);
        for(FuzzyWord fuzzyWord : fuzzyWordSame2){
            if(!result.containsKey(fuzzyWord.getOriginalWord().getId())){
                result.put(fuzzyWord.getOriginalWord().getId(), fuzzyWord.getOriginalWord());
                getAllSameWordOld(fuzzyWord.getOriginalWord(), result);
            }
        }
    }
}
