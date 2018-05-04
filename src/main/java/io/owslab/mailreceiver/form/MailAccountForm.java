package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccount;

/**
 * Created by khanhlvb on 3/27/18.
 */
public class MailAccountForm {
    private String account;
    private boolean disabled;
    private String signature;

    public MailAccountForm(String account, boolean disabled, String signature) {
        this.account = account;
        this.disabled = disabled;
        this.signature = signature;
    }

    public MailAccountForm() {
    }

    public MailAccountForm(EmailAccount emailAccount) {
        this.account = emailAccount.getAccount();
        this.disabled = emailAccount.isDisabled();
        this.signature = emailAccount.getSignature();
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
}
