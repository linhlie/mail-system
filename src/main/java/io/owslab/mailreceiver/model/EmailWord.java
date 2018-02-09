package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Created by khanhlvb on 2/9/18.
 */
@Entity
@Table(name = "Emails_Words")
public class EmailWord {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String messageId;

    @NotNull
    private long wordId;

    private String appearIndexs;

    @NotNull
    private int status;

    public EmailWord(String messageId, long wordId, String appearIndexs, int status) {
        this.messageId = messageId;
        this.wordId = wordId;
        this.appearIndexs = appearIndexs;
        this.status = status;
    }

    public EmailWord() {
    }

    public EmailWord(long id) {
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

    public String getAppearIndexs() {
        return appearIndexs;
    }

    public void setAppearIndexs(String appearIndexs) {
        this.appearIndexs = appearIndexs;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
