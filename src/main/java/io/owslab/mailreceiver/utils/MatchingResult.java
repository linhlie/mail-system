package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/12/18.
 */
public class MatchingResult {
    private String word;
    private Email source;
    private List<Email> destinationList;

    public MatchingResult(String word, Email source) {
        this.word = word;
        this.source = source;
        this.destinationList = new ArrayList<>();
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Email getSource() {
        return source;
    }

    public void setSource(Email source) {
        this.source = source;
    }

    public List<Email> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(List<Email> destinationList) {
        this.destinationList = destinationList;
    }

    public boolean addDestination(Email destination){
        return this.destinationList.add(destination);
    }
}
