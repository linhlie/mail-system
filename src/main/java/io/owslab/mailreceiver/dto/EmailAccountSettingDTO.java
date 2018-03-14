package io.owslab.mailreceiver.dto;

import io.owslab.mailreceiver.model.EmailAccountSetting;

/**
 * Created by khanhlvb on 3/14/18.
 */
public class EmailAccountSettingDTO {
    private long id;

    private String account;

    private String userName;

    private boolean disabled;

    private int type;

    public EmailAccountSettingDTO(EmailAccountSetting account) {
        this.id = account.getId();
        this.account = account.getAccount();
        this.userName = account.getUserName();
        this.disabled = account.isDisabled();
        this.type = account.getType();
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
}
