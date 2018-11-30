package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.AttachmentFile;
import io.owslab.mailreceiver.model.UploadFile;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.util.Base64;

/**
 * Created by khanhlvb on 3/13/18.
 */
public class FileDTO {
    public static final String CHECK_SUM = "a";

    private long id;
    private String messageId;
    private String fileName;
    private String storagePath;
    private String digest;
    private long size;

    public FileDTO(){

    }

    public FileDTO(AttachmentFile file) {
        this.setId(file.getId());
        this.setMessageId(file.getMessageId());
        this.setFileName(file.getFileName());
        this.setStoragePath(file.getStoragePath());
        this.setSize(file.getSize());
        String downloadDigest = file.getId() + File.separator + CHECK_SUM;
        String encodedDownloadDigest = DatatypeConverter.printHexBinary(downloadDigest.getBytes());
        this.setDigest(encodedDownloadDigest);
    }

    public FileDTO(UploadFile file) {
        this.setId(file.getId());
        this.setFileName(file.getFileName());
        this.setStoragePath(file.getStoragePath());
        this.setSize(file.getSize());
        String downloadDigest = file.getId() + File.separator + CHECK_SUM;
        String encodedDownloadDigest = DatatypeConverter.printHexBinary(downloadDigest.getBytes());
        this.setDigest(encodedDownloadDigest);
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

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }
}
