package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.EmailAccount;
import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 3/14/18.
 */
public class EmailAccountDTO {
    private long id;

    private String account;

    private boolean disabled;

    public EmailAccountDTO(EmailAccount account) {
        this.id = account.getId();
        this.account = account.getAccount();
        this.disabled = account.isDisabled();
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
}
