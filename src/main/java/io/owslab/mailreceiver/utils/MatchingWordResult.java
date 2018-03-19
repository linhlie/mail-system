package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/8/18.
 */
public class MatchingWordResult {
    private Email email;
    private List<String> words;

    public MatchingWordResult(Email email, List<String> words) {
        this.email = email;
        this.words = words;
    }

    public MatchingWordResult(Email email) {
        this.email = email;
        this.words = new ArrayList<>();
    }

    public Email getEmail() {
        return email;
    }

    public void setEmail(Email email) {
        this.email = email;
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public boolean addMatchWord(String word){
        if(!this.words.contains(word)){
            return this.words.add(word);
        }
        return false;
    }

    public boolean hasMatchWord(){
        return (this.words != null && this.words.size() > 0);
    }

    public List<String> intersect (MatchingWordResult other){
        List<String> list1 = this.getWords();
        List<String> list2 = other.getWords();
        List<String> list = list1.size() >= list2.size() ? list2 : list1;
        List<String> remainList = list1.size() >= list2.size() ? list1 : list2;
        List<String> result = new ArrayList<>();
        for(String word: list){
            if(remainList.contains(word)){
                result.add(word);
            }
        }
        return result;
    }

    public boolean contain(String word){
        return this.getWords().contains(word);
    }
}
