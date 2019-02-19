package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.utils.ConvertDate;
import io.owslab.mailreceiver.utils.ConvertDomain;

import java.util.ArrayList;
import java.util.List;

public class EmailStatisticDTO {
    private String messageId;
    private String date;
    private String hour;
    private String domain;
    private String word;
    private int count;
    private List<String> listMessageId;

    public EmailStatisticDTO(){

    }

    public EmailStatisticDTO(Email email,String word, boolean isDate, boolean isHour, boolean isFrom, boolean isWord){
        this.date = ConvertDate.convertDateToMMdd(email.getSentAt());
        this.hour = ConvertDate.convertHourStatistic(email.getSentAt());
        this.domain = ConvertDomain.convertEmailToDomain(email.getFrom());
        this.word = word;
        this.messageId = email.getMessageId();
        this.count = 1;
        this.listMessageId = new ArrayList<>();
        addMessageId(email.getMessageId());

        if(!isDate){
            this.date = "";
        }

        if(!isHour){
            this.hour = "";
        }

        if(!isFrom){
            this.domain = "";
        }

        if(!isWord){
            this.word = "";
        }
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getHour() {
        return hour;
    }

    public void setHour(String hour) {
        this.hour = hour;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getListMessageId() {
        return listMessageId;
    }

    public void setListMessageId(List<String> listMessageId) {
        this.listMessageId = listMessageId;
    }

    public void addMessageId(String messageId) {
        this.listMessageId.add(messageId);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
