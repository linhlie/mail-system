package io.owslab.mailreceiver.model;

import javax.persistence.*;

/**
 * Created by khanhlvb on 3/1/18.
 */
@Entity
@Table(name = "Accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "User_Name", length = 50, nullable = false)
    private String userName;

    @Column(name = "Encrypted_Password", length = 255, nullable = false)
    private String encryptedPassword;

    @Column(name = "Active", length = 1, nullable = false)
    private boolean active;

    @Column(name = "User_Role", length = 20, nullable = false)
    private String userRole;

    @Column(name = "Last_Name", length = 50)
    private String lastName;

    @Column(name = "First_Name", length = 50)
    private String firstName;

    public Account(){

    }

    public Account(long id){
        this.id = id;
    }

    public Account(String userName, String encryptedPassword) {
        this.userName = userName;
        this.encryptedPassword = encryptedPassword;
        this.active = true;
        this.userRole = Role.MEMBER;
    }

    public Account(String userName, String encryptedPassword, boolean active, String userRole) {
        this.userName = userName;
        this.encryptedPassword = encryptedPassword;
        this.active = active;
        this.userRole = userRole;
    }

    public Account(String userName, String encryptedPassword, boolean active, String userRole, String lastName, String firstName) {
        this.userName = userName;
        this.encryptedPassword = encryptedPassword;
        this.active = active;
        this.userRole = userRole;
        this.lastName = lastName;
        this.firstName = firstName;
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

    public String getAccountName(){
        if(this.lastName != null && this.firstName != null){
            return this.lastName + "　" + this.firstName;
        }
        return this.userName;
    }

    public class Role {
        public static final String ADMIN = "ROLE_ADMIN";
        public static final String MEMBER = "ROLE_MEMBER";
        public static final String MEMBER_EXPANSION = "ROLE_MEM_EXPANSION";
    }
}
