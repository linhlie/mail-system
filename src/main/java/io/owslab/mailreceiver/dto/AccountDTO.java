package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.Account;

import javax.persistence.Column;

/**
 * Created by khanhlvb on 7/4/18.
 */
public class AccountDTO {
    private long id;

    private String userName;

    private boolean active;

    private String userRole;

    private String lastName;

    private String firstName;

    private boolean expansion;

    public AccountDTO(Account account) {
        this.setId(account.getId());
        this.setUserName(account.getUserName());
        this.setActive(account.isActive());
        this.setUserRole(account.getUserRole());
        this.setLastName(account.getLastName());
        this.setFirstName(account.getFirstName());
        this.setExpansion(Account.Role.MEMBER_EXPANSION.equals(account.getUserRole()));
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public boolean isExpansion() {
        return expansion;
    }

    public void setExpansion(boolean expansion) {
        this.expansion = expansion;
    }
}
