package io.owslab.mailreceiver.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @Column(name = "User_Name", length = 50, nullable = false)
    private String userName;

    @Column(name = "Encrypted_Password", length = 255, nullable = false)
    private String encryptedPassword;

    @Column(name = "Active", length = 1, nullable = false)
    private boolean active;

    @Column(name = "User_Role", length = 20, nullable = false)
    private String userRole;

    public Account(){

    }

    public Account(String userName, String encryptedPassword, boolean active, String userRole) {
        this.userName = userName;
        this.encryptedPassword = encryptedPassword;
        this.active = active;
        this.userRole = userRole;
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

    public class Role {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String MEMBER = "ROLE_MEMBER";
    }
}
