package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.SendMailForm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "Sent_Mail_Histories")
public class SentMailHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String messageId;

    private String matchingMessageId;

    @NotNull
    private long accountId;

    @NotNull
    @Column(name="[from]")
    private String from;

    @NotNull
    private String subject;

    @Column(name="[to]")
    private String to;

    private String cc;

    private String bcc;

    private String replyTo;

    private Date sentAt;

    private Date originalReceivedAt;

    private Date matchingReceivedAt;

    private boolean hasAttachment;

    private String body;

    private String sendType;

    private String matchingMailAddress;

    public SentMailHistory() {
    }

    public SentMailHistory(long id){
        this.id = id;
    }

    public SentMailHistory(Email originalMail, Email matchingMail, EmailAccount emailAccount, String to, String cc, String bcc, String replyTo, SendMailForm form, boolean hasAttachment) {
        this.messageId = originalMail.getMessageId();
        this.accountId = emailAccount.getId();
        this.from = emailAccount.getAccount();
        this.subject = form.getSubject();
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.replyTo = replyTo;
        this.sentAt = new Date();
        this.originalReceivedAt = originalMail.getReceivedAt();
        this.hasAttachment = hasAttachment;
        this.body = form.getContent();
        this.sendType = form.getSendType();
        if(matchingMail != null) {
            this.matchingMessageId = matchingMail.getMessageId();
            this.matchingReceivedAt = matchingMail.getReceivedAt();
            this.matchingMailAddress = matchingMail.getFrom();
        }
    }

    public SentMailHistory(String messageId, String matchingMessageId, long accountId, String from, String subject, String to, String cc, String bcc, String replyTo, Date sentAt, Date originalReceivedAt, Date matchingReceivedAt, boolean hasAttachment, String body, String sendType, String matchingMailAddress) {
        this.messageId = messageId;
        this.matchingMessageId = matchingMessageId;
        this.accountId = accountId;
        this.from = from;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.replyTo = replyTo;
        this.sentAt = sentAt;
        this.originalReceivedAt = originalReceivedAt;
        this.matchingReceivedAt = matchingReceivedAt;
        this.hasAttachment = hasAttachment;
        this.body = body;
        this.sendType = sendType;
        this.matchingMailAddress = matchingMailAddress;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMatchingMessageId() {
        return matchingMessageId;
    }

    public void setMatchingMessageId(String matchingMessageId) {
        this.matchingMessageId = matchingMessageId;
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

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public Date getOriginalReceivedAt() {
        return originalReceivedAt;
    }

    public void setOriginalReceivedAt(Date originalReceivedAt) {
        this.originalReceivedAt = originalReceivedAt;
    }

    public Date getMatchingReceivedAt() {
        return matchingReceivedAt;
    }

    public void setMatchingReceivedAt(Date matchingReceivedAt) {
        this.matchingReceivedAt = matchingReceivedAt;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
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
}
