package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.ReplaceNumber;

import java.util.List;

/**
 * Created by khanhlvb on 4/4/18.
 */
public class SendMailForm {

    private String messageId;
    private String subject;
    private String receiver;
    private boolean activeCC;
    private String cc;
    private String content;
    private List<Long> originAttachment;
    private List<Long> uploadAttachment;

    public SendMailForm() {
    }

    public SendMailForm(String messageId, String subject, String receiver, boolean activeCC, String cc, String content, List<Long> originAttachment, List<Long> uploadAttachment) {
        this.messageId = messageId;
        this.subject = subject;
        this.receiver = receiver;
        this.activeCC = activeCC;
        this.cc = cc;
        this.content = content;
        this.originAttachment = originAttachment;
        this.uploadAttachment = uploadAttachment;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public List<Long> getOriginAttachment() {
        return originAttachment;
    }

    public void setOriginAttachment(List<Long> originAttachment) {
        this.originAttachment = originAttachment;
    }

    public List<Long> getUploadAttachment() {
        return uploadAttachment;
    }

    public void setUploadAttachment(List<Long> uploadAttachment) {
        this.uploadAttachment = uploadAttachment;
    }

    public boolean isActiveCC() {
        return activeCC;
    }

    public void setActiveCC(boolean activeCC) {
        this.activeCC = activeCC;
    }
}
