package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Email;
import io.owslab.mailreceiver.utils.FullNumberRange;
import org.apache.commons.lang.time.DateFormatUtils;

public class PreviewMailDTO {
    private long accountId;

    private String from;

    private String subject;

    private String to;

    private String sentAt;

    private String receivedAt;

    public PreviewMailDTO(Email email) {
        this.setAccountId(email.getAccountId());
        this.setFrom(email.getFrom());
        this.setSubject(email.getSubject());
        this.setTo(email.getTo());
        this.setSentAt(DateFormatUtils.format(email.getSentAt(), "yyyy-MM-dd"));
        this.setReceivedAt(DateFormatUtils.format(email.getReceivedAt(), "yyyy-MM-dd"));
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

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

    public String getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(String receivedAt) {
        this.receivedAt = receivedAt;
    }
}