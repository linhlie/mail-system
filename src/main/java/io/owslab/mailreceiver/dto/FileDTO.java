package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.AttachmentFile;

/**
 * Created by khanhlvb on 3/13/18.
 */
public class FileDTO {
    private long id;
    private String messageId;
    private String fileName;
    private String storagePath;
    private long size;

    public FileDTO(AttachmentFile file) {
        this.setId(file.getId());
        this.setMessageId(file.getMessageId());
        this.setFileName(file.getFileName());
        this.setStoragePath(file.getStoragePath());
        this.setSize(file.getSize());
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
