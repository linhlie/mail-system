package io.owslab.mailreceiver.form;

import io.owslab.mailreceiver.model.EmailAccount;

/**
 * Created by khanhlvb on 3/27/18.
 */
public class MailAccountForm {
    private String account;
    private boolean disabled;

    public MailAccountForm(String account, boolean disabled) {
        this.account = account;
        this.disabled = disabled;
    }

    public MailAccountForm() {
    }

    public MailAccountForm(EmailAccount emailAccount) {
        this.account = emailAccount.getAccount();
        this.disabled = emailAccount.isDisabled();
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
}
