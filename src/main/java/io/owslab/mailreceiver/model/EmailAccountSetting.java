package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.AccountForm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import java.util.Date;

@Entity
@Table(name = "Email_Account_Settings")
public class EmailAccountSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotNull
    private long accountId;

    private String userName;

    @NotNull
    private String password;

    @NotNull
    private String mailServerAddress;

    @NotNull
    private int mailServerPort;

    @NotNull
    private int mailProtocol;

    @NotNull
    private int encryptionProtocol;

    @NotNull
    private int authenticationProtocol;

    private String cc;

    private String proxyServer;

    private Date createdAt;

    private Date updatedAt;

    private int type;

    public EmailAccountSetting() {
    }

    public EmailAccountSetting(long id) {
        this.id = id;
    }

    public EmailAccountSetting(AccountForm form, boolean isUpdate){
        this.accountId = form.getAccountId();
        this.userName = form.getUserName();
        this.password = form.getPassword();
        this.mailServerAddress = form.getMailServerAddress();
        this.mailServerPort = form.getMailServerPort();
        this.mailProtocol = form.getMailProtocol();
        this.encryptionProtocol = form.getEncryptionProtocol();
        this.authenticationProtocol = form.getAuthenticationProtocol();
        this.cc = form.getCc();
        this.proxyServer = form.getProxyServer();
        this.type = form.getType();
        if(isUpdate){
            this.updatedAt = new Date();
        } else {
            this.createdAt = new Date();
        }
    }

    public EmailAccountSetting(long accountId, String userName, String password, String mailServerAddress, int mailServerPort, int mailProtocol, int encryptionProtocol, int authenticationProtocol, String cc, String proxyServer, Date createdAt, Date updatedAt, int type) {
        this.accountId = accountId;
        this.userName = userName;
        this.password = password;
        this.mailServerAddress = mailServerAddress;
        this.mailServerPort = mailServerPort;
        this.mailProtocol = mailProtocol;
        this.encryptionProtocol = encryptionProtocol;
        this.authenticationProtocol = authenticationProtocol;
        this.cc = cc;
        this.proxyServer = proxyServer;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.type = type;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMailServerAddress() {
        return mailServerAddress;
    }

    public void setMailServerAddress(String mailServerAddress) {
        this.mailServerAddress = mailServerAddress;
    }

    public int getMailServerPort() {
        return mailServerPort;
    }

    public void setMailServerPort(int mailServerPort) {
        this.mailServerPort = mailServerPort;
    }

    public int getMailProtocol() {
        return mailProtocol;
    }

    public void setMailProtocol(int mailProtocol) {
        this.mailProtocol = mailProtocol;
    }

    public int getEncryptionProtocol() {
        return encryptionProtocol;
    }

    public void setEncryptionProtocol(int encryptionProtocol) {
        this.encryptionProtocol = encryptionProtocol;
    }

    public int getAuthenticationProtocol() {
        return authenticationProtocol;
    }

    public void setAuthenticationProtocol(int authenticationProtocol) {
        this.authenticationProtocol = authenticationProtocol;
    }

    public String getProxyServer() {
        return proxyServer;
    }

    public void setProxyServer(String proxyServer) {
        this.proxyServer = proxyServer;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public class Protocol {
        public static final int IMAP = 0;
        public static final int POP3 = 1;
        public static final int SMTP = 2;
    }

    public class Type {
        public static final int RECEIVE = 0;
        public static final int SEND = 1;
    }
}
