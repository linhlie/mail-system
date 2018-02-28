package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.ReceiveAccountForm;

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
    private String account;

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

    private String proxyServer;

    private boolean disabled;

    private Date createdAt;

    private Date updatedAt;

    private int type;

    public EmailAccountSetting() {
    }

    public EmailAccountSetting(long id) {
        this.id = id;
    }

    public EmailAccountSetting(ReceiveAccountForm form, boolean isUpdate){
        this.account = form.getAccount();
        this.password = form.getPassword();
        this.mailServerAddress = form.getMailServerAddress();
        this.mailServerPort = form.getMailServerPort();
        this.mailProtocol = form.getMailProtocol();
        this.encryptionProtocol = form.getEncryptionProtocol();
        this.authenticationProtocol = form.getAuthenticationProtocol();
        this.proxyServer = form.getProxyServer();
        this.disabled = form.isDisabled();
        this.type = form.getType();
        if(isUpdate){
            this.updatedAt = new Date();
        } else {
            this.createdAt = new Date();
        }
    }

    public EmailAccountSetting(String account, String password, String mailServerAddress, int mailServerPort, int mailProtocol, int encryptionProtocol, int authenticationProtocol, String proxyServer, boolean disabled, Date createdAt, Date updatedAt, int type) {
        this.account = account;
        this.password = password;
        this.mailServerAddress = mailServerAddress;
        this.mailServerPort = mailServerPort;
        this.mailProtocol = mailProtocol;
        this.encryptionProtocol = encryptionProtocol;
        this.authenticationProtocol = authenticationProtocol;
        this.proxyServer = proxyServer;
        this.disabled = disabled;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
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

    @Override
    public String toString(){
        return this.getAccount() + " " + this.getPassword();
    }

    public class Protocol {
        public static final int IMAP = 0;
        public static final int POP3 = 1;
        public static final int SMTP = 2;
    }
}
