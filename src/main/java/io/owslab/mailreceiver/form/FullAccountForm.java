package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 4/9/18.
 */
public class FullAccountForm {

    private String account;

    private boolean disabled;

    private boolean alertSend;

    private long accountId;

    private String rUserName;

    private String rPassword;

    private String rMailServerAddress;

    private int rMailServerPort;

    private int rMailProtocol;

    private int rEncryptionProtocol;

    private int rAuthenticationProtocol;

    private String rProxyServer;

    private String sUserName;

    private String sPassword;

    private String sMailServerAddress;

    private int sMailServerPort;

    private int sMailProtocol;

    private int sEncryptionProtocol;

    private int sAuthenticationProtocol;

    private String sCC;

    private String sProxyServer;

    private String signature;

    private String inChargeCompany;

    public FullAccountForm() {
    }

    public FullAccountForm(MailAccountForm mailAccountForm, ReceiveAccountForm receiveAccountForm, SendAccountForm sendAccountForm) {
        this.account = mailAccountForm.getAccount();
        this.disabled = mailAccountForm.isDisabled();
        this.alertSend = mailAccountForm.isAlertSend();
        this.signature = mailAccountForm.getSignature();
        this.inChargeCompany = mailAccountForm.getInChargeCompany();
        this.rUserName = receiveAccountForm.getUserName();
        this.rPassword = receiveAccountForm.getPassword();
        this.rMailServerAddress = receiveAccountForm.getMailServerAddress();
        this.rMailServerPort = receiveAccountForm.getMailServerPort();
        this.rMailProtocol = receiveAccountForm.getMailProtocol();
        this.rEncryptionProtocol = receiveAccountForm.getEncryptionProtocol();
        this.rAuthenticationProtocol = receiveAccountForm.getAuthenticationProtocol();
        this.rProxyServer = receiveAccountForm.getProxyServer();
        this.sUserName = sendAccountForm.getUserName();
        this.sPassword = sendAccountForm.getPassword();
        this.sMailServerAddress = sendAccountForm.getMailServerAddress();
        this.sMailServerPort = sendAccountForm.getMailServerPort();
        this.sMailProtocol = sendAccountForm.getMailProtocol();
        this.sEncryptionProtocol = sendAccountForm.getEncryptionProtocol();
        this.sAuthenticationProtocol = sendAccountForm.getAuthenticationProtocol();
        this.sCC = sendAccountForm.getCc();
        this.sProxyServer = sendAccountForm.getProxyServer();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public String getrUserName() {
        return rUserName;
    }

    public void setrUserName(String rUserName) {
        this.rUserName = rUserName;
    }

    public String getrPassword() {
        return rPassword;
    }

    public void setrPassword(String rPassword) {
        this.rPassword = rPassword;
    }

    public String getrMailServerAddress() {
        return rMailServerAddress;
    }

    public void setrMailServerAddress(String rMailServerAddress) {
        this.rMailServerAddress = rMailServerAddress;
    }

    public int getrMailServerPort() {
        return rMailServerPort;
    }

    public void setrMailServerPort(int rMailServerPort) {
        this.rMailServerPort = rMailServerPort;
    }

    public int getrMailProtocol() {
        return rMailProtocol;
    }

    public void setrMailProtocol(int rMailProtocol) {
        this.rMailProtocol = rMailProtocol;
    }

    public int getrEncryptionProtocol() {
        return rEncryptionProtocol;
    }

    public void setrEncryptionProtocol(int rEncryptionProtocol) {
        this.rEncryptionProtocol = rEncryptionProtocol;
    }

    public int getrAuthenticationProtocol() {
        return rAuthenticationProtocol;
    }

    public void setrAuthenticationProtocol(int rAuthenticationProtocol) {
        this.rAuthenticationProtocol = rAuthenticationProtocol;
    }

    public String getrProxyServer() {
        return rProxyServer;
    }

    public void setrProxyServer(String rProxyServer) {
        this.rProxyServer = rProxyServer;
    }

    public String getsUserName() {
        return sUserName;
    }

    public void setsUserName(String sUserName) {
        this.sUserName = sUserName;
    }

    public String getsPassword() {
        return sPassword;
    }

    public void setsPassword(String sPassword) {
        this.sPassword = sPassword;
    }

    public String getsMailServerAddress() {
        return sMailServerAddress;
    }

    public void setsMailServerAddress(String sMailServerAddress) {
        this.sMailServerAddress = sMailServerAddress;
    }

    public int getsMailServerPort() {
        return sMailServerPort;
    }

    public void setsMailServerPort(int sMailServerPort) {
        this.sMailServerPort = sMailServerPort;
    }

    public int getsMailProtocol() {
        return sMailProtocol;
    }

    public void setsMailProtocol(int sMailProtocol) {
        this.sMailProtocol = sMailProtocol;
    }

    public int getsEncryptionProtocol() {
        return sEncryptionProtocol;
    }

    public void setsEncryptionProtocol(int sEncryptionProtocol) {
        this.sEncryptionProtocol = sEncryptionProtocol;
    }

    public int getsAuthenticationProtocol() {
        return sAuthenticationProtocol;
    }

    public void setsAuthenticationProtocol(int sAuthenticationProtocol) {
        this.sAuthenticationProtocol = sAuthenticationProtocol;
    }

    public String getsProxyServer() {
        return sProxyServer;
    }

    public void setsProxyServer(String sProxyServer) {
        this.sProxyServer = sProxyServer;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getsCC() {
        return sCC;
    }

    public boolean isAlertSend() {
        return alertSend;
    }

    public void setAlertSend(boolean alertSend) {
        this.alertSend = alertSend;
    }

    public void setsCC(String sCC) {
        this.sCC = sCC;
    }

    public String getInChargeCompany() {
        return inChargeCompany;
    }

    public void setInChargeCompany(String inChargeCompany) {
        this.inChargeCompany = inChargeCompany;
    }

    public MailAccountForm getMailAccountForm(){
        return new MailAccountForm(this.getAccount(), this.isDisabled(), this.getSignature(), this.isAlertSend(), this.getInChargeCompany());
    }

    public ReceiveAccountForm getReceiveAccountForm(){
        ReceiveAccountForm form = new ReceiveAccountForm();
        form.setUserName(this.getrUserName());
        form.setPassword(this.getrPassword());
        form.setMailServerPort(this.getrMailServerPort());
        form.setMailProtocol(this.getrMailProtocol());
        form.setMailServerAddress(this.getrMailServerAddress());
        form.setEncryptionProtocol(this.getrEncryptionProtocol());
        form.setAuthenticationProtocol(this.getrAuthenticationProtocol());
        form.setProxyServer(this.getrProxyServer());
        form.setType(EmailAccountSetting.Type.RECEIVE);
        return form;
    }

    public SendAccountForm getSendAccountForm(){
        SendAccountForm form = new SendAccountForm();
        form.setUserName(this.getsUserName());
        form.setPassword(this.getsPassword());
        form.setMailServerPort(this.getsMailServerPort());
        form.setMailProtocol(this.getsMailProtocol());
        form.setMailServerAddress(this.getsMailServerAddress());
        form.setEncryptionProtocol(this.getsEncryptionProtocol());
        form.setAuthenticationProtocol(this.getsAuthenticationProtocol());
        form.setProxyServer(this.getsProxyServer());
        form.setType(EmailAccountSetting.Type.SEND);
        form.setCc(this.getsCC());
        return form;
    }


}
