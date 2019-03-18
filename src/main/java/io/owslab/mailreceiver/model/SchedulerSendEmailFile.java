package io.owslab.mailreceiver.model;

import javax.persistence.*;

@Entity
@Table(name = "scheduler_send_email_file")
public class SchedulerSendEmailFile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long schedulerSendEmailId;
    private long uploadFilesId;

    public SchedulerSendEmailFile(){

    }

    public SchedulerSendEmailFile(long schedulerSendEmailId, long uploadFilesId){
        this.schedulerSendEmailId = schedulerSendEmailId;
        this.uploadFilesId = uploadFilesId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSchedulerSendEmailId() {
        return schedulerSendEmailId;
    }

    public void setSchedulerSendEmailId(long schedulerSendEmailId) {
        this.schedulerSendEmailId = schedulerSendEmailId;
    }

    public long getUploadFilesId() {
        return uploadFilesId;
    }

    public void setUploadFilesId(long uploadFilesId) {
        this.uploadFilesId = uploadFilesId;
    }
}
