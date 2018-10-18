package io.owslab.mailreceiver.model;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "Bulletin_Board")
public class BulletinBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String bulletin;
    private long accountId;
    private Date timeEdit;

    public BulletinBoard(){

    }

    public BulletinBoard(String bulletin, long accountId){
        this.bulletin = bulletin;
        this.accountId = accountId;
        this.timeEdit = new Date();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public Date getTimeEdit() {
        return timeEdit;
    }

    public void setTimeEdit(Date timeEdit) {
        this.timeEdit = timeEdit;
    }
}