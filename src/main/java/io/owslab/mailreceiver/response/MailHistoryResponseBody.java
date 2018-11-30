package io.owslab.mailreceiver.response;

import io.owslab.mailreceiver.dto.FileDTO;
import io.owslab.mailreceiver.model.SentMailHistory;

import java.util.List;

public class MailHistoryResponseBody extends AjaxResponseBody  {
    private SentMailHistory sentMailHistory;
    private List<FileDTO> listFile;

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

    public List<FileDTO> getListFile() {
        return listFile;
    }

    public void setListFile(List<FileDTO> listFile) {
        this.listFile = listFile;
    }
}
