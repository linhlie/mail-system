package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.model.Email;

import java.util.List;

/**
 * Created by khanhlvb on 3/8/18.
 */
public class MatchingWordResult {
    private String word;
    private List<Email> source;
    private List<Email> destination;

    public MatchingWordResult(String word, List<Email> source, List<Email> destination) {
        this.word = word;
        this.source = source;
        this.destination = destination;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<Email> getSource() {
        return source;
    }

    public void setSource(List<Email> source) {
        this.source = source;
    }

    public List<Email> getDestination() {
        return destination;
    }

    public void setDestination(List<Email> destination) {
        this.destination = destination;
    }
}
