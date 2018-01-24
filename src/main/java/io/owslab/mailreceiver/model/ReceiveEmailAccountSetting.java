package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.ReceiveAccountForm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import java.util.Date;

@Entity
@Table(name = "Receive_Email_Account_Settings")
public class ReceiveEmailAccountSetting {

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
    private int receiveMailProtocol;

    @NotNull
    private int encryptionProtocol;

    @NotNull
    private int authenticationProtocol;

    private String proxyServer;

    private boolean disabled;

    private Date createdAt;

    private Date updatedAt;

    public ReceiveEmailAccountSetting() {
    }

    public ReceiveEmailAccountSetting(long id) {
        this.id = id;
    }

    public ReceiveEmailAccountSetting(ReceiveAccountForm form, boolean isUpdate){
        this.account = form.getAccount();
        this.password = form.getPassword();
        this.mailServerAddress = form.getMailServerAddress();
        this.mailServerPort = form.getMailServerPort();
        this.receiveMailProtocol = form.getReceiveMailProtocol();
        this.encryptionProtocol = form.getEncryptionProtocol();
        this.authenticationProtocol = form.getAuthenticationProtocol();
        this.proxyServer = form.getProxyServer();
        this.disabled = form.isDisabled();
        if(isUpdate){
            this.updatedAt = new Date();
        } else {
            this.createdAt = new Date();
        }
    }

    public ReceiveEmailAccountSetting(String account, String password, String mailServerAddress, int mailServerPort, int receiveMailProtocol, int encryptionProtocol, int authenticationProtocol, String proxyServer, boolean disabled, Date createdAt, Date updatedAt) {
        this.account = account;
        this.password = password;
        this.mailServerAddress = mailServerAddress;
        this.mailServerPort = mailServerPort;
        this.receiveMailProtocol = receiveMailProtocol;
        this.encryptionProtocol = encryptionProtocol;
        this.authenticationProtocol = authenticationProtocol;
        this.proxyServer = proxyServer;
        this.disabled = disabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public int getReceiveMailProtocol() {
        return receiveMailProtocol;
    }

    public void setReceiveMailProtocol(int receiveMailProtocol) {
        this.receiveMailProtocol = receiveMailProtocol;
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

    @Override
    public String toString(){
        return this.getAccount() + " " + this.getPassword();
    }
}
