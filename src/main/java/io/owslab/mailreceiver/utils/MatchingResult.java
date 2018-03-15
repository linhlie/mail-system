package io.owslab.mailreceiver.utils;

import io.owslab.mailreceiver.dto.EmailDTO;
import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/12/18.
 */
public class MatchingResult {
    private String word;
    private EmailDTO source;
    private List<EmailDTO> destinationList;

    public MatchingResult(String word, Email source) {
        this.word = word;
        this.source = new EmailDTO(source);
        this.destinationList = new ArrayList<>();
    }

    public MatchingResult(String word, EmailDTO source) {
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

    public EmailDTO getSource() {
        return source;
    }

    public void setSource(EmailDTO source) {
        this.source = source;
    }

    public List<EmailDTO> getDestinationList() {
        return destinationList;
    }

    public void setDestinationList(List<EmailDTO> destinationList) {
        this.destinationList = destinationList;
    }

    public boolean addDestination(Email destination){
        return this.destinationList.add(new EmailDTO(destination));
    }

    public void addDestinations(List<EmailDTO> destinationList){
        this.destinationList.addAll(destinationList);
    }
}
