package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Account;

import javax.persistence.Column;

/**
 * Created by khanhlvb on 7/4/18.
 */
public class AccountDTO {
    private long id;

    private String userName;

    private String encryptedPassword;

    private boolean active;

    private String userRole;

    private String name;

    public AccountDTO(Account account) {
        this.setId(account.getId());
        this.setUserName(account.getUserName());
        this.setEncryptedPassword(account.getEncryptedPassword());
        this.setActive(account.isActive());
        this.setUserRole(account.getUserRole());
        this.setName(account.getName());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }

    public void setEncryptedPassword(String encryptedPassword) {
        this.encryptedPassword = encryptedPassword;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
