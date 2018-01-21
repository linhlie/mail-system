package io.owslab.mailreceiver.entity;

import io.owslab.mailreceiver.model.ReceiveEmailAccountSetting;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "Emails")
public class Email {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String messageId;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "account_id", nullable = false, //
//            foreignKey = @ForeignKey(name = "fk_receive_email_account"))
//    private ReceiveEmailAccountSetting receiveEmailAccountSetting;

    @NotNull
    private String accounId;

    @NotNull
    private String from;

    @NotNull
    private String subject;

    @NotNull
    private String to;

    private String cc;

    private String bcc;

    private String replyTo;

    @NotNull
    private Date sentAt;

    private Date receivedAt;

    private boolean hasAttachment;

    @NotNull
    private int contentType;

    private String originalBody;

    private String optimizedBody;

    private String header;

    private Date createdAt;

    private String metaData;

    public Email() {
    }

    public Email(String messageId){
        this.messageId = messageId;
    }

    public Email(String accounId, String from, String subject, String to, String cc, String bcc, String replyTo, Date sentAt, Date receivedAt, boolean hasAttachment, int contentType, String originalBody, String optimizedBody, String header, Date createdAt, String metaData) {
        this.accounId = accounId;
        this.from = from;
        this.subject = subject;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.replyTo = replyTo;
        this.sentAt = sentAt;
        this.receivedAt = receivedAt;
        this.hasAttachment = hasAttachment;
        this.contentType = contentType;
        this.originalBody = originalBody;
        this.optimizedBody = optimizedBody;
        this.header = header;
        this.createdAt = createdAt;
        this.metaData = metaData;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getAccounId() {
        return accounId;
    }

    public void setAccounId(String accounId) {
        this.accounId = accounId;
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

    public Date getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(Date receivedAt) {
        this.receivedAt = receivedAt;
    }

    public boolean isHasAttachment() {
        return hasAttachment;
    }

    public void setHasAttachment(boolean hasAttachment) {
        this.hasAttachment = hasAttachment;
    }

    public int getContentType() {
        return contentType;
    }

    public void setContentType(int contentType) {
        this.contentType = contentType;
    }

    public String getOriginalBody() {
        return originalBody;
    }

    public void setOriginalBody(String originalBody) {
        this.originalBody = originalBody;
    }

    public String getOptimizedBody() {
        return optimizedBody;
    }

    public void setOptimizedBody(String optimizedBody) {
        this.optimizedBody = optimizedBody;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getMetaData() {
        return metaData;
    }

    public void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    @Override
    public String toString(){
        return this.getMessageId() + " " + this.getFrom() + " " + this.getTo() + " " + this.getAccounId();
    }
}
