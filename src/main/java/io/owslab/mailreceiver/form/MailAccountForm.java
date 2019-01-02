package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccount;

/**
 * Created by khanhlvb on 3/27/18.
 */
public class MailAccountForm {
    private String account;
    private boolean disabled;
    private String signature;
    private boolean alertSend;
    private String inChargeCompany;

    public MailAccountForm(String account, boolean disabled, String signature, boolean alertSend, String inChargeCompany) {
        this.account = account;
        this.disabled = disabled;
        this.signature = signature;
        this.alertSend = alertSend;
        this.inChargeCompany = inChargeCompany;
    }

    public MailAccountForm() {
    }

    public MailAccountForm(EmailAccount emailAccount) {
        this.account = emailAccount.getAccount();
        this.disabled = emailAccount.isDisabled();
        this.signature = emailAccount.getSignature();
        this.alertSend = emailAccount.isAlertSend();
        this.inChargeCompany = emailAccount.getInChargeCompany();
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

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public boolean isAlertSend() {
        return alertSend;
    }

    public void setAlertSend(boolean alertSend) {
        this.alertSend = alertSend;
    }

    public String getInChargeCompany() {
        return inChargeCompany;
    }

    public void setInChargeCompany(String inChargeCompany) {
        this.inChargeCompany = inChargeCompany;
    }
}
