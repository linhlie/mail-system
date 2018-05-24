package io.owslab.mailreceiver.model;

import com.mariten.kanatools.KanaConverter;
import io.owslab.mailreceiver.service.mail.MailBoxService;
import io.owslab.mailreceiver.utils.FullNumberRange;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Emails")
public class Email {
    public static final String HAS_ATTACHMENT = "1";
    public static final String NO_ATTACHMENT = "0";

    @Id
    private String messageId;

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

    private Date receivedAt;

    private boolean hasAttachment;

    @NotNull
    private int contentType;

    private String originalBody;

    private String optimizedBody;

    private String header;

    private Date createdAt;

    private String metaData;

    private boolean deleted;

    private Date deletedAt;

    private String errorLog;

    @Transient
    private String optimizedText;

    @Transient
    private String optimizedTextDistinguish;

    @Transient
    private String cachedOptimizedBodyAndSubject = null;

    @Transient
    private List<FullNumberRange> rangeList = null;

    public Email() {
        this.rangeList = new ArrayList<>();
    }

    public Email(String messageId){
        this.messageId = messageId;
        this.rangeList = new ArrayList<>();
    }

    public Email(long accountId, String from, String subject, String to, String cc, String bcc,
                 String replyTo, Date sentAt, Date receivedAt, boolean hasAttachment,
                 int contentType, String originalBody, String optimizedBody, String header,
                 Date createdAt, String metaData) {
        this.accountId = accountId;
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
        this.rangeList = new ArrayList<>();
    }

    public Email(String messageId, long accountId, String from, String subject, String to,
                 String cc, String bcc, String replyTo, Date sentAt, Date receivedAt,
                 boolean hasAttachment, int contentType, String originalBody,
                 String optimizedBody, String header, Date createdAt, String metaData,
                 boolean deleted, Date deletedAt, String errorLog) {
        this.messageId = messageId;
        this.accountId = accountId;
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
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.errorLog = errorLog;
        this.rangeList = new ArrayList<>();
    }

    public Email(String messageId, long accountId, String from, String subject, String to,
                 String cc, String bcc, String replyTo, Date sentAt, Date receivedAt,
                 boolean hasAttachment, int contentType, String originalBody,
                 String optimizedBody, String header, Date createdAt, String metaData,
                 boolean deleted, Date deletedAt) {
        this.messageId = messageId;
        this.accountId = accountId;
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
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.rangeList = new ArrayList<>();
    }



    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
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

    public String getSubjectAndOptimizedBody(){
        if(cachedOptimizedBodyAndSubject == null)
            cachedOptimizedBodyAndSubject = MailBoxService.optimizeText(this.getSubject()) + "\n" + this.getOptimizedBody();
        return cachedOptimizedBodyAndSubject;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    @Override
    public String toString(){
        return this.getMessageId() + " " + this.getFrom() + " " + this.getTo() + " " + this.getAccountId();
    }

    public String getOptimizedText(boolean distinguish) {
        String raw = this.getSubjectAndOptimizedBody();
        if(this.optimizedTextDistinguish == null) {
            int conv_op_flags = 0;
            conv_op_flags |= KanaConverter.OP_HAN_KATA_TO_ZEN_KATA;
            conv_op_flags |= KanaConverter.OP_ZEN_ASCII_TO_HAN_ASCII;
            String japaneseOptimizedText = KanaConverter.convertKana(raw, conv_op_flags);
            this.setOptimizedTextDistinguish(japaneseOptimizedText.toLowerCase());
        }
        if(this.optimizedText == null){
            this.setOptimizedText(raw.toLowerCase());
        }
        return distinguish ? optimizedTextDistinguish : optimizedText;
    }

    public void setOptimizedText(String optimizedText) {
        this.optimizedText = optimizedText;
    }

    public void setOptimizedTextDistinguish(String optimizedTextDistinguish) {
        this.optimizedTextDistinguish = optimizedTextDistinguish;
    }

    public List<FullNumberRange> getRangeList() {
        return rangeList;
    }

    public void setRangeList(List<FullNumberRange> rangeList) {
        this.rangeList = rangeList;
    }

    public String getErrorLog() {
        return errorLog;
    }

    public void setErrorLog(String errorLog) {
        this.errorLog = errorLog;
    }
}
