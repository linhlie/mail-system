package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.dto.BulletinBoardDTO;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "Bulletin_Board")
public class BulletinBoard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String bulletin;
    private long accountCreateId;
    private Date timeCreate;
    private long accountId;
    private Date timeEdit;
    private String tabName;
    private long tabNumber;

    public BulletinBoard(){

    }

    public BulletinBoard(BulletinBoardDTO bulletin, long accountId, List<BulletinBoard> bulletinBoards){
        if(bulletin.getId() != null && bulletin.getId()>0){
            this.id = bulletin.getId();
        }else{
            this.accountCreateId = accountId;
            this.timeCreate = new Date();
        }
        if(bulletin.getBulletin()!=null){
            this.bulletin = bulletin.getBulletin();
        }else{
            this.bulletin = "";
        }
        this.accountId = accountId;
        this.timeEdit = new Date();
        this.tabName =bulletin.getTabName();
        if(bulletin.getTabNumber() == 0){
            this.tabNumber = bulletinBoards.size() + 1;
        }else{
            this.tabNumber = bulletin.getTabNumber();
        }
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

    public String getTabName() { return tabName; }

    public void setTabName(String tabName) { this.tabName = tabName; }

    public long getTabNumber() { return tabNumber; }

    public void setTabNumber(long tabNumber) { this.tabNumber = tabNumber; }

    public long getAccountCreateId() {
        return accountCreateId;
    }

    public void setAccountCreateId(long accountCreateId) {
        this.accountCreateId = accountCreateId;
    }

    public Date getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(Date timeCreate) {
        this.timeCreate = timeCreate;
    }
}