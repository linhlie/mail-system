package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Entity
@Table(name = "Email_Word_Jobs")
public class EmailWordJob {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String messageId;

    @NotNull
    private long wordId;

    public EmailWordJob(String messageId, long wordId) {
        this.messageId = messageId;
        this.wordId = wordId;
    }

    public EmailWordJob() {
    }

    public EmailWordJob(long id) {
        this.id = id;
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

    public long getWordId() {
        return wordId;
    }

    public void setWordId(long wordId) {
        this.wordId = wordId;
    }
}
