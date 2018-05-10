package io.owslab.mailreceiver.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Table(name = "Upload_Files")
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private String fileName;

    @NotNull
    private String storagePath;

    private Date createdAt;

    private long size;

    public UploadFile() {
    }

    public UploadFile(long id) {
        this.id = id;
    }

    public UploadFile(String fileName, String storagePath, Date createdAt, long size) {
        this.fileName = fileName;
        this.storagePath = storagePath;
        this.createdAt = createdAt;
        this.size = size;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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
