package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.enums.SentMailType;
import io.owslab.mailreceiver.model.SentMailHistory;
import org.apache.commons.lang.time.DateFormatUtils;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by khanhlvb on 6/28/18.
 */
public class SentMailHistoryDTO {
    public static final TimeZone TIME_ZONE = TimeZone.getTimeZone("Asia/Tokyo");

    private long id;
    private String from;
    private String subject;
    private String to;
    private String sentAt;
    private String originalReceivedAt;
    private String matchingReceivedAt;
    private String body;
    private String sendType;
    private String matchingMailAddress;
    private String username;
    private boolean hasAttachment;

    public SentMailHistoryDTO(SentMailHistory history, String username) {
        this.setId(history.getId());
        this.setFrom(history.getFrom());
        this.setSubject(history.getSubject());
        this.setTo(history.getTo());
        this.setBody(history.getBody());
        this.setSendType(SentMailType.fromValue(history.getSendType()).getText());
        this.setMatchingMailAddress(history.getMatchingMailAddress());
        if(history.getSentAt() != null)
            this.setSentAt(DateFormatUtils.format(history.getSentAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));
        if(history.getOriginalReceivedAt() != null)
            this.setOriginalReceivedAt(DateFormatUtils.format(history.getOriginalReceivedAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));
        if(history.getMatchingReceivedAt() != null)
            this.setMatchingReceivedAt(DateFormatUtils.format(history.getMatchingReceivedAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null));
        this.username = username;
        this.hasAttachment = history.isHasAttachment();
    }

    public long getId() { return id; }

    public void setId(long id) { this.id = id; }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getOriginalReceivedAt() {
        return originalReceivedAt;
    }

    public void setOriginalReceivedAt(String originalReceivedAt) {
        this.originalReceivedAt = originalReceivedAt;
    }

    public String getMatchingReceivedAt() {
        return matchingReceivedAt;
    }

    public void setMatchingReceivedAt(String matchingReceivedAt) {
        this.matchingReceivedAt = matchingReceivedAt;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSendType() {
        return sendType;
    }

    public void setSendType(String sendType) {
        this.sendType = sendType;
    }

    public String getMatchingMailAddress() {
        return matchingMailAddress;
    }

    public void setMatchingMailAddress(String matchingMailAddress) {
        this.matchingMailAddress = matchingMailAddress;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public boolean isHasAttachment() { return hasAttachment; }

    public void setHasAttachment(boolean hasAttachment) { this.hasAttachment = hasAttachment; }
}
