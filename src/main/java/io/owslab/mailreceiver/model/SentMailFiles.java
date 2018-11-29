package io.owslab.mailreceiver.model;


import javax.persistence.*;

@Entity
@Table(name = "sent_mail_files")
public class SentMailFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long sentMailHistoriesId;

    private long uploadFileId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSentMailHistoriesId() {
        return sentMailHistoriesId;
    }

    public void setSentMailHistoriesId(long sentMailHistoriesId) {
        this.sentMailHistoriesId = sentMailHistoriesId;
    }

    public long getUploadFileId() {
        return uploadFileId;
    }

    public void setUploadFileId(long uploadFileId) {
        this.uploadFileId = uploadFileId;
    }
}
