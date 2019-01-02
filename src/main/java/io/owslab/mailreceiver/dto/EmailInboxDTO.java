package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Email;
import org.apache.commons.lang.time.DateFormatUtils;
import org.ocpsoft.prettytime.PrettyTime;

public class EmailInboxDTO {
    private String messageId;
    private String from;
    private String subject;
    private String body;
    private String to;
    private String relativeDate;
    private String sentAt;
    private PrettyTime p = new PrettyTime();
    private boolean hasAttachment;
    private int status;
    private String mark;
    private int replyTimes;

    public EmailInboxDTO(){

    }

    public EmailInboxDTO(Email email){
        this.messageId = email.getMessageId();
        this.from = email.getFrom();
        this.subject = email.getSubject();
        this.body = email.getOriginalBody();
        this.to = email.getTo();
        this.relativeDate = p.format(email.getSentAt());
        this.sentAt = DateFormatUtils.format(email.getSentAt(), "yyyy-MM-dd HH:mm:ss", DetailMailDTO.TIME_ZONE, null);
        this.hasAttachment = email.isHasAttachment();
        this.status = email.getStatus();
        this.mark = email.getMark();
        this.replyTimes = email.getReplyTimes();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getRelativeDate() {
        return relativeDate;
    }

    public void setRelativeDate(String relativeDate) {
        this.relativeDate = relativeDate;
    }

    public String getSentAt() {
        return sentAt;
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public int getStatus() { return status; }

    public void setStatus(int status) { this.status = status; }

    public String getMark() { return mark; }

    public void setMark(String mark) { this.mark = mark; }

    public String getBody() { return body; }

    public void setBody(String body) { this.body = body; }

    public int getReplyTimes() {
        return replyTimes;
    }

    public void setReplyTimes(int replyTimes) {
        this.replyTimes = replyTimes;
    }
}
