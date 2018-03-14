package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 2/28/18.
 */
public class AccountForm {
    private String account;

    private String userName;

    private String password;

    private String mailServerAddress;

    private int mailServerPort;

    private int mailProtocol;

    private int encryptionProtocol;

    private int authenticationProtocol;

    private String proxyServer;

    private boolean disabled;

    private int type;

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

    public AccountForm() {
    }

    public AccountForm(EmailAccountSetting account) {
        this.account = account.getAccount();
        this.userName = account.getUserName();
        this.password = account.getPassword();
        this.mailServerAddress = account.getMailServerAddress();
        this.mailServerPort = account.getMailServerPort();
        this.mailProtocol = account.getMailProtocol();
        this.encryptionProtocol = account.getEncryptionProtocol();
        this.authenticationProtocol = account.getAuthenticationProtocol();
        this.proxyServer = account.getProxyServer();
        this.disabled = account.isDisabled();
        this.type = account.getType();
    }
}
