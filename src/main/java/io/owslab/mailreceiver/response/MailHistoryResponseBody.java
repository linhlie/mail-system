package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.model.SentMailHistory;

public class MailHistoryResponseBody extends AjaxResponseBody  {
    private SentMailHistory sentMailHistory;

    public MailHistoryResponseBody(String msg, boolean status) {
        super(msg, status);
    }

    public MailHistoryResponseBody(String msg) {
        this(msg, false);
    }

    public MailHistoryResponseBody() {
        this("");
    }

    public SentMailHistory getSentMailHistory() {
        return sentMailHistory;
    }

    public void setSentMailHistory(SentMailHistory sentMailHistory) {
        this.sentMailHistory = sentMailHistory;
    }
}
