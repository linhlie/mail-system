package io.owslab.mailreceiver.form;

/**
 * Created by khanhlvb on 4/4/18.
 */
public class SendMailForm {

    private String messageId;
    private String subject;
    private String receiver;
    private String content;

    public SendMailForm() {
    }

    public SendMailForm(String messageId, String subject, String receiver, String content) {
        this.messageId = messageId;
        this.subject = subject;
        this.receiver = receiver;
        this.content = content;
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
}
