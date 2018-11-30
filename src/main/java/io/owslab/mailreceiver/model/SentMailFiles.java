package io.owslab.mailreceiver.model;


import javax.persistence.*;

@Entity
@Table(name = "sent_mail_files")
public class SentMailFiles {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private long sentMailHistoriesId;

    private long uploadFilesId;

    public SentMailFiles(){

    }

    public SentMailFiles(long sentMailHistoriesId, long uploadFilesId){
        this.sentMailHistoriesId = sentMailHistoriesId;
        this.uploadFilesId = uploadFilesId;
    }

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

    public long getUploadFilesId() {
        return uploadFilesId;
    }

    public void setUploadFilesId(long uploadFilesId) {
        this.uploadFilesId = uploadFilesId;
    }
}
