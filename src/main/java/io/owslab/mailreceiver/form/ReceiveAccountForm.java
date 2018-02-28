package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 1/24/18.
 */
public class ReceiveAccountForm {
    private String account;

    private String password;

    private String mailServerAddress;

    private int mailServerPort;

    private int receiveMailProtocol;

    private int encryptionProtocol;

    private int authenticationProtocol;

    private String proxyServer;

    private boolean disabled;

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

    public ReceiveAccountForm() {
    }

    public ReceiveAccountForm(EmailAccountSetting account) {
        this.account = account.getAccount();
        this.password = account.getPassword();
        this.mailServerAddress = account.getMailServerAddress();
        this.mailServerPort = account.getMailServerPort();
        this.receiveMailProtocol = account.getReceiveMailProtocol();
        this.encryptionProtocol = account.getEncryptionProtocol();
        this.authenticationProtocol = account.getAuthenticationProtocol();
        this.proxyServer = account.getProxyServer();
        this.disabled = account.isDisabled();
    }
}
