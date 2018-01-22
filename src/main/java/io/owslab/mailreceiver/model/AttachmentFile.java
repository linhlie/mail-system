package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "Files")
public class AttachmentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String messageId;

    @NotNull
    private String fileName;

    @NotNull
    private String storagePath;

    private Date createdAt;

    private String metaData;

    public AttachmentFile() {
    }

    public AttachmentFile(long id) {
        this.id = id;
    }

    public AttachmentFile(String messageId, String fileName, String storagePath, Date createdAt, String metaData) {
        this.messageId = messageId;
        this.fileName = fileName;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
        this.metaData = metaData;
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

    @Override
    public String toString(){
        return this.getId() + " " + this.getFileName() + " " + this.getStoragePath();
    }
}
