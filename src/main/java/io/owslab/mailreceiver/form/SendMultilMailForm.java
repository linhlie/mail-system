package io.owslab.mailreceiver.form;

import java.util.List;

public class SendMultilMailForm {
    private List<String> listId;
    private String receiver;
    private boolean activeCC;
    private String content;
    private List<Long> originAttachment;
    private List<Long> uploadAttachment;
    private long accountId;

    private int sendType;
    private int historyType;

    public List<String> getListId() {
        return listId;
    }

    public void setListId(List<String> listId) {
        this.listId = listId;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public boolean isActiveCC() {
        return activeCC;
    }

    public void setActiveCC(boolean activeCC) {
        this.activeCC = activeCC;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public int getSendType() {
        return sendType;
    }

    public void setSendType(int sendType) {
        this.sendType = sendType;
    }

    public int getHistoryType() {
        return historyType;
    }

    public void setHistoryType(int historyType) {
        this.historyType = historyType;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }
}
