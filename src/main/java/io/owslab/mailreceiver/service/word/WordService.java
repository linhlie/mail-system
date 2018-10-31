package io.owslab.mailreceiver.service.word;

import io.owslab.mailreceiver.dao.WordDAO;
import io.owslab.mailreceiver.form.EditWordForm;
import io.owslab.mailreceiver.model.Word;
import io.owslab.mailreceiver.utils.KeyWordItem;
import io.owslab.mailreceiver.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by khanhlvb on 2/7/18.
 */

@Service
@CacheConfig(cacheNames = "short_term_data")
public class WordService {

    @Autowired
    private WordDAO wordDAO;

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

    public void deleteGroupWord(String group){
        wordDAO.deleteGroup(group);
    }

    public int deleteWordInGroup(String w){
        Word word = findOne(w);
        if(word != null){
            String group = word.getGroupWord();
            List<Word> words = getListWordinGroup(group);
            System.out.println(group+"  "+words.size());
            word.setGroupWord(null);
            wordDAO.save(word);
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

    public void editWord(EditWordForm form) throws Exception {
        Word word = findOne(form.getOldWord());
        Word newWord = findOne(form.getNewWord());

        if(word == null || newWord != null){
            throw new Exception("Word esxit");
        }else{
            word.setWord(form.getNewWord());
            save(word);
        }
    }

    public void addWord(Word word) throws Exception {
        Word checkWord = findOne(word.getWord());

        if(checkWord!=null){
            if(checkWord.getGroupWord()!=null){
                throw new Exception("Word esxit");
            }else{
                checkWord.setGroupWord(word.getGroupWord());
                save(checkWord);
            }
        }else{
            save(word);
        }
    }

}
