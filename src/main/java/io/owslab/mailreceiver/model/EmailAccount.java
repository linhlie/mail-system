package io.owslab.mailreceiver.model;

import io.owslab.mailreceiver.form.MailAccountForm;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

@Entity
@Table(name = "Email_Accounts")
public class EmailAccount implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    private String account;

    private boolean disabled;

    private boolean alertSend;

    private String signature;

    public EmailAccount() {}

    public EmailAccount(long id) {
        this.id = id;
    }

    public EmailAccount(String account, boolean disabled, boolean alertSend, String signature) {
        this.account = account;
        this.disabled = disabled;
        this.alertSend = alertSend;
        this.signature = signature;
    }

    public EmailAccount(MailAccountForm mailAccountForm) {
        this.account = mailAccountForm.getAccount();
        this.disabled = mailAccountForm.isDisabled();
        this.signature = mailAccountForm.getSignature();
        this.alertSend = mailAccountForm.isAlertSend();
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
}
