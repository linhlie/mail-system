package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.Email;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by khanhlvb on 3/13/18.
 */
public class DetailMailDTO {
    private String messageId;

    private long accountId;

    private String from;

    private String subject;

    private String to;

    private String cc;

    private String bcc;

    private String replyTo;

    private String sentAt;

    private String receivedAt;

    private boolean hasAttachment;

    private int contentType;

    private String originalBody;

    private List<FileDTO> files;

    public DetailMailDTO(Email email) {
        this.setMessageId(email.getMessageId());
        this.setAccountId(email.getAccountId());
        this.setFrom(email.getFrom());
        this.setSubject(email.getSubject());
        this.setTo(email.getTo());
        this.setCc(email.getCc());
        this.setBcc(email.getBcc());
        this.setReplyTo(email.getReplyTo());
        this.setSentAt(email.getSentAt().toString());
        this.setReceivedAt(email.getReceivedAt().toString());
        this.setHasAttachment(email.isHasAttachment());
        this.setContentType(email.getContentType());
        this.setOriginalBody(email.getOriginalBody());
        this.files = new ArrayList<>();
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

    public List<FileDTO> getFiles() {
        return files;
    }

    public void setFiles(List<FileDTO> files) {
        this.files = files;
    }

    public boolean addFile(AttachmentFile file) {
        return this.files.add(new FileDTO(file));
    }
}
