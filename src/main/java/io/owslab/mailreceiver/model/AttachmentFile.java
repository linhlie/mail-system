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

    private boolean deleted;

    private Date deletedAt;

    private long size;

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

    public AttachmentFile(String messageId, String fileName, String storagePath, Date createdAt, String metaData,
                          boolean deleted, Date deletedAt) {
        this.messageId = messageId;
        this.fileName = fileName;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
        this.metaData = metaData;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public AttachmentFile(String messageId, String fileName, String storagePath, Date createdAt, String metaData, long size) {
        this.messageId = messageId;
        this.fileName = fileName;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
        this.metaData = metaData;
        this.size = size;
    }

    public AttachmentFile(String messageId, String fileName, String storagePath, Date createdAt, String metaData, boolean deleted, Date deletedAt, long size) {
        this.messageId = messageId;
        this.fileName = fileName;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
        this.metaData = metaData;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
        this.size = size;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    @Override
    public String toString(){
        return this.getId() + " " + this.getFileName() + " " + this.getStoragePath();
    }
}
