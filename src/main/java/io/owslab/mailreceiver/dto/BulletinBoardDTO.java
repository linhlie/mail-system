package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Account;
import io.owslab.mailreceiver.model.BulletinBoard;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BulletinBoardDTO {
    private Long id;
    private String bulletin;
    private String timeEdit;
    private String username;
    private String tabName;
    private long tabNumber;
    private long accountId;
    private String usernameCreate;
    private long accountCreateId;
    private String timeCreate;

    public BulletinBoardDTO(){

    }

    public BulletinBoardDTO(BulletinBoard bulletin, Account accountEdit, Account accountCreate){
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        this.id = bulletin.getId();
        this.bulletin = bulletin.getBulletin();
        String dateEdit = df.format(bulletin.getTimeEdit());
        this.timeEdit = dateEdit;
        this.username = accountEdit.getAccountName();
        this.tabName = bulletin.getTabName();
        this.tabNumber = bulletin.getTabNumber();
        this.accountId = bulletin.getAccountId();
        this.accountCreateId = bulletin.getAccountCreateId();
        String dateCreate = df.format(bulletin.getTimeCreate());
        this.timeCreate = dateCreate;
        this.usernameCreate = accountCreate.getAccountName();
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getBulletin() {
        return bulletin;
    }

    public void setBulletin(String bulletin) {
        this.bulletin = bulletin;
    }

    public String getTimeEdit() {
        return timeEdit;
    }

    public void setTimeEdit(String timeEdit) {
        this.timeEdit = timeEdit;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTabName() { return tabName; }

    public void setTabName(String tabName) { this.tabName = tabName; }

    public long getTabNumber() { return tabNumber; }

    public void setTabNumber(long tabNumber) { this.tabNumber = tabNumber; }

    public long getAccountId() { return accountId; }

    public void setAccountId(long accountId) { this.accountId = accountId; }

    public String getUsernameCreate() {
        return usernameCreate;
    }

    public void setUsernameCreate(String usernameCreate) {
        this.usernameCreate = usernameCreate;
    }

    public long getAccountCreateId() {
        return accountCreateId;
    }

    public void setAccountCreateId(long accountCreateId) {
        this.accountCreateId = accountCreateId;
    }

    public String getTimeCreate() {
        return timeCreate;
    }

    public void setTimeCreate(String timeCreate) {
        this.timeCreate = timeCreate;
    }
}
