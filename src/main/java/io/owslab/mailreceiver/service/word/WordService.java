package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.WordDAO;
import io.owslab.mailreceiver.form.AddListWordForm;
import io.owslab.mailreceiver.form.EditWordForm;
import io.owslab.mailreceiver.model.FuzzyWord;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.utils.KeyWordItem;
import io.owslab.mailreceiver.utils.Utils;
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
public class WordService {

    @Autowired
    private WordDAO wordDAO;

    @Autowired
    private FuzzyWordService fuzzyWordService;

    @CacheEvict(allEntries = true)
    public void delete(Word word){
        wordDAO.delete(word);
    }

    @Cacheable(key="\"WordService:findAll\"")
    public List<Word> findAll(){
        return (List<Word>) wordDAO.findAll();
    }

    @Cacheable(key="\"WordService:findOne:\"+#word")
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

    @Cacheable(key="\"WordService:findById:\"+#id")
    public Word findById(long id){
        List<Word> wordList = wordDAO.findById(id);
        return wordList.size() > 0 ? wordList.get(0) : null;
    }

    @CacheEvict(allEntries = true)
    public void save(Word word){
        wordDAO.save(word);
    }

    //need transaction
    public void editGroupWord(EditWordForm form) throws Exception {
        List<Word> oldWords = getListWordinGroup(form.getOldWord());
        List<Word> newWords = getListWordinGroup(form.getNewWord());

        if(oldWords.size()==0){
            throw new Exception("グループ名が存在しない、又は消除された");
        }

        if(newWords.size() > 0){
            throw new Exception("言葉が既存の同義語のグループにある");
        }else{
            wordDAO.editGroup(form.getOldWord(), form.getNewWord());
        }
    }

    public void deleteGroupWord(String group){
        List<Word> listWord = getListWordinGroup(group);
        for(Word word : listWord){
            List<FuzzyWord> checkFuzzyword = fuzzyWordService.findByAssociatedWord(word);
            if(checkFuzzyword.size()>0){
                word.setGroupWord(null);
                wordDAO.save(word);
            }else{
                delete(word);
            }
        }
    }

    public int deleteWordInGroup(String w){
        Word word = findOne(w);
        if(word != null){
            String group = word.getGroupWord();
            List<Word> words = getListWordinGroup(group);
            List<FuzzyWord> checkFuzzyword = fuzzyWordService.findByAssociatedWord(word);
            if(checkFuzzyword.size()>0){
                word.setGroupWord(null);
                wordDAO.save(word);
            }else{
                delete(word);
            }

            return words.size()-1;
        }
        return 0;
    }

    public String normalize(String word){
        return word == null ? "" : Utils.normalize(word);
    }

    public List<Word> getListWordByGroup(){
        return wordDAO.findWordsGroupNotNull();
    }

    public List<Word> getListWordinGroup(String group){
        return wordDAO.findByGroupWord(group);
    }


    public List<Word> searchWord(String wordValue){
        if(wordValue==null || wordValue.equals("")){
            return getListWordByGroup();
        }
        List<Word> listWordGroup = new ArrayList<>();
        listWordGroup = getListWordinGroup(wordValue);
        if(listWordGroup!=null && listWordGroup.size()>0){
            return listWordGroup;
        }
        List<Word> listWord = new ArrayList<>();
        Word word = findOne(wordValue);
        if(word == null || word.getGroupWord() == null){
            return listWord;
        }else{
            return getListWordinGroup(word.getGroupWord());
        }
    }

    //need transaction
    public void editWord(EditWordForm form) throws Exception {
        Word word = findOne(form.getOldWord());
        Word newWord = findOne(form.getNewWord());

        if(word == null){
            throw new Exception("言葉が存在しない、又は消除された");
        }

        if(newWord != null){
            if(newWord.getGroupWord()!=null){
                throw new Exception("言葉が既存の同義語のグループにある");
            }else{
                newWord.setGroupWord(word.getGroupWord());
                word.setGroupWord(null);
                save(newWord);
                save(word);
            }
        }else{
            word.setWord(form.getNewWord());
            save(word);
        }
    }

    public void addWord(Word word) throws Exception {
        Word checkWord = findOne(word.getWord());

        if(checkWord!=null){
            if(checkWord.getGroupWord()!=null){
                throw new Exception("言葉が既存の同義語のグループにある");
            }else{
                checkWord.setGroupWord(word.getGroupWord());
                save(checkWord);
            }
        }else{
            save(word);
        }
    }

    public void addListWord(AddListWordForm form) throws Exception {
        List<Word> words = getListWordinGroup(form.getGroupWord());
        List<Word> wordsSave = new ArrayList<>();
        if(words.size()>0){
            throw new Exception("グループ名は既に存在します");
        }else{
            for(String w : form.getListWord()){
                Word word = findOne(w);
                if(word!=null && word.getGroupWord() != null){
                    throw new Exception("言葉が既存の同義語のグループにある");
                }else{
                    if(word!=null){
                        word.setGroupWord(form.getGroupWord());
                        wordsSave.add(word);
                    }else{
                        Word newWord = new Word();
                        newWord.setWord(w);
                        newWord.setGroupWord(form.getGroupWord());
                        wordsSave.add(newWord);
                    }
                }
            }
            wordDAO.save(wordsSave);
        }
    }

    public long countGroupWord(){
        long count  = wordDAO.countByGroupWordIsNotNull();
        return count;
    }

    public void configGroupWord(){
        List<Word> wordList = (List<Word>) wordDAO.findAll();
        getDefaultListWord(wordList);
    }

    public void getDefaultListWord(List<Word> wordList){
        LinkedHashMap<Long, Word> listDefault = new LinkedHashMap<>();
        for(Word word : wordList){
            if(!listDefault.containsKey(word.getId())){
                LinkedHashMap<Long, Word> listWordSame = new LinkedHashMap<>();
                listWordSame.put(word.getId(), word);
                fuzzyWordService.getAllSameWordOld(word,listWordSame);
                if(listWordSame.size()>0){
                    long min = Long.MAX_VALUE;
                    String rootWord = "";
                    for(Word w : listWordSame.values()){
                        if(w.getId()<min){
                            min = w.getId();
                            rootWord = w.getWord();
                        }
                    }
                    for(Word w : listWordSame.values()){
                        if(!listDefault.containsKey(w.getId())){
                            w.setGroupWord(rootWord);
                            listDefault.put(w.getId(), w);
                        }
                    }
                }else{
                    word.setGroupWord(word.getWord());
                    listDefault.put(word.getId(), word);
                }
            }
        }
        List<Word> result = new ArrayList<Word>();
        SortedSet<Long> keys = new TreeSet<>(listDefault.keySet());
        for (long key : keys) {
            result.add(listDefault.get(key));
        }
        wordDAO.save(result);
    }
}
